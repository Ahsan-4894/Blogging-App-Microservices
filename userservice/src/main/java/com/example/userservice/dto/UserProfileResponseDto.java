package com.example.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserProfileResponseDto {
    private String id;
    private String username;
    private String bio;
    private long followerCount;
    private long followingCount;
    private Boolean isFollowing;
}
