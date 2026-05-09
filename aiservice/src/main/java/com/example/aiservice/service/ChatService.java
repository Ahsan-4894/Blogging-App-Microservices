package com.example.aiservice.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatClient chatClient;

    @Value("classpath:rephrase_prompt.txt")
    private Resource rephrasePromptResource;

    @Value("classpath:generate_tags_prompt.txt")
    private Resource generateTagsPromptResource;

    private String rephrasePrompt;
    private String generateTagsPrompt;

    @PostConstruct
    private void loadPrompts() throws IOException {
        rephrasePrompt = rephrasePromptResource.getContentAsString(StandardCharsets.UTF_8);
        generateTagsPrompt = generateTagsPromptResource.getContentAsString(StandardCharsets.UTF_8);
        log.info("Loaded rephrase and generate_tags system prompts");
    }

    public String rephrase(String content) {
        return chatClient.prompt()
                .system(rephrasePrompt)
                .user(content)
                .call()
                .content();
    }

    public String generateTags(String content) {
        return chatClient.prompt()
                .system(generateTagsPrompt)
                .user(content)
                .call()
                .content();
    }
}
