package com.prm392.konkung.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prm392.konkung.R;
import com.prm392.konkung.models.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    
    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_BOT = 2;
    
    private List<ChatMessage> messages;
    private SimpleDateFormat timeFormat;
    
    public ChatAdapter() {
        this.messages = new ArrayList<>();
        this.timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }
    
    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isFromUser() ? VIEW_TYPE_USER : VIEW_TYPE_BOT;
    }
    
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_user, parent, false);
            return new UserMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_bot, parent, false);
            return new BotMessageViewHolder(view);
        }
    }
    
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        
        if (holder instanceof UserMessageViewHolder) {
            ((UserMessageViewHolder) holder).bind(message);
        } else if (holder instanceof BotMessageViewHolder) {
            ((BotMessageViewHolder) holder).bind(message);
        }
    }
    
    @Override
    public int getItemCount() {
        return messages.size();
    }
    
    public void addMessage(ChatMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }
    
    public void addMessages(List<ChatMessage> newMessages) {
        int startPosition = messages.size();
        messages.addAll(newMessages);
        notifyItemRangeInserted(startPosition, newMessages.size());
    }
    
    public void clearMessages() {
        messages.clear();
        notifyDataSetChanged();
    }
    
    class UserMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;
        
        public UserMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.textUserMessage);
            timeText = itemView.findViewById(R.id.textUserTime);
        }
        
        public void bind(ChatMessage message) {
            messageText.setText(message.getMessage());
            timeText.setText(timeFormat.format(new Date(message.getTimestamp())));
        }
    }
    
    class BotMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;
        
        public BotMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.textBotMessage);
            timeText = itemView.findViewById(R.id.textBotTime);
        }
        
        public void bind(ChatMessage message) {
            messageText.setText(message.getMessage());
            timeText.setText(timeFormat.format(new Date(message.getTimestamp())));
        }
    }
}
