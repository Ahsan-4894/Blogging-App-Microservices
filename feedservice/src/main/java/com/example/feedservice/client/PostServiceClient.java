package com.example.feedservice.client;

import com.example.feedservice.dto.PostResponseDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostServiceClient {

    private final RestTemplate restTemplate;

    private static final String POST_BATCH_URL = "http://POST-SERVICE/api/v1/posts/batch";

    @Retry(name = "postServiceRest")
    @CircuitBreaker(name = "postServiceRest", fallbackMethod = "getPostsByIdsFallback")
    public List<PostResponseDto> getPostsByIds(List<String> postIds) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<List<PostResponseDto>> response = restTemplate.exchange(
                POST_BATCH_URL,
                HttpMethod.POST,
                new HttpEntity<>(postIds, headers),
                new ParameterizedTypeReference<>() {}
        );

        return response.getBody();
    }

    public List<PostResponseDto> getPostsByIdsFallback(List<String> postIds, Throwable t) {
        log.error("Post-service unavailable after retries, returning empty feed. postIds={}: {}",
                postIds, t.getMessage());
        return List.of();
    }
}
