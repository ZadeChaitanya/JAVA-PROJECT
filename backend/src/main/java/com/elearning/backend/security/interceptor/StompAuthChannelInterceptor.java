package com.elearning.backend.security.interceptor;

import com.elearning.backend.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String username = jwtService.extractUsername(token);

                if (jwtService.validateToken(token)) {
                    var principal = new User(username, "", java.util.List.of());
                    var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    accessor.setUser(auth);
                    System.out.println("✅ WebSocket authenticated for user: " + username);
                } else {
                    System.out.println("❌ Invalid JWT in STOMP CONNECT frame");
                }
            } else {
                System.out.println("⚠️ No Authorization header found in STOMP CONNECT");
            }
            System.out.println("👉 STOMP Command: " + accessor.getCommand());
            System.out.println("👉 Header Authorization: " + authHeader);
            System.out.println("👉 Principal set: " + accessor.getUser());

        }
        return message;
    }

}