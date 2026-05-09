package com.example.authservice.controller;

import com.example.authservice.dto.UserLoginRequestDto;
import com.example.authservice.dto.UserLoginResponseDto;
import com.example.authservice.dto.UserRegisterRequestDto;
import com.example.authservice.dto.UserRegisterResponseDto;
import com.example.authservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Mono<ResponseEntity<UserLoginResponseDto>> login(@Valid @RequestBody UserLoginRequestDto dto,
                                                           ServerHttpResponse response) {
        return authService.login(dto, response)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<UserRegisterResponseDto>> register(@Valid @RequestBody UserRegisterRequestDto dto,
                                                                  ServerHttpResponse response) {
        return authService.register(dto, response)
                .map(result -> ResponseEntity.status(HttpStatus.CREATED).body(result));
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<Void>> logout(ServerHttpResponse response) {
        return authService.logout(response)
                .then(Mono.just(ResponseEntity.<Void>noContent().build()));
    }
}
