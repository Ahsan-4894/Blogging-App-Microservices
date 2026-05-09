package com.example.feedservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostResponseDto {
    private String id;
    private String userId;
    private String username;
    private String title;
    private String content;
    private List<String> tags;
    private long likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void setUsername(String username) {
        this.username = username;
    }
}
