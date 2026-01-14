package com.artem.finalproject.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Таблица настроек приложения
 */
@Entity(tableName = "app_settings")
public class AppSettings {
    @PrimaryKey
    @NonNull
    private String key;
    
    private String value;
    private long updatedAt;
    
    public AppSettings() {
        this.updatedAt = System.currentTimeMillis();
    }
    
    public AppSettings(@NonNull String key, String value) {
        this.key = key;
        this.value = value;
        this.updatedAt = System.currentTimeMillis();
    }
    
    @NonNull
    public String getKey() {
        return key;
    }
    
    public void setKey(@NonNull String key) {
        this.key = key;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
        this.updatedAt = System.currentTimeMillis();
    }
    
    public long getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
