package com.example.postservice.client;

import com.example.aiserviceproto.AiServiceGrpc;
import com.example.aiserviceproto.GenerateTagsRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiServiceClient {

    private final AiServiceGrpc.AiServiceBlockingStub aiServiceBlockingStub;
    private final ObjectMapper objectMapper;

    @Retry(name = "aiServiceGrpc")
    @CircuitBreaker(name = "aiServiceGrpc", fallbackMethod = "generateTagsFallback")
    public List<String> generateTags(String content) throws Exception {
        GenerateTagsRequest request = GenerateTagsRequest.newBuilder()
                .setContent(content)
                .build();
        String tagsJson = aiServiceBlockingStub.generateTags(request).getTags();
        return objectMapper.readValue(tagsJson, new TypeReference<>() {});
    }

    public List<String> generateTagsFallback(String content, Throwable t) {
        log.warn("AI service unavailable after retries, using empty tags: {}", t.getMessage());
        return List.of();
    }
}
