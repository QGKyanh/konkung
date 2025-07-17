package com.prm392.konkung.models;

import com.google.gson.annotations.SerializedName;

public class OrderProduct {
    @SerializedName("name")
    private String name;
    
    @SerializedName("thumbnail")
    private String thumbnail;

    // Constructors
    public OrderProduct() {}

    public OrderProduct(String name, String thumbnail) {
        this.name = name;
        this.thumbnail = thumbnail;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public String toString() {
        return "OrderProduct{" +
                "name='" + name + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                '}';
    }
}
