package com.example.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@Order(-1)
public class LoggingWebFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String method = exchange.getRequest().getMethod().name();
        String path = exchange.getRequest().getURI().getPath();
        long start = System.currentTimeMillis();

        log.info("→ {} {}", method, path);

        return chain.filter(exchange)
                .doOnSuccess(v -> log.info("← {} {} {} in {}ms",
                        method, path,
                        exchange.getResponse().getStatusCode(),
                        System.currentTimeMillis() - start))
                .doOnError(ex -> log.error("✗ {} {} threw {} in {}ms: {}",
                        method, path,
                        ex.getClass().getSimpleName(),
                        System.currentTimeMillis() - start,
                        ex.getMessage()));
    }
}
