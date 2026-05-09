package com.example.searchservice.service;

import com.example.searchservice.document.PostDocument;
import com.example.searchservice.dto.PostIndexDto;
import com.example.searchservice.dto.SnsNotificationWrapper;
import com.example.searchservice.repository.PostSearchRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import org.springframework.beans.factory.annotation.Value;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostIndexerService {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final PostSearchRepository postSearchRepository;

    @Value("${aws.sqs.indexer-queue-url}")
    private String indexerQueueUrl;

    @Scheduled(fixedDelay = 5000)
    public void poll() {
        List<Message> messages = sqsClient.receiveMessage(ReceiveMessageRequest.builder()
                .queueUrl(indexerQueueUrl)
                .maxNumberOfMessages(10)
                .waitTimeSeconds(10)
                .build()).messages();

        for (Message message : messages) {
            try {
                SnsNotificationWrapper wrapper = objectMapper.readValue(message.body(), SnsNotificationWrapper.class);
                PostIndexDto dto = objectMapper.readValue(wrapper.getMessage(), PostIndexDto.class);
                PostDocument doc = PostDocument.builder()
                        .id(dto.getId())
                        .userId(dto.getUserId())
                        .username(dto.getUsername())
                        .title(dto.getTitle())
                        .content(dto.getContent())
                        .tags(dto.getTags())
                        .createdAt(dto.getCreatedAt())
                        .build();
                postSearchRepository.save(doc);
                log.info("Indexed post {} into Elasticsearch", dto.getId());
                deleteMessage(message.receiptHandle());
            } catch (Exception e) {
                log.error("Failed to index message {}: {}", message.messageId(), e.getMessage());
            }
        }
    }

    private void deleteMessage(String receiptHandle) {
        sqsClient.deleteMessage(DeleteMessageRequest.builder()
                .queueUrl(indexerQueueUrl)
                .receiptHandle(receiptHandle)
                .build());
    }
}
