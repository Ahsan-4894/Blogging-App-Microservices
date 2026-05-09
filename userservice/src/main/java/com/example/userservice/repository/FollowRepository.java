package com.example.userservice.repository;

import com.example.userservice.entity.Follow;
import com.example.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, String> {

    @Query("SELECT f.follower.email FROM Follow f WHERE f.following.id = :userId")
    List<String> findFollowerEmailsByUserId(@Param("userId") String userId);

    @Query("SELECT f.follower.id FROM Follow f WHERE f.following.id = :userId")
    List<String> findFollowerIdsByUserId(@Param("userId") String userId);

    @Query("SELECT f.follower FROM Follow f WHERE f.following.id = :userId")
    List<User> findFollowersByFollowingId(@Param("userId") String userId);

    @Query("SELECT f.following FROM Follow f WHERE f.follower.id = :userId")
    List<User> findFollowingByFollowerId(@Param("userId") String userId);

    boolean existsByFollowerIdAndFollowingId(String followerId, String followingId);

    long countByFollowingId(String followingId);

    long countByFollowerId(String followerId);

    void deleteByFollowerIdAndFollowingId(String followerId, String followingId);
}
