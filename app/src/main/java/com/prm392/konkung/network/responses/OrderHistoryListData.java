package com.prm392.konkung.network.responses;

import com.google.gson.annotations.SerializedName;
import com.prm392.konkung.models.OrderHistory;

import java.util.List;

public class OrderHistoryListData {
    @SerializedName("items")
    private List<OrderHistory> items;
    
    @SerializedName("page")
    private int page;
    
    @SerializedName("pageSize")
    private int pageSize;
    
    @SerializedName("totalCount")
    private int totalCount;
    
    @SerializedName("hasNextPage")
    private boolean hasNextPage;
    
    @SerializedName("hasPreviousPage")
    private boolean hasPreviousPage;

    // Constructors
    public OrderHistoryListData() {}

    public OrderHistoryListData(List<OrderHistory> items, int page, int pageSize, int totalCount, 
                               boolean hasNextPage, boolean hasPreviousPage) {
        this.items = items;
        this.page = page;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        this.hasNextPage = hasNextPage;
        this.hasPreviousPage = hasPreviousPage;
    }

    // Getters and Setters
    public List<OrderHistory> getItems() {
        return items;
    }

    public void setItems(List<OrderHistory> items) {
        this.items = items;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    public boolean isHasPreviousPage() {
        return hasPreviousPage;
    }

    public void setHasPreviousPage(boolean hasPreviousPage) {
        this.hasPreviousPage = hasPreviousPage;
    }

    @Override
    public String toString() {
        return "OrderHistoryListData{" +
                "items=" + (items != null ? items.size() : 0) + " orders" +
                ", page=" + page +
                ", pageSize=" + pageSize +
                ", totalCount=" + totalCount +
                ", hasNextPage=" + hasNextPage +
                ", hasPreviousPage=" + hasPreviousPage +
                '}';
    }
}
