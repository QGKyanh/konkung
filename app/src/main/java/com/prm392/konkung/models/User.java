package com.prm392.konkung.models;

public class User {
    private String userID;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String profilePictureUrl;
    private String googleId;
    private String role;
    private boolean isBanned;
    private boolean isActive;
    private String accessToken;
    private String refreshToken;
    private String address;

    // Default constructor
    public User() {
    }

    // Constructor with email and name
    public User(String email, String name) {
        this.email = email;
        if (name != null && !name.isEmpty()) {
            String[] nameParts = name.split(" ", 2);
            if (nameParts.length > 0) {
                this.firstName = nameParts[0];
                if (nameParts.length > 1) {
                    this.lastName = nameParts[1];
                }
            }
        }
    }

    public String getUserID() { return userID; }
    public void setUserID(String userID) { this.userID = userID; }

    // Alias for getUserID to match the code that calls getId()
    public String getId() { return userID; }
    public void setId(String id) { this.userID = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    // Alias for getPhoneNumber to match the code that calls getPhone()
    public String getPhone() { return phoneNumber; }
    public void setPhone(String phone) { this.phoneNumber = phone; }

    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }

    public String getGoogleId() { return googleId; }
    public void setGoogleId(String googleId) { this.googleId = googleId; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isBanned() { return isBanned; }
    public void setBanned(boolean banned) { isBanned = banned; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    // Method to check if user is admin
    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }

    public String getDisplayName() {
        if (firstName != null && lastName != null)
            return firstName + " " + lastName;
        if (username != null)
            return username;
        return email;
    }
} 