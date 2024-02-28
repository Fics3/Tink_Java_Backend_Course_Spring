package edu.java.configuration;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties("app.scheduler")
public class LinkUpdaterSchedulerConfig {
    private Duration interval;
}
