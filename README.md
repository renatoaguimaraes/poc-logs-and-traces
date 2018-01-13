# poc-logs-and-traces
POC Logs and Traces

Requirements:
- Be able to correlate application logs with application traces.
- Be able to create a map of service calls (direct and indirect) per user request.
- Be able to add dashboards to centralize and cross information upon Logs and
Tracing.
- Prepare your system to be able to analyse data and predict failures.

## Architecture 

ELK (Logstash + Eslastic Search + Kibana)

Http Filter

Http Client

Slf4j

Spring Boot 

## Quick Start

### Configuration

Logstash input configuration file *02-beats-input.conf*.
```
input {
  tcp {
    port => 5044
  }  
}
```
Logstash output configuration file *30-output.conf*.

```
output {
  elasticsearch {
    hosts => ["localhost"]
    manage_template => false
    index => "elk-%{+YYYY.MM.dd}%"
  }
}
```

### Application

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

Build ELK docker image.
 
```shell
cd docker/
docker build --tag elkpoc .
```

Run ELK environment.
```shell
docker run -p 5601:5601 -p 9200:9200 -p 5044:5044 -it --name elk elkpoc
```

