package com.prm392.konkung.models;

import com.google.gson.annotations.SerializedName;

public class Blog {
    @SerializedName("id")
    private String id;
    
    @SerializedName("title")
    private String title;
    
    @SerializedName("content")
    private String content;
    
    @SerializedName("authorId")
    private String authorId;
    
    @SerializedName("authorName")
    private String authorName;
    
    @SerializedName("metaTitle")
    private String metaTitle;
    
    @SerializedName("metaDescription")
    private String metaDescription;
    
    @SerializedName("isActive")
    private boolean isActive;
    
    @SerializedName("thumbnail")
    private String thumbnail;
    
    @SerializedName("createdAt")
    private String createdAt;
    
    @SerializedName("updatedAt")
    private String updatedAt;

    // Constructors
    public Blog() {}

    public Blog(String id, String title, String content, String authorId, String authorName, 
                String metaTitle, String metaDescription, boolean isActive, String thumbnail, 
                String createdAt, String updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.authorId = authorId;
        this.authorName = authorName;
        this.metaTitle = metaTitle;
        this.metaDescription = metaDescription;
        this.isActive = isActive;
        this.thumbnail = thumbnail;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getMetaTitle() {
        return metaTitle;
    }

    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Helper methods
    public String getShortContent() {
        if (content == null || content.length() <= 150) {
            return content;
        }
        return content.substring(0, 150) + "...";
    }

    public String getFormattedCreatedDate() {
        if (createdAt == null) return "";
        try {
            // Parse ISO date format and return formatted date
            return createdAt.substring(0, 10).replace("-", "/");
        } catch (Exception e) {
            return createdAt;
        }
    }

    @Override
    public String toString() {
        return "Blog{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", authorName='" + authorName + '\'' +
                ", isActive=" + isActive +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
