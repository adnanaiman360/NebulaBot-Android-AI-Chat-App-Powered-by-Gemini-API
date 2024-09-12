package com.example.chatapp;

public class Message {
    private String message;
    private boolean isSent;
    private long timestamp;

    public Message(String message, boolean isSent, long timestamp) {
        this.message = message;
        this.isSent = isSent;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSent() {
        return isSent;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
