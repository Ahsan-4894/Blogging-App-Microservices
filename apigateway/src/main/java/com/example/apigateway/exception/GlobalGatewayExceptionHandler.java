package com.example.apigateway.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
@Order(-2)
@RequiredArgsConstructor
public class GlobalGatewayExceptionHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status;
        String message;

        if (isServiceUnavailable(ex)) {
            status = HttpStatus.SERVICE_UNAVAILABLE;
            message = "Service is temporarily unavailable. Please try again later.";
            log.warn("Service unavailable: {}", ex.getMessage());
        } else if (isTimeout(ex)) {
            status = HttpStatus.GATEWAY_TIMEOUT;
            message = "Request timed out. Please try again.";
            log.warn("Gateway timeout: {}", ex.getMessage());
        } else if (ex instanceof ResponseStatusException rse) {
            status = HttpStatus.valueOf(rse.getStatusCode().value());
            message = rse.getReason() != null ? rse.getReason() : status.getReasonPhrase();
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message = "An unexpected error occurred.";
            log.error("Unhandled gateway exception", ex);
        }

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        byte[] bytes = toJson(Map.of(
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", message
        ));

        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    private boolean isServiceUnavailable(Throwable ex) {
        if (ex instanceof ConnectException) return true;
        if (ex != null && ex.getMessage() != null &&
                (ex.getMessage().contains("No servers available") ||
                 ex.getMessage().contains("Unable to find instance"))) return true;
        return ex != null && ex.getCause() != null && isServiceUnavailable(ex.getCause());
    }

    private boolean isTimeout(Throwable ex) {
        if (ex instanceof TimeoutException) return true;
        return ex != null && ex.getCause() instanceof TimeoutException;
    }

    private byte[] toJson(Object obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            return "{\"error\":\"Internal Server Error\"}".getBytes(StandardCharsets.UTF_8);
        }
    }
}
