package com.example.postservice.service;

import com.example.postservice.client.AiServiceClient;
import com.example.postservice.dto.CreatePostRequestDto;
import com.example.postservice.dto.PostResponseDto;
import com.example.postservice.entity.Post;
import com.example.postservice.entity.PostLike;
import com.example.postservice.exception.PostAlreadyLikedException;
import com.example.postservice.exception.PostNotFoundException;
import com.example.postservice.repository.PostLikeRepository;
import com.example.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final AiServiceClient aiServiceClient;
    private final SqsPublisherService sqsPublisherService;
    private final IndexerQueuePublisher indexerQueuePublisher;

    public PostResponseDto createPost(CreatePostRequestDto dto, String userId) throws Exception {
        List<String> tags = aiServiceClient.generateTags(dto.getContent());

        Post post = Post.builder()
                .userId(userId)
                .title(dto.getTitle())
                .content(dto.getContent())
                .tags(tags)
                .build();
        post = postRepository.save(post);
        PostResponseDto response = toDto(post, 0L, null);
        sqsPublisherService.publishPostCreated(response);
        return response;
    }

    public PostResponseDto getPostById(String id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
        long likeCount = postLikeRepository.countByPostId(id);
        return toDto(post, likeCount, null);
    }

    public PostResponseDto likePost(String postId, String userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
        if (postLikeRepository.existsByPostIdAndUserId(postId, userId)) {
            throw new PostAlreadyLikedException(postId);
        }
        postLikeRepository.save(PostLike.builder().postId(postId).userId(userId).build());
        long likeCount = postLikeRepository.countByPostId(postId);
        return toDto(post, likeCount, true);
    }

    public PostResponseDto unlikePost(String postId, String userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
        postLikeRepository.deleteByPostIdAndUserId(postId, userId);
        long likeCount = postLikeRepository.countByPostId(postId);
        return toDto(post, likeCount, false);
    }

    public List<PostResponseDto> getPostsByUserId(String userId) {
        return postRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(post -> toDto(post, postLikeRepository.countByPostId(post.getId()), null))
                .toList();
    }

    public List<PostResponseDto> getPostsByIds(List<String> ids) {
        return postRepository.findAllById(ids).stream()
                .map(post -> toDto(post, postLikeRepository.countByPostId(post.getId()), null))
                .toList();
    }

    private PostResponseDto toDto(Post post, long likeCount, Boolean likedByMe) {
        return new PostResponseDto(
                post.getId(),
                post.getUserId(),
                post.getTitle(),
                post.getContent(),
                post.getTags(),
                likeCount,
                likedByMe,
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
