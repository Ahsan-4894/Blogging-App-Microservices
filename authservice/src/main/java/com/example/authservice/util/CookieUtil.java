package com.example.authservice.util;

import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    public void setCookieInTheBrowser(ServerHttpResponse response, String cookieName, String token, int ttl) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, token)
                .httpOnly(true)
                .path("/")
                .maxAge(ttl)
                .sameSite("Lax")
                .build();
        response.addCookie(cookie);
    }

    public void deleteCookieFromBrowser(ServerHttpResponse response, String cookieName) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        response.addCookie(cookie);
    }
}
