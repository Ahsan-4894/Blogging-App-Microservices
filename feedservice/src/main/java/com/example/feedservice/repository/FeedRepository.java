package com.example.feedservice.repository;

import com.example.feedservice.entity.Feed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedRepository extends JpaRepository<Feed, String> {

    List<Feed> findByOwnerUserIdOrderByCreatedAtDesc(String ownerUserId);

    Page<Feed> findByOwnerUserIdOrderByCreatedAtDesc(String ownerUserId, Pageable pageable);
}
