package com.example.apigateway.security;

import com.example.apigateway.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtSecurityContextRepository implements ServerSecurityContextRepository {

    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return Mono.empty(); // stateless — nothing to persist
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        HttpCookie cookie = exchange.getRequest().getCookies().getFirst("access_token");
        if (cookie == null) return Mono.empty();

        String token = cookie.getValue();
        try {
            String username = jwtUtil.extractUsername(token);
            if (username != null && !jwtUtil.isTokenExpired(token)) {
                String role = jwtUtil.extractRole(token);
                String userId = jwtUtil.extractUserId(token);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        username, null, List.of(new SimpleGrantedAuthority(role))
                );
                auth.setDetails(userId);
                return Mono.just(new SecurityContextImpl(auth));
            }
        } catch (Exception ignored) {
            // malformed or expired token — fall through to empty
        }
        return Mono.empty();
    }
}
