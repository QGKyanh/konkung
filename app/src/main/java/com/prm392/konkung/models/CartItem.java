package com.prm392.konkung.models;

public class CartItem {
    private Product product;
    private int quantity;

    public CartItem() {}

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    // Getters and Setters
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    // Helper methods
    public double getTotalPrice() {
        return product.getCurrentPrice() * quantity;
    }

    public double getOriginalTotalPrice() {
        return product.getOriginalPrice() * quantity;
    }

    public double getSavings() {
        if (product.isOnSale()) {
            return (product.getOriginalPrice() - product.getSalePrice()) * quantity;
        }
        return 0;
    }
}
