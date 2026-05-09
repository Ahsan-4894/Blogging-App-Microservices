package com.example.postservice.service;

import com.example.postservice.dto.PostResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class IndexerQueuePublisher {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.indexer-queue-url}")
    private String indexerQueueUrl;

    public void publish(PostResponseDto post) {
        try {
            String body = objectMapper.writeValueAsString(post);
            sqsClient.sendMessage(SendMessageRequest.builder()
                    .queueUrl(indexerQueueUrl)
                    .messageBody(body)
                    .build());
            log.info("Published post {} to indexer queue", post.getId());
        } catch (Exception e) {
            log.error("Failed to publish post {} to indexer queue: {}", post.getId(), e.getMessage());
        }
    }
}
