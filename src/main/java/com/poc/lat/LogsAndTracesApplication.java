package com.poc.lat;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.poc.lat.httpclient.LogsAndTracesHttpClientInterceptor;

@RestController
@SpringBootApplication
public class LogsAndTracesApplication
{

    @Autowired
    RestTemplate restTemplate;

    @RequestMapping(value = "/api-a")
    public String apia()
    {
        restTemplate.getForEntity("http://localhost:8090/api-b", String.class);

        return "API A";
    }

    @RequestMapping(value = "/api-b")
    public String apib()
    {
        return "API B";
    }

    @RequestMapping(value = "/api-c")
    public void apic()
    {
        throw new RuntimeException("Runtime error!");
    }

    @Bean
    public RestTemplate restTemplate()
    {
        return new RestTemplate()
        {
            {
                setInterceptors(Collections.singletonList(new LogsAndTracesHttpClientInterceptor()));
            }
        };
    }

    public static void main(String[] args)
    {
        SpringApplication.run(LogsAndTracesApplication.class, args);
    }

}