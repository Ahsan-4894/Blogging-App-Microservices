package com.example.feedservice.service;

import com.example.feedservice.dto.PostCreatedEvent;
import com.example.feedservice.dto.SnsNotificationWrapper;
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
public class FeedPreparingListenerService {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final FeedService feedService;

    @Value("${aws.sqs.feed-preparing-queue-url}")
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
                handleEvent(event);
                deleteMessage(message.receiptHandle());
            } catch (Exception e) {
                log.error("Failed to process message {}: {}", message.messageId(), e.getMessage());
            }
        }
    }

    private void handleEvent(PostCreatedEvent event) {
        log.info("=== PostCreatedEvent received ===");
        log.info("postId    : {}", event.getId());
        log.info("authorId  : {}", event.getUserId());
        log.info("title     : {}", event.getTitle());
        log.info("createdAt : {}", event.getCreatedAt());
        log.info("================================");
        feedService.prepareFeeds(event);
    }

    private void deleteMessage(String receiptHandle) {
        sqsClient.deleteMessage(DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(receiptHandle)
                .build());
    }
}
