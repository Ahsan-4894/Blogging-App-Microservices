package com.example.feedservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "feed", indexes = {
        @Index(name = "idx_feed_owner_created", columnList = "ownerUserId, createdAt")
})
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Feed {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String ownerUserId;

    @Column(nullable = false)
    private String postId;

    @Column(nullable = false)
    private String authorId;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
