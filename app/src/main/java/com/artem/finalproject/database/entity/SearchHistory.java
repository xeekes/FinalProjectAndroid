package com.artem.finalproject.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Таблица истории поиска
 */
@Entity(tableName = "search_history")
public class SearchHistory {
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    private String query;
    private long searchedAt;
    private int resultCount;
    
    public SearchHistory() {
        this.searchedAt = System.currentTimeMillis();
    }
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getQuery() {
        return query;
    }
    
    public void setQuery(String query) {
        this.query = query;
    }
    
    public long getSearchedAt() {
        return searchedAt;
    }
    
    public void setSearchedAt(long searchedAt) {
        this.searchedAt = searchedAt;
    }
    
    public int getResultCount() {
        return resultCount;
    }
    
    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }
}
