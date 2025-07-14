package com.prm392.konkung.models;

public class CartItem {
    private String productId;
    private String productName;
    private String thumbnail;
    private int quantity;
    private double originalPrice;
    private double salePrice;
    private int gram;
    private int productQuantity;

    // Getters & Setters
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getThumbnail() { return thumbnail; }
    public void setThumbnail(String thumbnail) { this.thumbnail = thumbnail; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(double originalPrice) { this.originalPrice = originalPrice; }
    public double getSalePrice() { return salePrice; }
    public void setSalePrice(double salePrice) { this.salePrice = salePrice; }
    public int getGram() { return gram; }
    public void setGram(int gram) { this.gram = gram; }
    public int getProductQuantity() { return productQuantity; }
    public void setProductQuantity(int productQuantity) { this.productQuantity = productQuantity; }

    public double getTotalPrice() {
        return (salePrice > 0 ? salePrice : originalPrice) * quantity;
    }
}
