package com.prm392.konkung.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.prm392.konkung.models.ChatMessage;

import java.util.List;

@Dao
public interface ChatMessageDao {
    
    @Insert
    void insertMessage(ChatMessage message);
    
    @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    List<ChatMessage> getMessagesBySession(String sessionId);
    
    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC")
    List<ChatMessage> getAllMessages();
    
    @Query("DELETE FROM chat_messages WHERE sessionId = :sessionId")
    void deleteSessionMessages(String sessionId);
    
    @Query("DELETE FROM chat_messages")
    void deleteAllMessages();
    
    @Query("SELECT DISTINCT sessionId FROM chat_messages ORDER BY timestamp DESC")
    List<String> getAllSessions();
}
