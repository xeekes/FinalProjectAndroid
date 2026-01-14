package com.artem.finalproject.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Таблица избранных новостей
 */
@Entity(tableName = "favorite_articles")
public class FavoriteArticle {
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private String author;
    private String publishedAt;
    private String sourceName;
    private long savedAt;
    
    public FavoriteArticle() {
        this.savedAt = System.currentTimeMillis();
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getUrlToImage() {
        return urlToImage;
    }
    
    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public String getPublishedAt() {
        return publishedAt;
    }
    
    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }
    
    public String getSourceName() {
        return sourceName;
    }
    
    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }
    
    public long getSavedAt() {
        return savedAt;
    }
    
    public void setSavedAt(long savedAt) {
        this.savedAt = savedAt;
    }
}
