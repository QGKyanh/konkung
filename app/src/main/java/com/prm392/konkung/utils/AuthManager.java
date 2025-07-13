package com.prm392.konkung.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.prm392.konkung.models.User;

public class AuthManager {
    private static final String PREFS_NAME = "AuthPrefs";
    private static final String KEY_USER = "current_user";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    
    private static AuthManager instance;
    private SharedPreferences prefs;
    private Gson gson;
    private User currentUser;
    private String authToken;
    private boolean isLoggedIn;

    private AuthManager(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
        loadAuthData();
    }

    public static synchronized AuthManager getInstance(Context context) {
        if (instance == null) {
            instance = new AuthManager(context.getApplicationContext());
        }
        return instance;
    }

    public static AuthManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("AuthManager not initialized. Call getInstance(Context) first.");
        }
        return instance;
    }

    private void loadAuthData() {
        try {
            String userJson = prefs.getString(KEY_USER, null);
            if (userJson != null) {
                currentUser = gson.fromJson(userJson, User.class);
            }
            
            authToken = prefs.getString(KEY_TOKEN, null);
            isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false);
        } catch (Exception e) {
            e.printStackTrace();
            clearAuthData();
        }
    }

    private void saveAuthData() {
        try {
            SharedPreferences.Editor editor = prefs.edit();
            
            if (currentUser != null) {
                editor.putString(KEY_USER, gson.toJson(currentUser));
                // Lưu refreshToken riêng nếu cần
                if (currentUser.getRefreshToken() != null) {
                    editor.putString("refresh_token", currentUser.getRefreshToken());
                }
            }
            
            if (authToken != null) {
                editor.putString(KEY_TOKEN, authToken);
            }
            
            editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void login(User user, String token) {
        this.currentUser = user;
        this.authToken = user.getAccessToken() != null ? user.getAccessToken() : token;
        this.isLoggedIn = true;
        saveAuthData();
    }

    public void logout() {
        clearAuthData();
    }

    private void clearAuthData() {
        this.currentUser = null;
        this.authToken = null;
        this.isLoggedIn = false;
        
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    public boolean isLoggedIn() {
        return isLoggedIn && currentUser != null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void updateUser(User user) {
        this.currentUser = user;
        saveAuthData();
    }

    public boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }

    public String getUserId() {
        return currentUser != null ? currentUser.getId() : null;
    }

    public String getUserEmail() {
        return currentUser != null ? currentUser.getEmail() : null;
    }

    public String getUserDisplayName() {
        return currentUser != null ? currentUser.getDisplayName() : null;
    }
} 