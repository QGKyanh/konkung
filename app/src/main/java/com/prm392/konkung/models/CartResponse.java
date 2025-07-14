package com.prm392.konkung.models;

public class CartResponse {
    private int id;
    private String customerId;
    private double totalPrice;
    private double totalPriceAfterDiscount;
    private int totalQuantity;
    private int totalGram;
    private CartItems cartItems;
    private String voucherId;
    private boolean isUsingPoint;
    private int voucherDiscountPercent;
    private double voucherDiscount;
    private double pointDiscount;
    private String voucherMessage;
    private String pointMessage;

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public double getTotalPriceAfterDiscount() { return totalPriceAfterDiscount; }
    public void setTotalPriceAfterDiscount(double totalPriceAfterDiscount) { this.totalPriceAfterDiscount = totalPriceAfterDiscount; }
    public int getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(int totalQuantity) { this.totalQuantity = totalQuantity; }
    public int getTotalGram() { return totalGram; }
    public void setTotalGram(int totalGram) { this.totalGram = totalGram; }
    public CartItems getCartItems() { return cartItems; }
    public void setCartItems(CartItems cartItems) { this.cartItems = cartItems; }
    public String getVoucherId() { return voucherId; }
    public void setVoucherId(String voucherId) { this.voucherId = voucherId; }
    public boolean isUsingPoint() { return isUsingPoint; }
    public void setUsingPoint(boolean usingPoint) { isUsingPoint = usingPoint; }
    public int getVoucherDiscountPercent() { return voucherDiscountPercent; }
    public void setVoucherDiscountPercent(int voucherDiscountPercent) { this.voucherDiscountPercent = voucherDiscountPercent; }
    public double getVoucherDiscount() { return voucherDiscount; }
    public void setVoucherDiscount(double voucherDiscount) { this.voucherDiscount = voucherDiscount; }
    public double getPointDiscount() { return pointDiscount; }
    public void setPointDiscount(double pointDiscount) { this.pointDiscount = pointDiscount; }
    public String getVoucherMessage() { return voucherMessage; }
    public void setVoucherMessage(String voucherMessage) { this.voucherMessage = voucherMessage; }
    public String getPointMessage() { return pointMessage; }
    public void setPointMessage(String pointMessage) { this.pointMessage = pointMessage; }
} 