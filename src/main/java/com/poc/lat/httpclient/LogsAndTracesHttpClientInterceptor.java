package com.poc.lat.httpclient;

import java.io.IOException;

import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class LogsAndTracesHttpClientInterceptor implements ClientHttpRequestInterceptor
{
    private static final String X_CORRELATION_ID = "x-correlation-id";

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException
    {
        HttpHeaders headers = request.getHeaders();

        headers.add(X_CORRELATION_ID, MDC.get(X_CORRELATION_ID));

        return execution.execute(request, body);
    }
}
