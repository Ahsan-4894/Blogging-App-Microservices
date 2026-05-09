package com.example.postservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class PostResponseDto {
    private String id;
    private String userId;
    private String title;
    private String content;
    private List<String> tags;
    private long likeCount;
    private Boolean likedByMe;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
