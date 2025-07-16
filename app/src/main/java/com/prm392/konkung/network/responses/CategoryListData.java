package com.prm392.konkung.network.responses;

import com.prm392.konkung.models.Category;
import java.io.Serializable;
import java.util.List;

public class CategoryListData implements Serializable {
    private List<Category> items;
    public List<Category> getItems() { return items; }
    public void setItems(List<Category> items) { this.items = items; }
} 