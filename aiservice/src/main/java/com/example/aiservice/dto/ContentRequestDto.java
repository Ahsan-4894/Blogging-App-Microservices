package com.example.aiservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContentRequestDto {

    @NotBlank(message = "Content must not be blank")
    private String content;
}
