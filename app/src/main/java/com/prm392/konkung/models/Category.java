package com.prm392.konkung.models;

import java.io.Serializable;

public class Category implements Serializable {
    private int id;
    private String name;
    private int iconResId; // resource id cho icon hoặc drawable
    private String description; // link ảnh

    public Category(int id, String name, int iconResId, String description) {
        this.id = id;
        this.name = name;
        this.iconResId = iconResId;
        this.description = description;
    }

    public Category(int id, String name, int iconResId) {
        this(id, name, iconResId, null);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getIconResId() { return iconResId; }
    public void setIconResId(int iconResId) { this.iconResId = iconResId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
} 