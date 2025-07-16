package com.prm392.konkung.models;

public class CheckoutRequest {
    private String address;
    private String note;
    private String paymentMethod;

    public CheckoutRequest(String address, String note, String paymentMethod) {
        this.address = address;
        this.note = note;
        this.paymentMethod = paymentMethod;
    }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
} 