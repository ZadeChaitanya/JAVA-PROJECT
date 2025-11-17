package com.elearning.backend.security.handshake;

import com.elearning.backend.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class HttpAuthHandshakeHandler extends DefaultHandshakeHandler {

    private final JwtService jwtService;

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        try {
            URI uri = request.getURI();
            String query = uri.getQuery();

            if (query != null && query.contains("token=")) {
                String token = query.split("token=")[1];
                if (jwtService.validateToken(token)) {
                    String username = jwtService.extractUsername(token);
                    System.out.println("✅ WebSocket Authenticated User via Handshake: " + username);
                    return () -> username;
                } else {
                    System.out.println("❌ Invalid JWT during WebSocket handshake");
                }
            } else {
                System.out.println("⚠️ No JWT token in WebSocket query params");
            }
        } catch (Exception e) {
            System.out.println("⚠️ WebSocket token validation failed: " + e.getMessage());
        }

        return null; // unauthenticated
    }
}
