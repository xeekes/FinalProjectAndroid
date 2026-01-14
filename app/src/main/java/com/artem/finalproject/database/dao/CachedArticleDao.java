package com.artem.finalproject.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;
import com.artem.finalproject.database.entity.CachedArticle;
import java.util.List;

@Dao
public interface CachedArticleDao {
    @Query("SELECT * FROM cached_articles WHERE query = :query ORDER BY cachedAt DESC")
    List<CachedArticle> getByQuery(String query);
    
    @Query("SELECT * FROM cached_articles WHERE url = :url")
    CachedArticle getByUrl(String url);
    
    @Query("SELECT * FROM cached_articles ORDER BY cachedAt DESC")
    List<CachedArticle> getAll();
    
    @Insert
    void insert(CachedArticle article);
    
    @Delete
    void delete(CachedArticle article);
    
    @Query("DELETE FROM cached_articles WHERE cachedAt < :timestamp")
    void deleteOld(long timestamp);
    
    @Query("DELETE FROM cached_articles")
    void deleteAll();
}
