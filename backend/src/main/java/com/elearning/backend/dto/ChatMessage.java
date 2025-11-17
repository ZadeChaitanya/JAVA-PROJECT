package com.elearning.backend.dto; // <-- CORRECT PACKAGE

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// This DTO defines the structure for messages sent and received over the WebSocket.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    private String sender;
    private String content;

    // Note: If you want to use this for general chat during a call, you might
    // also want to add the ID of the lecture/room:
    // private String roomId;
}