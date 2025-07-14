package com.prm392.konkung.models;

public class CheckoutRequest {
    private int shippingFee;
    private int addressId;
    private String note;
    private String paymentMethod;
    private boolean isUsingPoint;
    private String voucherId;

    public CheckoutRequest(int shippingFee, int addressId, String note, String paymentMethod, boolean isUsingPoint) {
        this.shippingFee = shippingFee;
        this.addressId = addressId;
        this.note = note;
        this.paymentMethod = paymentMethod;
        this.isUsingPoint = isUsingPoint;
        this.voucherId = null;
    }

    public CheckoutRequest(int shippingFee, int addressId, String note, String paymentMethod, boolean isUsingPoint, String voucherId) {
        this.shippingFee = shippingFee;
        this.addressId = addressId;
        this.note = note;
        this.paymentMethod = paymentMethod;
        this.isUsingPoint = isUsingPoint;
        this.voucherId = voucherId;
    }

    public int getShippingFee() { return shippingFee; }
    public void setShippingFee(int shippingFee) { this.shippingFee = shippingFee; }
    public int getAddressId() { return addressId; }
    public void setAddressId(int addressId) { this.addressId = addressId; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public boolean isUsingPoint() { return isUsingPoint; }
    public void setUsingPoint(boolean usingPoint) { isUsingPoint = usingPoint; }
    public String getVoucherId() { return voucherId; }
    public void setVoucherId(String voucherId) { this.voucherId = voucherId; }
} 