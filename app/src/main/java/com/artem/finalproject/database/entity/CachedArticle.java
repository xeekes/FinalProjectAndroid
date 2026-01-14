package com.artem.finalproject.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Таблица кэшированных новостей
 */
@Entity(tableName = "cached_articles")
public class CachedArticle {
    @PrimaryKey
    @NonNull
    private String url;
    
    private String title;
    private String description;
    private String urlToImage;
    private String author;
    private String publishedAt;
    private String sourceName;
    private String query;
    private long cachedAt;
    
    public CachedArticle() {
        this.cachedAt = System.currentTimeMillis();
    }
    
    @NonNull
    public String getUrl() {
        return url;
    }
    
    public void setUrl(@NonNull String url) {
        this.url = url;
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
    
    public String getQuery() {
        return query;
    }
    
    public void setQuery(String query) {
        this.query = query;
    }
    
    public long getCachedAt() {
        return cachedAt;
    }
    
    public void setCachedAt(long cachedAt) {
        this.cachedAt = cachedAt;
    }
}
