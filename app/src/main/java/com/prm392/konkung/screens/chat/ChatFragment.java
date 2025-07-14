package com.prm392.konkung.screens.chat;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prm392.konkung.R;
import com.prm392.konkung.adapters.ChatAdapter;
import com.prm392.konkung.models.ChatMessage;
import com.prm392.konkung.repository.ChatRepository;

import java.util.List;

public class ChatFragment extends Fragment {
    
    private RecyclerView recyclerViewChat;
    private EditText editTextMessage;
    private ImageView buttonSend, buttonClearChat;
    
    private ChatAdapter chatAdapter;
    private ChatRepository chatRepository;
    private Handler mainHandler;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initializeComponents(view);
        setupViews(view);
        loadChatHistory();
        
        // Send welcome message if this is a new session
        sendWelcomeMessage();
    }
    
    private void initializeComponents(View view) {
        recyclerViewChat = view.findViewById(R.id.recyclerViewChat);
        editTextMessage = view.findViewById(R.id.editTextMessage);
        buttonSend = view.findViewById(R.id.buttonSend);
        buttonClearChat = view.findViewById(R.id.buttonClearChat);
        
        chatAdapter = new ChatAdapter();
        chatRepository = ChatRepository.getInstance(requireContext());
        mainHandler = new Handler(Looper.getMainLooper());
    }

    private void setupViews(View view) {
        // Setup RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setStackFromEnd(true);
        recyclerViewChat.setLayoutManager(layoutManager);
        recyclerViewChat.setAdapter(chatAdapter);
        
        // Setup send button click listener
        buttonSend.setOnClickListener(v -> sendMessage());
        
        // Setup clear chat button
        buttonClearChat.setOnClickListener(v -> clearChat());
        
        // Setup text change listener for send button state
        editTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSendButtonState();
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
        
        // Initial send button state
        updateSendButtonState();
    }
    
    private void sendMessage() {
        String messageText = editTextMessage.getText().toString().trim();
        if (messageText.isEmpty()) {
            return;
        }
        
        // Clear input
        editTextMessage.setText("");
        
        // Send message through repository
        chatRepository.sendMessage(messageText, new ChatRepository.MessageCallback() {
            @Override
            public void onMessageSent(ChatMessage userMessage, ChatMessage botResponse) {
                mainHandler.post(() -> {
                    chatAdapter.addMessage(userMessage);
                    scrollToBottom();
                    
                    // Add bot response with a slight delay for better UX
                    mainHandler.postDelayed(() -> {
                        chatAdapter.addMessage(botResponse);
                        scrollToBottom();
                    }, 500);
                });
            }
            
            @Override
            public void onError(String error) {
                mainHandler.post(() -> {
                    Toast.makeText(requireContext(), "Failed to send message: " + error, 
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private void loadChatHistory() {
        chatRepository.getChatHistory(new ChatRepository.ChatCallback() {
            @Override
            public void onSuccess(List<ChatMessage> messages) {
                mainHandler.post(() -> {
                    if (messages != null && !messages.isEmpty()) {
                        chatAdapter.addMessages(messages);
                        scrollToBottom();
                    }
                });
            }
            
            @Override
            public void onError(String error) {
                mainHandler.post(() -> {
                    Toast.makeText(requireContext(), "Failed to load chat history: " + error, 
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private void clearChat() {
        chatRepository.clearCurrentSession(new ChatRepository.ChatCallback() {
            @Override
            public void onSuccess(List<ChatMessage> messages) {
                mainHandler.post(() -> {
                    chatAdapter.clearMessages();
                    Toast.makeText(requireContext(), "Chat cleared", Toast.LENGTH_SHORT).show();
                    sendWelcomeMessage();
                });
            }
            
            @Override
            public void onError(String error) {
                mainHandler.post(() -> {
                    Toast.makeText(requireContext(), "Failed to clear chat: " + error, 
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private void sendWelcomeMessage() {
        // Only send welcome if chat is empty
        if (chatAdapter.getItemCount() == 0) {
            chatRepository.sendMessage("Hello", new ChatRepository.MessageCallback() {
                @Override
                public void onMessageSent(ChatMessage userMessage, ChatMessage botResponse) {
                    mainHandler.post(() -> {
                        // Only add bot response, not the "Hello" user message
                        chatAdapter.addMessage(botResponse);
                        scrollToBottom();
                    });
                }
                
                @Override
                public void onError(String error) {
                    // Silently handle error for welcome message
                }
            });
        }
    }
    
    private void updateSendButtonState() {
        boolean hasText = !editTextMessage.getText().toString().trim().isEmpty();
        buttonSend.setAlpha(hasText ? 1.0f : 0.5f);
        buttonSend.setClickable(hasText);
    }
    
    private void scrollToBottom() {
        if (chatAdapter.getItemCount() > 0) {
            recyclerViewChat.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
        }
    }
}
