package com.prm392.konkung.models;

import com.google.gson.annotations.SerializedName;

public class OrderLog {
    @SerializedName("status")
    private String status;
    
    @SerializedName("createdAt")
    private String createdAt;

    // Constructors
    public OrderLog() {}

    public OrderLog(String status, String createdAt) {
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and Setters
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

    // Helper methods
    public String getStatusDisplay() {
        if (status == null) return "Không rõ";
        
        switch (status) {
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
                return status;
        }
    }

    public String getFormattedDate() {
        if (createdAt == null || createdAt.isEmpty()) {
            return "";
        }
        
        try {
            // Parse ISO date and format to Vietnamese format
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

    public String getFormattedDateTime() {
        if (createdAt == null || createdAt.isEmpty()) {
            return "";
        }
        
        try {
            // Parse ISO date and format to Vietnamese format with time
            String[] dateParts = createdAt.split("T");
            if (dateParts.length == 2) {
                String[] dateComponents = dateParts[0].split("-");
                String[] timeComponents = dateParts[1].split(":");
                if (dateComponents.length == 3 && timeComponents.length >= 2) {
                    return dateComponents[2] + "/" + dateComponents[1] + "/" + dateComponents[0] + 
                           " " + timeComponents[0] + ":" + timeComponents[1];
                }
            }
            return createdAt;
        } catch (Exception e) {
            return createdAt;
        }
    }

    @Override
    public String toString() {
        return "OrderLog{" +
                "status='" + status + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
