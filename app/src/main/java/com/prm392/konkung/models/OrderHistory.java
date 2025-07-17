package com.prm392.konkung.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderHistory {
    @SerializedName("id")
    private String id;
    
    @SerializedName("totalAmount")
    private double totalAmount;
    
    @SerializedName("paymentMethod")
    private String paymentMethod;
    
    @SerializedName("orderStatus")
    private String orderStatus;
    
    @SerializedName("createdAt")
    private String createdAt;
    
    @SerializedName("productList")
    private List<OrderProduct> productList;
    
    @SerializedName("isPreorder")
    private boolean isPreorder;

    // Constructors
    public OrderHistory() {}

    public OrderHistory(String id, double totalAmount, String paymentMethod, String orderStatus, 
                        String createdAt, List<OrderProduct> productList, boolean isPreorder) {
        this.id = id;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.orderStatus = orderStatus;
        this.createdAt = createdAt;
        this.productList = productList;
        this.isPreorder = isPreorder;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<OrderProduct> getProductList() {
        return productList;
    }

    public void setProductList(List<OrderProduct> productList) {
        this.productList = productList;
    }

    public boolean isPreorder() {
        return isPreorder;
    }

    public void setPreorder(boolean preorder) {
        isPreorder = preorder;
    }

    // Helper methods
    public String getFormattedCreatedDate() {
        if (createdAt == null) return "";
        try {
            // Parse ISO date format and return formatted date
            return createdAt.substring(0, 10).replace("-", "/");
        } catch (Exception e) {
            return createdAt;
        }
    }

    public String getStatusDisplayText() {
        switch (orderStatus) {
            case "Pending":
                return "Chờ xử lý";
            case "Processing":
                return "Đang xử lý";
            case "Shipped":
                return "Đã gửi hàng";
            case "Delivered":
                return "Đã giao hàng";
            case "Cancelled":
                return "Đã hủy";
            case "Completed":
                return "Hoàn thành";
            default:
                return orderStatus;
        }
    }

    public int getStatusColor() {
        switch (orderStatus) {
            case "Pending":
                return android.R.color.holo_orange_light;
            case "Processing":
                return android.R.color.holo_blue_light;
            case "Shipped":
                return android.R.color.holo_purple;
            case "Delivered":
            case "Completed":
                return android.R.color.holo_green_light;
            case "Cancelled":
                return android.R.color.holo_red_light;
            default:
                return android.R.color.darker_gray;
        }
    }

    public String getPaymentMethodDisplayText() {
        switch (paymentMethod) {
            case "PAYOS":
                return "PayOS";
            case "COD":
                return "Thanh toán khi nhận hàng";
            case "CREDIT_CARD":
                return "Thẻ tín dụng";
            case "BANK_TRANSFER":
                return "Chuyển khoản ngân hàng";
            default:
                return paymentMethod;
        }
    }

    public int getProductCount() {
        return productList != null ? productList.size() : 0;
    }

    @Override
    public String toString() {
        return "OrderHistory{" +
                "id='" + id + '\'' +
                ", totalAmount=" + totalAmount +
                ", orderStatus='" + orderStatus + '\'' +
                ", productCount=" + getProductCount() +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
