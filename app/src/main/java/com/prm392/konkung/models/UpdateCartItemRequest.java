package com.prm392.konkung.models;

public class UpdateCartItemRequest {
    private int quantity;
    public UpdateCartItemRequest(int quantity) {
        this.quantity = quantity;
    }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
} 