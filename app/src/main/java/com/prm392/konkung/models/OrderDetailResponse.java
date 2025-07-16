package com.prm392.konkung.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OrderDetailResponse {
    @SerializedName("id")
    private String id;
    
    @SerializedName("customerId")
    private String customerId;
    
    @SerializedName("receiverName")
    private String receiverName;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("phoneNumber")
    private String phoneNumber;
    
    @SerializedName("address")
    private String address;
    
    @SerializedName("note")
    private String note;
    
    @SerializedName("orderDetail")
    private List<OrderDetail> orderDetail;
    
    @SerializedName("totalPriceBeforeDiscount")
    private double totalPriceBeforeDiscount;
    
    @SerializedName("voucherDiscount")
    private double voucherDiscount;
    
    @SerializedName("pointDiscount")
    private double pointDiscount;
    
    @SerializedName("totalPriceAfterDiscount")
    private double totalPriceAfterDiscount;
    
    @SerializedName("recievingPoint")
    private double receivingPoint;
    
    @SerializedName("shippingFee")
    private double shippingFee;
    
    @SerializedName("totalAmount")
    private double totalAmount;
    
    @SerializedName("paymentMethod")
    private String paymentMethod;
    
    @SerializedName("orderStatus")
    private String orderStatus;
    
    @SerializedName("createdAt")
    private String createdAt;
    
    @SerializedName("expectedDeliveryDate")
    private String expectedDeliveryDate;
    
    @SerializedName("paymentData")
    private PaymentData paymentData;
    
    @SerializedName("logs")
    private List<OrderLog> logs;
    
    @SerializedName("isPreorder")
    private boolean isPreorder;
    
    @SerializedName("shippingCode")
    private String shippingCode;

    // Constructors
    public OrderDetailResponse() {}

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<OrderDetail> getOrderDetail() {
        return orderDetail;
    }

    public void setOrderDetail(List<OrderDetail> orderDetail) {
        this.orderDetail = orderDetail;
    }

    public double getTotalPriceBeforeDiscount() {
        return totalPriceBeforeDiscount;
    }

    public void setTotalPriceBeforeDiscount(double totalPriceBeforeDiscount) {
        this.totalPriceBeforeDiscount = totalPriceBeforeDiscount;
    }

    public double getVoucherDiscount() {
        return voucherDiscount;
    }

    public void setVoucherDiscount(double voucherDiscount) {
        this.voucherDiscount = voucherDiscount;
    }

    public double getPointDiscount() {
        return pointDiscount;
    }

    public void setPointDiscount(double pointDiscount) {
        this.pointDiscount = pointDiscount;
    }

    public double getTotalPriceAfterDiscount() {
        return totalPriceAfterDiscount;
    }

    public void setTotalPriceAfterDiscount(double totalPriceAfterDiscount) {
        this.totalPriceAfterDiscount = totalPriceAfterDiscount;
    }

    public double getReceivingPoint() {
        return receivingPoint;
    }

    public void setReceivingPoint(double receivingPoint) {
        this.receivingPoint = receivingPoint;
    }

    public double getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(double shippingFee) {
        this.shippingFee = shippingFee;
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

    public String getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }

    public void setExpectedDeliveryDate(String expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    public PaymentData getPaymentData() {
        return paymentData;
    }

    public void setPaymentData(PaymentData paymentData) {
        this.paymentData = paymentData;
    }

    public List<OrderLog> getLogs() {
        return logs;
    }

    public void setLogs(List<OrderLog> logs) {
        this.logs = logs;
    }

    public boolean isPreorder() {
        return isPreorder;
    }

    public void setPreorder(boolean preorder) {
        isPreorder = preorder;
    }

    public String getShippingCode() {
        return shippingCode;
    }

    public void setShippingCode(String shippingCode) {
        this.shippingCode = shippingCode;
    }

    // Helper methods
    public String getFormattedTotalAmount() {
        return String.format("%,.0f₫", totalAmount);
    }

    public String getFormattedTotalPriceBeforeDiscount() {
        return String.format("%,.0f₫", totalPriceBeforeDiscount);
    }

    public String getFormattedVoucherDiscount() {
        return String.format("%,.0f₫", voucherDiscount);
    }

    public String getFormattedPointDiscount() {
        return String.format("%,.0f₫", pointDiscount);
    }

    public String getFormattedShippingFee() {
        return String.format("%,.0f₫", shippingFee);
    }

    public String getFormattedReceivingPoint() {
        return String.format("%,.0f điểm", receivingPoint);
    }

    public String getOrderStatusDisplay() {
        if (orderStatus == null) return "Không rõ";
        
        switch (orderStatus) {
            case "Pending":
                return "Chờ xử lý";
            case "Confirmed":
                return "Đã xác nhận";
            case "Preparing":
                return "Đang chuẩn bị";
            case "Shipped":
                return "Đang giao";
            case "Delivered":
                return "Đã giao";
            case "Cancelled":
                return "Đã hủy";
            case "Returned":
                return "Đã trả hàng";
            default:
                return orderStatus;
        }
    }

    public String getPaymentMethodDisplay() {
        if (paymentMethod == null) return "Không rõ";
        
        switch (paymentMethod.toUpperCase()) {
            case "PAYOS":
                return "PayOS";
            case "COD":
                return "Thanh toán khi nhận hàng";
            case "BANK_TRANSFER":
                return "Chuyển khoản ngân hàng";
            case "CREDIT_CARD":
                return "Thẻ tín dụng";
            default:
                return paymentMethod;
        }
    }

    public String getFormattedCreatedAt() {
        if (createdAt == null || createdAt.isEmpty()) {
            return "";
        }
        
        try {
            String[] dateParts = createdAt.split("T");
            if (dateParts.length > 0) {
                String[] dateComponents = dateParts[0].split("-");
                if (dateComponents.length == 3) {
                    return dateComponents[2] + "/" + dateComponents[1] + "/" + dateComponents[0];
                }
            }
            return createdAt.substring(0, Math.min(10, createdAt.length()));
        } catch (Exception e) {
            return createdAt;
        }
    }

    public int getTotalItems() {
        if (orderDetail == null) return 0;
        int total = 0;
        for (OrderDetail detail : orderDetail) {
            total += detail.getQuantity();
        }
        return total;
    }

    @Override
    public String toString() {
        return "OrderDetailResponse{" +
                "id='" + id + '\'' +
                ", customerId='" + customerId + '\'' +
                ", totalAmount=" + totalAmount +
                ", orderStatus='" + orderStatus + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                '}';
    }
}
