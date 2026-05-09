package com.example.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailNotificationDto {
    private String to;
    private String postId;
    private String postTitle;
    private String authorUserId;
}
