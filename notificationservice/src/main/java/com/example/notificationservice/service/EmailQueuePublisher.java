package com.example.notificationservice.service;

import com.example.notificationservice.dto.EmailNotificationDto;
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
public class EmailQueuePublisher {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.email-queue-url}")
    private String emailQueueUrl;

    public void publish(EmailNotificationDto dto) {
        try {
            String body = objectMapper.writeValueAsString(dto);
            sqsClient.sendMessage(SendMessageRequest.builder()
                    .queueUrl(emailQueueUrl)
                    .messageBody(body)
                    .build());
            log.info("Queued email notification to {}", dto.getTo());
        } catch (Exception e) {
            log.error("Failed to queue email notification to {}: {}", dto.getTo(), e.getMessage());
        }
    }
}
