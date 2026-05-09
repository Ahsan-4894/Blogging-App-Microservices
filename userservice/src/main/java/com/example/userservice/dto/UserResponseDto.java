package com.example.userservice.dto;

import com.example.userservice.entity.type.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponseDto {
    private String id;
    private String username;
    private String email;
    private UserRole role;
    private String bio;
}
