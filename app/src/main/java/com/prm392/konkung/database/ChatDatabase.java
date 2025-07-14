package com.prm392.konkung.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.prm392.konkung.models.ChatMessage;

@Database(
    entities = {ChatMessage.class},
    version = 1,
    exportSchema = false
)
public abstract class ChatDatabase extends RoomDatabase {
    
    private static volatile ChatDatabase INSTANCE;
    private static final String DATABASE_NAME = "chat_database";
    
    public abstract ChatMessageDao chatMessageDao();
    
    public static ChatDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (ChatDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            ChatDatabase.class,
                            DATABASE_NAME
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}
