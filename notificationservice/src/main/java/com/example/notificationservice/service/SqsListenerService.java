package com.example.notificationservice.service;

import com.example.notificationservice.dto.PostCreatedEvent;
import com.example.notificationservice.dto.SnsNotificationWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SqsListenerService {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    @Value("${aws.sqs.notification-queue-url}")
    private String queueUrl;

    @Scheduled(fixedDelay = 5000)
    public void poll() {
        List<Message> messages = sqsClient.receiveMessage(ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(10)
                .waitTimeSeconds(10)
                .build()).messages();

        for (Message message : messages) {
            try {
                SnsNotificationWrapper wrapper = objectMapper.readValue(message.body(), SnsNotificationWrapper.class);
                PostCreatedEvent event = objectMapper.readValue(wrapper.getMessage(), PostCreatedEvent.class);
                handlePostCreated(event);
                deleteMessage(message.receiptHandle());
            } catch (Exception e) {
                log.error("Failed to process SQS message {}: {}", message.messageId(), e.getMessage());
            }
        }
    }

    private void handlePostCreated(PostCreatedEvent event) {
        log.info("Received post-created event: postId={}, userId={}, title={}",
                event.getId(), event.getUserId(), event.getTitle());
        notificationService.handlePostCreated(event);
    }

    private void deleteMessage(String receiptHandle) {
        sqsClient.deleteMessage(DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(receiptHandle)
                .build());
    }
}
