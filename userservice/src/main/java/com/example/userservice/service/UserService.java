package com.example.userservice.service;

import com.example.userservice.dto.UpdateProfileRequestDto;
import com.example.userservice.dto.UserLoginRequestDto;
import com.example.userservice.dto.UserProfileResponseDto;
import com.example.userservice.dto.UserRegisterRequestDto;
import com.example.userservice.dto.UserResponseDto;
import com.example.userservice.entity.Follow;
import com.example.userservice.entity.User;
import com.example.userservice.entity.type.UserRole;
import com.example.userservice.repository.FollowRepository;
import com.example.userservice.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final FollowRepository followRepository;

    public List<String> getFollowerEmails(String userId) {
        return followRepository.findFollowerEmailsByUserId(userId);
    }

    public List<String> getFollowerIds(String userId) {
        return followRepository.findFollowerIdsByUserId(userId);
    }

    public void followUser(String followerId, String followingId) {
        if (followerId.equals(followingId))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot follow yourself");
        if (followRepository.existsByFollowerIdAndFollowingId(followerId, followingId))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already following this user");

        User follower = userRepo.findById(followerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Follower not found"));
        User following = userRepo.findById(followingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User to follow not found"));

        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowing(following);
        followRepository.save(follow);
    }

    public void unfollowUser(String followerId, String followingId) {
        followRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);
    }

    public Boolean validateUser(String userId) {
        return userRepo.findById(userId).isPresent();
    }

    public java.util.Map<String, String> getUsernamesByIds(List<String> userIds) {
        return userRepo.findAllById(userIds).stream()
                .collect(java.util.stream.Collectors.toMap(User::getId, User::getUsername));
    }

    public UserResponseDto getMe(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return new UserResponseDto(user.getId(), user.getUsername(), user.getEmail(), user.getRole(), user.getBio());
    }

    public UserProfileResponseDto getUserById(String userId, String requestingUserId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        long followerCount = followRepository.countByFollowingId(userId);
        long followingCount = followRepository.countByFollowerId(userId);
        Boolean isFollowing = (requestingUserId != null && !requestingUserId.isBlank())
                ? followRepository.existsByFollowerIdAndFollowingId(requestingUserId, userId)
                : null;
        return new UserProfileResponseDto(user.getId(), user.getUsername(), user.getBio(),
                followerCount, followingCount, isFollowing);
    }

    public void updateProfile(String username, UpdateProfileRequestDto dto) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (dto.getUsername() != null && !dto.getUsername().isBlank()) {
            user.setUsername(dto.getUsername());
        }
        if (dto.getBio() != null) {
            user.setBio(dto.getBio());
        }
        userRepo.save(user);
    }

    public List<UserProfileResponseDto> getFollowers(String userId) {
        return followRepository.findFollowersByFollowingId(userId).stream()
                .map(u -> new UserProfileResponseDto(u.getId(), u.getUsername(), u.getBio(), 0, 0, null))
                .toList();
    }

    public List<UserProfileResponseDto> getFollowing(String userId) {
        return followRepository.findFollowingByFollowerId(userId).stream()
                .map(u -> new UserProfileResponseDto(u.getId(), u.getUsername(), u.getBio(), 0, 0, null))
                .toList();
    }

    public UserResponseDto loginUserInternally(UserLoginRequestDto dto) {
        User user = userRepo.findByUsername(dto.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid credentials");
        }
        return new UserResponseDto(user.getId(), user.getUsername(), user.getEmail(), user.getRole(), user.getBio());
    }

    public Boolean registerUserInternally(UserRegisterRequestDto dto) {
        if (userRepo.existsByUsernameOrEmail(dto.getUsername(), dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username or email already exists");
        }
        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(UserRole.USER)
                .build();
        userRepo.save(user);
        return true;
    }
}
