package com.example.notificationservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class PostCreatedEvent {
    private String id;
    private String userId;
    private String title;
    private String content;
    private List<String> tags;
    private long likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
