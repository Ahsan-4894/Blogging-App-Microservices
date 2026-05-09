package com.example.postservice.controller;

import com.example.postservice.dto.CreatePostRequestDto;
import com.example.postservice.dto.PostResponseDto;
import com.example.postservice.service.PostService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponseDto> createPost(
            @Valid @RequestBody CreatePostRequestDto dto,
            @RequestHeader("X-Auth-UserId") String userId) throws Exception {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(postService.createPost(dto, userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDto> getPostById(@PathVariable String id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @PostMapping("/like/{postId}")
    public ResponseEntity<PostResponseDto> likePost(
            @PathVariable String postId,
            @RequestHeader("X-Auth-UserId") String userId) {
        return ResponseEntity.ok(postService.likePost(postId, userId));
    }

    @DeleteMapping("/like/{postId}")
    public ResponseEntity<PostResponseDto> unlikePost(
            @PathVariable String postId,
            @RequestHeader("X-Auth-UserId") String userId) {
        return ResponseEntity.ok(postService.unlikePost(postId, userId));
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<PostResponseDto>> getPostsByUser(@PathVariable String userId) {
        return ResponseEntity.ok(postService.getPostsByUserId(userId));
    }

    @PostMapping("/batch")
    public ResponseEntity<List<PostResponseDto>> getPostsByIds(@RequestBody List<String> postIds) {
        return ResponseEntity.ok(postService.getPostsByIds(postIds));
    }
}
