package com.example.aiservice.controller;

import com.example.aiservice.dto.ContentRequestDto;
import com.example.aiservice.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/rephrase")
    public ResponseEntity<String> rephrase(@Valid @RequestBody ContentRequestDto dto) {
        return ResponseEntity.ok(chatService.rephrase(dto.getContent()));
    }

    @PostMapping("/generate_tags")
    public ResponseEntity<String> generateTags(@Valid @RequestBody ContentRequestDto dto) {
        return ResponseEntity.ok(chatService.generateTags(dto.getContent()));
    }
}
