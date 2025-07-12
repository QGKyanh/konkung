package com.prm392.konkung.repository;

import android.content.Context;
import android.os.AsyncTask;

import com.prm392.konkung.database.ChatDatabase;
import com.prm392.konkung.database.ChatMessageDao;
import com.prm392.konkung.models.ChatMessage;
import com.prm392.konkung.services.GeminiChatbotService;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatRepository {
    
    private static ChatRepository instance;
    private ChatMessageDao chatMessageDao;
    private GeminiChatbotService chatbotService;
    private ExecutorService executor;
    private String currentSessionId;
    
    private ChatRepository(Context context) {
        ChatDatabase database = ChatDatabase.getInstance(context);
        chatMessageDao = database.chatMessageDao();
        chatbotService = new GeminiChatbotService();
        executor = Executors.newFixedThreadPool(2);
        currentSessionId = UUID.randomUUID().toString();
    }
    
    public static synchronized ChatRepository getInstance(Context context) {
        if (instance == null) {
            instance = new ChatRepository(context.getApplicationContext());
        }
        return instance;
    }
    
    public interface ChatCallback {
        void onSuccess(List<ChatMessage> messages);
        void onError(String error);
    }
    
    public interface MessageCallback {
        void onMessageSent(ChatMessage userMessage, ChatMessage botResponse);
        void onError(String error);
    }
    
    public void sendMessage(String userMessage, MessageCallback callback) {
        executor.execute(() -> {
            try {
                // Create user message
                ChatMessage userMsg = new ChatMessage(userMessage, true, currentSessionId);
                chatMessageDao.insertMessage(userMsg);
                
                // Generate bot response using Gemini AI service
                chatbotService.generateAIResponse(userMessage, new GeminiChatbotService.ResponseCallback() {
                    @Override
                    public void onSuccess(String botResponse) {
                        try {
                            ChatMessage botMsg = new ChatMessage(botResponse, false, currentSessionId);
                            chatMessageDao.insertMessage(botMsg);
                            
                            if (callback != null) {
                                callback.onMessageSent(userMsg, botMsg);
                            }
                        } catch (Exception e) {
                            if (callback != null) {
                                callback.onError("Failed to save bot response: " + e.getMessage());
                            }
                        }
                    }
                    
                    @Override
                    public void onError(String error) {
                        if (callback != null) {
                            callback.onError("Failed to get AI response: " + error);
                        }
                    }
                });
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("Failed to send message: " + e.getMessage());
                }
            }
        });
    }
    
    public void getChatHistory(ChatCallback callback) {
        executor.execute(() -> {
            try {
                List<ChatMessage> messages = chatMessageDao.getMessagesBySession(currentSessionId);
                if (callback != null) {
                    callback.onSuccess(messages);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("Failed to load chat history: " + e.getMessage());
                }
            }
        });
    }
    
    public void startNewSession() {
        currentSessionId = UUID.randomUUID().toString();
    }
    
    public void clearCurrentSession(ChatCallback callback) {
        executor.execute(() -> {
            try {
                chatMessageDao.deleteSessionMessages(currentSessionId);
                if (callback != null) {
                    callback.onSuccess(null);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("Failed to clear session: " + e.getMessage());
                }
            }
        });
    }
    
    public String getCurrentSessionId() {
        return currentSessionId;
    }
}
