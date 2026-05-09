package com.example.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserRegisterResponseDto {
    private boolean ok;
    private String message;
}
