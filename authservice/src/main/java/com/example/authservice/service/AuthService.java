package com.example.authservice.service;

import com.example.authservice.deps.user.UserService;
import com.example.authservice.dto.UserLoginRequestDto;
import com.example.authservice.dto.UserLoginResponseDto;
import com.example.authservice.dto.UserRegisterRequestDto;
import com.example.authservice.dto.UserRegisterResponseDto;
import com.example.authservice.exception.BadRequestException;
import com.example.authservice.deps.user.User;
import com.example.authservice.util.CookieUtil;
import com.example.authservice.util.JwtAuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtAuthUtil jwtAuthUtil;
    private final CookieUtil cookieUtil;

    private static final int ACCESS_TOKEN_TTL = 60 * 15; // 15 minutes in seconds

    public Mono<UserLoginResponseDto> login(UserLoginRequestDto dto, ServerHttpResponse response) {
        return userService.loginUser(dto)
                .flatMap(user -> {
                    String role = user.getRole() != null && !user.getRole().startsWith("ROLE_")
                            ? "ROLE_" + user.getRole()
                            : user.getRole();
                    User tokenUser = new User(user.getId(), user.getUsername(), role);
                    String token = jwtAuthUtil.generateAccessToken(tokenUser);
                    cookieUtil.setCookieInTheBrowser(response, "access_token", token, ACCESS_TOKEN_TTL);
                    return Mono.just(new UserLoginResponseDto(true, "Login successful"));
                });
    }

    public Mono<UserRegisterResponseDto> register(UserRegisterRequestDto dto, ServerHttpResponse response) {
        return userService.registerUser(dto)
                .flatMap(success -> {
                    if (!success) return Mono.error(new BadRequestException("Registration failed"));
                    UserLoginRequestDto loginDto = new UserLoginRequestDto();
                    loginDto.setUsername(dto.getUsername());
                    loginDto.setPassword(dto.getPassword());
                    return userService.loginUser(loginDto);
                })
                .flatMap(user -> {
                    String role = user.getRole() != null && !user.getRole().startsWith("ROLE_")
                            ? "ROLE_" + user.getRole()
                            : user.getRole();
                    User tokenUser = new User(user.getId(), user.getUsername(), role);
                    String token = jwtAuthUtil.generateAccessToken(tokenUser);
                    cookieUtil.setCookieInTheBrowser(response, "access_token", token, ACCESS_TOKEN_TTL);
                    return Mono.just(new UserRegisterResponseDto(true, "Registration successful"));
                });
    }

    public Mono<Void> logout(ServerHttpResponse response) {
        cookieUtil.deleteCookieFromBrowser(response, "access_token");
        return Mono.empty();
    }
}
