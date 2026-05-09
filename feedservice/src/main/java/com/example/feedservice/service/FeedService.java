package com.example.feedservice.service;

import com.example.feedservice.client.PostServiceClient;
import com.example.feedservice.client.UserServiceClient;
import com.example.feedservice.dto.PostCreatedEvent;
import com.example.feedservice.dto.PostResponseDto;
import com.example.feedservice.entity.Feed;
import com.example.feedservice.repository.FeedRepository;
import com.example.userserviceproto.GetFollowerIdsRequest;
import com.example.userserviceproto.UserServiceGrpc;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {

    private final UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;
    private final FeedRepository feedRepository;
    private final PostServiceClient postServiceClient;
    private final UserServiceClient userServiceClient;

    @Retry(name = "userServiceGrpc")
    @CircuitBreaker(name = "userServiceGrpc", fallbackMethod = "prepareFeedsFallback")
    public void prepareFeeds(PostCreatedEvent event) {
        List<String> followerIds = userServiceBlockingStub
                .getFollowerIds(GetFollowerIdsRequest.newBuilder()
                        .setUserId(event.getUserId())
                        .build())
                .getIdsList();

        log.info("Author {} has {} followers: {}", event.getUserId(), followerIds.size(), followerIds);

        List<Feed> feedRows = followerIds.stream()
                .map(followerId -> Feed.builder()
                        .ownerUserId(followerId)
                        .postId(event.getId())
                        .authorId(event.getUserId())
                        .createdAt(event.getCreatedAt())
                        .build())
                .toList();

        feedRepository.saveAll(feedRows);
        log.info("Inserted {} feed rows for postId={}", feedRows.size(), event.getId());
    }

    public void prepareFeedsFallback(PostCreatedEvent event, Throwable t) {
        log.error("User-service unavailable after retries, skipping feed preparation for postId={}, userId={}: {}",
                event.getId(), event.getUserId(), t.getMessage());
        throw new RuntimeException("User-service unavailable, feed preparation deferred", t);
    }

    public List<PostResponseDto> getFeedPostIds(String userId) {
        List<String> postIds = feedRepository.findByOwnerUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(Feed::getPostId)
                .toList();
        log.info("Feed for userId={} → fetching {} posts from postservice", userId, postIds.size());
        List<PostResponseDto> posts = postServiceClient.getPostsByIds(postIds);

        if (!posts.isEmpty()) {
            List<String> authorIds = posts.stream().map(PostResponseDto::getUserId).distinct().toList();
            Map<String, String> usernameMap = userServiceClient.getUsernames(authorIds);
            posts.forEach(p -> p.setUsername(usernameMap.get(p.getUserId())));
        }

        log.info("Returning {} posts for userId={}", posts.size(), userId);
        return posts;
    }
}
