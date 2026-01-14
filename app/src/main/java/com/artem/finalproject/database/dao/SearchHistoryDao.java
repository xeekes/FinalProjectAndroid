package com.artem.finalproject.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;
import com.artem.finalproject.database.entity.SearchHistory;
import java.util.List;

@Dao
public interface SearchHistoryDao {
    @Query("SELECT * FROM search_history ORDER BY searchedAt DESC LIMIT :limit")
    List<SearchHistory> getRecent(int limit);
    
    @Query("SELECT * FROM search_history ORDER BY searchedAt DESC")
    List<SearchHistory> getAll();
    
    @Insert
    void insert(SearchHistory history);
    
    @Delete
    void delete(SearchHistory history);
    
    @Query("DELETE FROM search_history")
    void deleteAll();
}
