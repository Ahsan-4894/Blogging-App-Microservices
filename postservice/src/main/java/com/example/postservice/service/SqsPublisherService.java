package com.example.postservice.service;

import com.example.postservice.dto.PostResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class SqsPublisherService {

    private final SnsClient snsClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.sns.post-created-topic-arn}")
    private String topicArn;

    public void publishPostCreated(PostResponseDto post) {
        try {
            String message = objectMapper.writeValueAsString(post);
            snsClient.publish(PublishRequest.builder()
                    .topicArn(topicArn)
                    .message(message)
                    .build());
            log.info("Published post {} to SNS", post.getId());
        } catch (Exception e) {
            log.error("Failed to publish post {} to SNS: {}", post.getId(), e.getMessage());
        }
    }
}
