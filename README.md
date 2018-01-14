# Logs and Traces

## The Challenger

In a SOA ecosystem we need to have strong monitoring tools, in this challenge you will design a framework to centralize logs and traces.

Logs contains information about events that happened in an application (errors, warnings and meaningful operational events). On another hand, a trace contains information about function and service calls (Service A called endpoint X in Service B successfully and it took T ms).

Requirements:
- Be able to correlate application logs with application traces.
- Be able to create a map of service calls (direct and indirect) per user request.
- Be able to add dashboards to centralize and cross information upon Logs and
Tracing.
- Prepare your system to be able to analyse data and predict failures.

## The Architecture 

The proposal architecture enable the log centralization and correlation of service calls and application tracing, the solution is very simple, scalable and flexible.

### Components

![Component Diagram](component.png)

### Log and Trace

![Log and Trace](log-and-trace-seq.png)

### Store, Index and Search

![Store, Index and Search](store-index-search-seq.png)

### ELK (Logstash + Eslastic Search + Kibana)

**Store and Index**

Elasticsearch is a distributed, RESTful search and analytics engine capable of solving a growing number of use cases. As the heart of the Elastic Stack, it centrally stores your data so you can discover the expected and uncover the unexpected.

**Collector**

Logstash is an open source, server-side data processing pipeline that ingests data from a multitude of sources simultaneously, transforms it, and then sends it to your favorite “stash.”

**Data Visualization and Monitoring**

Kibana is an open source data visualization plugin for Elasticsearch. It provides visualization capabilities on top of the content indexed on an Elasticsearch cluster. Users can create bar, line and scatter plots, or pie charts and maps on top of large volumes of data.

**Http Filter**

Simple http filter *(javax.servlet.Filter)* used to create, if not exists, x-correlation-id of serives interactions. Responsible to configure MDC - Mapped Diagnostic Context.

**Http Client**

Simple Interceptor used to get x-correlation-id from MDC and propagate along of services call by http client (Spring Rest Template).

**Slf4j and Logback**

Standard logger frameworks.

**Spring Boot** 

Implementation of REST api samples.

## Quick Start

Build ELK docker image.

```shell
cd docker/
docker build --tag elkpoc .
```

Run ELK environment.
```shell
docker run -p 5601:5601 -p 9200:9200 -p 5044:5044 -it --name elk elkpoc
```

Build and run application with api's on http://localhost:8090.

Using Maven:

```shell
mvn clean install
mvn spring-boot:run 
```
Or using Gradle:

```
./gradlew bootRun
```

API's

- /api-a -> /api-b (correlation sample)
- /api-b
- /api-c -> /api-d (correlation sample)
- /api-d (error sample)

### ELK Configuration

Logstash input configuration file *02-beats-input.conf*.
```
input {
  tcp {
    port => 5044
  }  
}
```
Logstash filter and output configuration file *30-output.conf*.

```
filter {
  json {
    source => "message"
  }
}

output {
  elasticsearch {
    hosts => ["localhost"]
    manage_template => false
    index => "elk-%{+YYYY.MM.dd}%"
  }
}
```

### Application Configuration

Logger framework configuration *src/main/resources/logback.xml*.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration debug="false">
  <include resource="org/springframework/boot/logging/logback/base.xml" />
  <appender name="logstash" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
    <destination>localhost:5044</destination>
    <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
      <providers>
        <mdc />
        <context />
        <version />
        <logLevel />
        <loggerName />
        <pattern>
          <pattern>
            {
            "appName": "elk-logs-and-traces",
            "appVersion": "1.0"
            }
          </pattern>
        </pattern>
        <threadName />
        <stackTrace />
      </providers>
    </encoder>
  </appender>
  <root level="INFO">
    <appender-ref ref="CONSOLE" />
    <appender-ref ref="logstash" />
  </root>
  <logger name="org.springframework" level="INFO" />
  <logger name="com.poc.lat" level="INFO" />
</configuration>
```

## POC Results

The proof of concept have success, the solution solved all the requirements. 

![Component Diagram](results.png)

## References  

[Elastic Search - Store and Index](https://www.elastic.co/products/elasticsearch)

[Logstash - Ingestion, Transform and Filter](https://www.elastic.co/products/logstash)

[Kibana - Data Visualization](https://www.elastic.co/products/kibana)

[Docker ELK Stack](https://github.com/spujadas/elk-docker)

[Logback Configuration](https://logback.qos.ch/manual/configuration.html)

[Logstash Logback Encoder](https://github.com/logstash/logstash-logback-encoder)

[Spring Boot Logging](https://docs.spring.io/spring-boot/docs/current/reference/html/howto-logging.html)

