package com.example.searchservice.config;

import org.apache.http.message.BasicHeader;
import org.springframework.boot.autoconfigure.elasticsearch.RestClientBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {

    @Bean
    public RestClientBuilderCustomizer restClientBuilderCustomizer() {
        return builder -> builder.setDefaultHeaders(new org.apache.http.Header[]{
                new BasicHeader("Accept", "application/json"),
                new BasicHeader("Content-Type", "application/json")
        });
    }
}
