package com.prm392.konkung.network.responses;

import com.google.gson.annotations.SerializedName;
import com.prm392.konkung.models.Product;

import java.util.List;

public class ProductListData {
    @SerializedName("items")
    private List<Product> items;
    
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
    public ProductListData() {}

    // Getters and Setters
    public List<Product> getItems() { return items; }
    public void setItems(List<Product> items) { this.items = items; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }

    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }

    public boolean isHasNextPage() { return hasNextPage; }
    public void setHasNextPage(boolean hasNextPage) { this.hasNextPage = hasNextPage; }

    public boolean isHasPreviousPage() { return hasPreviousPage; }
    public void setHasPreviousPage(boolean hasPreviousPage) { this.hasPreviousPage = hasPreviousPage; }
}
