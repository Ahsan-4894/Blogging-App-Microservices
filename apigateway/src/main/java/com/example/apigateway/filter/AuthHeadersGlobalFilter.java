package com.example.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Order(-1)
public class AuthHeadersGlobalFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(auth -> {
                    if (auth == null || !auth.isAuthenticated()) {
                        return chain.filter(exchange);
                    }
                    String username = auth.getName();
                    String role = auth.getAuthorities().stream()
                            .findFirst()
                            .map(GrantedAuthority::getAuthority)
                            .orElse("");
                    String userId = auth.getDetails() != null ? auth.getDetails().toString() : "";
                    System.out.println("Auth Details" + auth.getDetails());
                    ServerWebExchange mutated = exchange.mutate()
                            .request(r -> r
                                    .header("X-Auth-Username", username)
                                    .header("X-Auth-Role", role)
                                    .header("X-Auth-UserId", userId))
                            .build();
                    return chain.filter(mutated);
                })
                .switchIfEmpty(chain.filter(exchange));
    }
}
