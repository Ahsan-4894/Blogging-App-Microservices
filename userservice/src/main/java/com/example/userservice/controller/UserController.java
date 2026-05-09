package com.example.userservice.controller;

import com.example.userservice.dto.UpdateProfileRequestDto;
import com.example.userservice.dto.UserLoginRequestDto;
import com.example.userservice.dto.UserProfileResponseDto;
import com.example.userservice.dto.UserRegisterRequestDto;
import com.example.userservice.dto.UserResponseDto;
import com.example.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/validate/{userId}")
    public ResponseEntity<Boolean> validateUser(@RequestParam String userId) {
        return ResponseEntity.ok(userService.validateUser(userId));
    }

    @PostMapping("/batch-usernames")
    public ResponseEntity<java.util.Map<String, String>> getBatchUsernames(@RequestBody List<String> userIds) {
        return ResponseEntity.ok(userService.getUsernamesByIds(userIds));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> me(@RequestHeader("X-Auth-Username") String username) {
        return ResponseEntity.ok(userService.getMe(username));
    }

    @PutMapping("/me")
    public ResponseEntity<Void> updateProfile(
            @RequestHeader("X-Auth-Username") String username,
            @RequestBody UpdateProfileRequestDto dto) {
        userService.updateProfile(username, dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponseDto> getUserById(
            @PathVariable String userId,
            @RequestHeader(value = "X-Auth-UserId", required = false) String requestingUserId) {
        return ResponseEntity.ok(userService.getUserById(userId, requestingUserId));
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<UserProfileResponseDto>> getFollowers(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getFollowers(userId));
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<List<UserProfileResponseDto>> getFollowing(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getFollowing(userId));
    }

    @PostMapping("/loginUser")
    public ResponseEntity<UserResponseDto> loginUser(@Valid @RequestBody UserLoginRequestDto dto) {
        return ResponseEntity.ok(userService.loginUserInternally(dto));
    }

    @PostMapping("/registerUser")
    public ResponseEntity<Boolean> registerUser(@Valid @RequestBody UserRegisterRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerUserInternally(dto));
    }

    @PostMapping("/{userId}/follow")
    public ResponseEntity<Void> followUser(
            @PathVariable String userId,
            @RequestHeader("X-Auth-UserId") String followerId) {
        userService.followUser(followerId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{userId}/follow")
    public ResponseEntity<Void> unfollowUser(
            @PathVariable String userId,
            @RequestHeader("X-Auth-UserId") String followerId) {
        userService.unfollowUser(followerId, userId);
        return ResponseEntity.noContent().build();
    }
}
