package com.artem.finalproject.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Таблица уведомлений
 */
@Entity(tableName = "notifications")
public class NotificationItem {
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private String title;
    private String message;
    private long createdAt;
    private boolean isRead;
    private String articleUrl;
    
    public NotificationItem() {
        this.createdAt = System.currentTimeMillis();
        this.isRead = false;
    }
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
    
    public boolean isRead() {
        return isRead;
    }
    
    public void setRead(boolean read) {
        isRead = read;
    }
    
    public String getArticleUrl() {
        return articleUrl;
    }
    
    public void setArticleUrl(String articleUrl) {
        this.articleUrl = articleUrl;
    }
}
