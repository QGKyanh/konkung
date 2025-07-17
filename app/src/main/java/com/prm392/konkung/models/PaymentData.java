package com.prm392.konkung.models;

import com.google.gson.annotations.SerializedName;

public class PaymentData {
    @SerializedName("id")
    private String id;
    
    @SerializedName("orderCode")
    private long orderCode;
    
    @SerializedName("amount")
    private double amount;
    
    @SerializedName("amountPaid")
    private double amountPaid;
    
    @SerializedName("amountRemaining")
    private double amountRemaining;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("createdAt")
    private String createdAt;
    
    @SerializedName("transactions")
    private Object[] transactions;
    
    @SerializedName("canceledAt")
    private String canceledAt;
    
    @SerializedName("cancellationReason")
    private String cancellationReason;

    // Constructors
    public PaymentData() {}

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(long orderCode) {
        this.orderCode = orderCode;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public double getAmountRemaining() {
        return amountRemaining;
    }

    public void setAmountRemaining(double amountRemaining) {
        this.amountRemaining = amountRemaining;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Object[] getTransactions() {
        return transactions;
    }

    public void setTransactions(Object[] transactions) {
        this.transactions = transactions;
    }

    public String getCanceledAt() {
        return canceledAt;
    }

    public void setCanceledAt(String canceledAt) {
        this.canceledAt = canceledAt;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    // Helper methods
    public String getFormattedAmount() {
        return String.format("%,.0f₫", amount);
    }

    public String getFormattedAmountPaid() {
        return String.format("%,.0f₫", amountPaid);
    }

    public String getFormattedAmountRemaining() {
        return String.format("%,.0f₫", amountRemaining);
    }

    public String getPaymentStatusDisplay() {
        if (status == null) return "Không rõ";
        
        switch (status.toUpperCase()) {
            case "PAID":
                return "Đã thanh toán";
            case "PENDING":
                return "Chờ thanh toán";
            case "EXPIRED":
                return "Hết hạn";
            case "CANCELLED":
                return "Đã hủy";
            default:
                return status;
        }
    }

    @Override
    public String toString() {
        return "PaymentData{" +
                "id='" + id + '\'' +
                ", orderCode=" + orderCode +
                ", amount=" + amount +
                ", amountPaid=" + amountPaid +
                ", amountRemaining=" + amountRemaining +
                ", status='" + status + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
