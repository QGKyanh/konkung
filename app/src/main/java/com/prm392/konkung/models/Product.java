package com.prm392.konkung.models;

import com.google.gson.annotations.SerializedName;

public class Product {
    @SerializedName("id")
    private String id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("quantity")
    private int quantity;
    
    @SerializedName("originalPrice")
    private double originalPrice;
    
    @SerializedName("salePrice")
    private double salePrice;
    
    @SerializedName("thumbnail")
    private String thumbnail;
    
    @SerializedName("categoryId")
    private int categoryId;
    
    @SerializedName("category")
    private String category;
    
    @SerializedName("brandId")
    private int brandId;
    
    @SerializedName("brand")
    private String brand;
    
    @SerializedName("unitId")
    private int unitId;
    
    @SerializedName("unit")
    private String unit;
    
    @SerializedName("statusId")
    private int statusId;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("averageRating")
    private double averageRating;
    
    @SerializedName("ratingCount")
    private int ratingCount;
    
    @SerializedName("orderCount")
    private int orderCount;
    
    @SerializedName("isActive")
    private boolean isActive;
    
    @SerializedName("createdAt")
    private String createdAt;
    
    @SerializedName("maxPreOrderQuantity")
    private int maxPreOrderQuantity;
    
    @SerializedName("startDate")
    private String startDate;
    
    @SerializedName("endDate")
    private String endDate;
    
    @SerializedName("expectedPreOrderDays")
    private int expectedPreOrderDays;

    // Constructors
    public Product() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(double originalPrice) { this.originalPrice = originalPrice; }

    public double getSalePrice() { return salePrice; }
    public void setSalePrice(double salePrice) { this.salePrice = salePrice; }

    public String getThumbnail() { return thumbnail; }
    public void setThumbnail(String thumbnail) { this.thumbnail = thumbnail; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getBrandId() { return brandId; }
    public void setBrandId(int brandId) { this.brandId = brandId; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public int getUnitId() { return unitId; }
    public void setUnitId(int unitId) { this.unitId = unitId; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public int getStatusId() { return statusId; }
    public void setStatusId(int statusId) { this.statusId = statusId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }

    public int getRatingCount() { return ratingCount; }
    public void setRatingCount(int ratingCount) { this.ratingCount = ratingCount; }

    public int getOrderCount() { return orderCount; }
    public void setOrderCount(int orderCount) { this.orderCount = orderCount; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public int getMaxPreOrderQuantity() { return maxPreOrderQuantity; }
    public void setMaxPreOrderQuantity(int maxPreOrderQuantity) { this.maxPreOrderQuantity = maxPreOrderQuantity; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public int getExpectedPreOrderDays() { return expectedPreOrderDays; }
    public void setExpectedPreOrderDays(int expectedPreOrderDays) { this.expectedPreOrderDays = expectedPreOrderDays; }

    // Helper methods
    public double getCurrentPrice() {
        return salePrice > 0 ? salePrice : originalPrice;
    }

    public boolean isOnSale() {
        return salePrice > 0 && salePrice < originalPrice;
    }

    public double getDiscountPercentage() {
        if (!isOnSale()) return 0;
        return ((originalPrice - salePrice) / originalPrice) * 100;
    }

    public boolean isAvailable() {
        return quantity > 0 && "SELLING".equals(status);
    }

    public boolean isPreOrder() {
        return "PREORDER".equals(status);
    }
}
