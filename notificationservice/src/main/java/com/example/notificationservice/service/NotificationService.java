package com.example.notificationservice.service;

import com.example.notificationservice.dto.EmailNotificationDto;
import com.example.notificationservice.dto.PostCreatedEvent;
import com.example.userserviceproto.GetFollowerEmailsRequest;
import com.example.userserviceproto.UserServiceGrpc;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;
    private final EmailQueuePublisher emailQueuePublisher;

    @Retry(name = "userServiceGrpc")
    @CircuitBreaker(name = "userServiceGrpc", fallbackMethod = "handlePostCreatedFallback")
    public void handlePostCreated(PostCreatedEvent event) {
        List<String> emails = userServiceBlockingStub.getFollowerEmails(
                GetFollowerEmailsRequest.newBuilder()
                        .setUserId(event.getUserId())
                        .build()
        ).getEmailsList();

        log.info("Fanning out {} email notifications for postId={}", emails.size(), event.getId());

        emails.forEach(email -> emailQueuePublisher.publish(
                new EmailNotificationDto(email, event.getId(), event.getTitle(), event.getUserId())
        ));
    }

    public void handlePostCreatedFallback(PostCreatedEvent event, Throwable t) {
        log.error("User-service unavailable after retries, skipping notifications for postId={}, userId={}: {}",
                event.getId(), event.getUserId(), t.getMessage());
        throw new RuntimeException("User-service unavailable, notification deferred", t);
    }
}
