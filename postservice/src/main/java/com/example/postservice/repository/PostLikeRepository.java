package com.example.postservice.repository;

import com.example.postservice.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, String> {
    long countByPostId(String postId);
    boolean existsByPostIdAndUserId(String postId, String userId);
    void deleteByPostIdAndUserId(String postId, String userId);
}
