package com.example.postservice.exception;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(String id) {
        super("Post not found: " + id);
    }
}
