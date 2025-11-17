package com.elearning.backend.controller;

import com.elearning.backend.dto.ChatMessage;
import com.elearning.backend.dto.SignalMessage;
import com.elearning.backend.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class CallController {

    private final SimpMessagingTemplate messagingTemplate;
    private final SecurityUtils securityUtils;

    // ---------------------------------------------------------------------------
    // 1️⃣ Handles WebRTC signaling messages (offer/answer/ICE)
    // ---------------------------------------------------------------------------

    @MessageMapping("/signal/user/{recipientEmail}")
    public void sendSignalingMessage(
            @DestinationVariable String recipientEmail,
            @Payload SignalMessage message,
            Principal principal,
            SimpMessageHeaderAccessor headerAccessor) {

        // --- Retrieve authenticated sender identity safely ---
        String senderEmail = null;

        // Case 1: Principal provided directly (ideal)
        if (principal != null) {
            senderEmail = principal.getName();
        }
        // Case 2: Fallback to SecurityContext
        else {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                senderEmail = auth.getName();
            }
        }
        // Case 3: Fallback to STOMP session attributes (from interceptor)
        if (senderEmail == null && headerAccessor.getSessionAttributes() != null) {
            Object sessionUser = headerAccessor.getSessionAttributes().get("user");
            if (sessionUser instanceof UsernamePasswordAuthenticationToken token) {
                senderEmail = ((org.springframework.security.core.userdetails.User)
                        token.getPrincipal()).getUsername();
            }
        }

        // --- Security guard ---
        if (senderEmail == null) {
            System.err.println("❌ SECURITY ERROR: Unauthenticated signaling message received.");
            return;
        }

        // --- Enrich message & forward ---
        message.setSender(senderEmail);
        messagingTemplate.convertAndSendToUser(recipientEmail, "/queue/signal", message);

        System.out.println("✅ Signal sent from " + senderEmail + " to " + recipientEmail);
    }

    // ---------------------------------------------------------------------------
    // 2️⃣ Handles text chat messages during a call
    // ---------------------------------------------------------------------------

    @MessageMapping("/chat/topic")
    public void sendChatMessage(@Payload ChatMessage message, Principal principal) {
        String senderEmail = null;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            senderEmail = auth.getName();
        } else if (principal != null) {
            senderEmail = principal.getName();
        }

        if (senderEmail == null) senderEmail = "Anonymous";

        message.setSender(senderEmail);
        messagingTemplate.convertAndSend("/topic/chat", message);

        System.out.println("💬 Chat message from " + senderEmail + ": " + message.getContent());
    }
}
