package com.example.userservice.entity;


import com.example.userservice.entity.type.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users", indexes = { @Index(name = "idx_username", columnList = "username") })
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String username;
    private String email;

    @Column(columnDefinition = "text")
    private String bio;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;



    @OneToMany(mappedBy = "follower")
    private List<Follow> following;

    @OneToMany(mappedBy = "following")
    private List<Follow> followers;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
