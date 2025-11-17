package com.elearning.backend.dto;

import lombok.Data;

@Data
public class SignalMessage {
    private String sender;
    private String type;
    private String sdp;
}
