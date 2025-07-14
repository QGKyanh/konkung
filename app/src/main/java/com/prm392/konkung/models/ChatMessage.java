package com.prm392.konkung.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "chat_messages")
public class ChatMessage {
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private String message;
    private boolean isFromUser;
    private long timestamp;
    private String sessionId;
    
    public ChatMessage() {}
    
    @Ignore
    public ChatMessage(String message, boolean isFromUser, String sessionId) {
        this.message = message;
        this.isFromUser = isFromUser;
        this.sessionId = sessionId;
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public boolean isFromUser() { return isFromUser; }
    public void setFromUser(boolean fromUser) { this.isFromUser = fromUser; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
}
