package com.example.feedservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostCreatedEvent {
    private String id;
    private String userId;
    private String title;
    private String content;
    private List<String> tags;
    private LocalDateTime createdAt;
}
