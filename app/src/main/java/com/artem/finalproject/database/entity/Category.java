package com.artem.finalproject.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Таблица категорий новостей
 */
@Entity(tableName = "categories")
public class Category {
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private String name;
    private String displayName;
    private boolean isSelected;
    private int order;
    
    public Category() {
    }
    
    public Category(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
        this.isSelected = false;
    }
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public boolean isSelected() {
        return isSelected;
    }
    
    public void setSelected(boolean selected) {
        isSelected = selected;
    }
    
    public int getOrder() {
        return order;
    }
    
    public void setOrder(int order) {
        this.order = order;
    }
}
