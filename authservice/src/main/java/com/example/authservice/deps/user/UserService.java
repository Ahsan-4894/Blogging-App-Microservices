package com.example.authservice.deps.user;


import com.example.authservice.dto.UserLoginRequestDto;
import com.example.authservice.dto.UserRegisterRequestDto;
import com.example.authservice.exception.BadRequestException;
import com.example.authservice.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService  {
    private final WebClient userServiceWebClient;

    public Mono<User> loginUser(UserLoginRequestDto userDto) {
        log.info("Calling User Login for user : {}", userDto);
        return userServiceWebClient.post()
                .uri("/api/v1/users/loginUser")
                .bodyValue(userDto)
                .retrieve()
                .bodyToMono(User.class)
                .onErrorResume(WebClientResponseException.class, e -> {
                    if (e.getStatusCode() == HttpStatus.NOT_FOUND)
                        return Mono.error(new UserNotFoundException("User Not Found: " + userDto.getUsername()));
                    else if (e.getStatusCode() == HttpStatus.BAD_REQUEST)
                        return Mono.error(new BadRequestException("Invalid Request: " + userDto.getUsername()));
                    return Mono.error(new RuntimeException("Unexpected error: " + e.getMessage()));
                });
    }

    public Mono<Boolean> registerUser(UserRegisterRequestDto user) {
        log.info("Calling User Register for user : {}", user.getUsername());
        return userServiceWebClient.post()
                .uri("/api/v1/users/registerUser")
                .bodyValue(user)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume(WebClientResponseException.class, e -> {
                    if (e.getStatusCode() == HttpStatus.CONFLICT)
                        return Mono.error(new BadRequestException("Username or email already exists: " + user.getUsername()));
                    else if (e.getStatusCode() == HttpStatus.BAD_REQUEST)
                        return Mono.error(new BadRequestException("Invalid Request: " + user.getUsername()));
                    return Mono.error(new RuntimeException("Unexpected error: " + e.getMessage()));
                });
    }

}
