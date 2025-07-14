package com.prm392.konkung;

import android.app.Application;

import com.prm392.konkung.utils.AuthManager;

public class KonkungApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize AuthManager
        AuthManager.getInstance(this);
    }
} 