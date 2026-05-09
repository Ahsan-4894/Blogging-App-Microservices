package com.example.postservice.exception;

public class PostAlreadyLikedException extends RuntimeException {
    public PostAlreadyLikedException(String postId) {
        super("Post already liked: " + postId);
    }
}
