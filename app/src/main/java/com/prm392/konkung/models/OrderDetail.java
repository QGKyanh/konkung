package com.prm392.konkung.models;

import com.google.gson.annotations.SerializedName;

public class OrderDetail {
    @SerializedName("productId")
    private String productId;
    
    @SerializedName("productName")
    private String productName;
    
    @SerializedName("quantity")
    private int quantity;
    
    @SerializedName("unitPrice")
    private double unitPrice;
    
    @SerializedName("itemPrice")
    private double itemPrice;
    
    @SerializedName("thumbnail")
    private String thumbnail;

    // Constructors
    public OrderDetail() {}

    public OrderDetail(String productId, String productName, int quantity, 
                      double unitPrice, double itemPrice, String thumbnail) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.itemPrice = itemPrice;
        this.thumbnail = thumbnail;
    }

    // Getters and Setters
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    // Helper methods
    public String getFormattedUnitPrice() {
        return String.format("%,.0f₫", unitPrice);
    }

    public String getFormattedItemPrice() {
        return String.format("%,.0f₫", itemPrice);
    }

    @Override
    public String toString() {
        return "OrderDetail{" +
                "productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", itemPrice=" + itemPrice +
                ", thumbnail='" + thumbnail + '\'' +
                '}';
    }
}
