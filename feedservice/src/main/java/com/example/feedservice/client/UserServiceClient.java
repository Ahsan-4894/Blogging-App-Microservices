package com.example.feedservice.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserServiceClient {

    private final RestTemplate restTemplate;

    private static final String BATCH_USERNAMES_URL = "http://USER-SERVICE/api/v1/users/batch-usernames";

    @Retry(name = "userServiceRest")
    @CircuitBreaker(name = "userServiceRest", fallbackMethod = "getUsernamesFallback")
    public Map<String, String> getUsernames(List<String> userIds) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                BATCH_USERNAMES_URL,
                HttpMethod.POST,
                new HttpEntity<>(userIds, headers),
                new ParameterizedTypeReference<>() {}
        );

        return response.getBody();
    }

    public Map<String, String> getUsernamesFallback(List<String> userIds, Throwable t) {
        log.error("User-service unavailable, returning empty username map: {}", t.getMessage());
        return Map.of();
    }
}
