package edu.java.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private final ClientConfig clientConfig;

}
