package com.example.feedservice.controller;

import com.example.feedservice.dto.PostResponseDto;
import com.example.feedservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @GetMapping
    public ResponseEntity<List<PostResponseDto>> getFeed(@RequestHeader("X-Auth-UserId") String userId) {
        return ResponseEntity.ok(feedService.getFeedPostIds(userId));
    }
}
