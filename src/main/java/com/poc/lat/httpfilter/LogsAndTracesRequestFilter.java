package com.poc.lat.httpfilter;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import ch.qos.logback.classic.ClassicConstants;

@Component
public class LogsAndTracesRequestFilter implements Filter
{
    private static final Logger LOG = LoggerFactory.getLogger(LogsAndTracesRequestFilter.class);

    private static final String REQ_TIME = "req.time";

    private static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";
    private static final String HEADER_USER_AGENT = "User-Agent";
    private static final String HEADER_X_CORRELATION_ID = "x-correlation-id";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        Date start = new Date();
        String uri = null;

        try
        {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;

            uri = httpServletRequest.getRequestURI();

            populateMDC(httpServletRequest);

            chain.doFilter(request, response);

            endRequest(start);

            LOG.info("Success %", uri);
        }
        catch (Exception e)
        {
            endRequest(start);

            LOG.error("Error %", uri);
        }
        finally
        {
            MDC.clear();
        }
    }

    protected void populateMDC(HttpServletRequest httpServletRequest)
    {
        String correlationId = httpServletRequest.getHeader(HEADER_X_CORRELATION_ID);

        if (StringUtils.isEmpty(correlationId))
        {
            correlationId = UUID.randomUUID().toString();
        }

        MDC.put(HEADER_X_CORRELATION_ID, correlationId);

        MDC.put(ClassicConstants.REQUEST_REQUEST_URI, httpServletRequest.getRequestURI());

        StringBuffer requestURL = httpServletRequest.getRequestURL();

        if (requestURL != null)
        {
            MDC.put(ClassicConstants.REQUEST_REQUEST_URL, requestURL.toString());
        }

        MDC.put(ClassicConstants.REQUEST_METHOD, httpServletRequest.getMethod());

        MDC.put(ClassicConstants.REQUEST_QUERY_STRING, httpServletRequest.getQueryString());

        MDC.put(ClassicConstants.REQUEST_USER_AGENT_MDC_KEY, httpServletRequest.getHeader(HEADER_USER_AGENT));

        MDC.put(ClassicConstants.REQUEST_X_FORWARDED_FOR, httpServletRequest.getHeader(HEADER_X_FORWARDED_FOR));
    }

    protected void endRequest(Date start)
    {
        Date end = new Date();

        MDC.put(REQ_TIME, String.valueOf(end.getTime() - start.getTime()));
    }

    @Override
    public void destroy()
    {
    }
}
