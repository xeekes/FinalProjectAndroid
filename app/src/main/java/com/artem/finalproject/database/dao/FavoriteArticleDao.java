package com.artem.finalproject.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.artem.finalproject.database.entity.FavoriteArticle;
import java.util.List;

@Dao
public interface FavoriteArticleDao {
    @Query("SELECT * FROM favorite_articles ORDER BY savedAt DESC")
    List<FavoriteArticle> getAll();
    
    @Query("SELECT * FROM favorite_articles WHERE id = :id")
    FavoriteArticle getById(long id);
    
    @Query("SELECT * FROM favorite_articles WHERE url = :url")
    FavoriteArticle getByUrl(String url);
    
    @Insert
    void insert(FavoriteArticle article);
    
    @Update
    void update(FavoriteArticle article);
    
    @Delete
    void delete(FavoriteArticle article);
    
    @Query("DELETE FROM favorite_articles WHERE id = :id")
    void deleteById(long id);
    
    @Query("DELETE FROM favorite_articles")
    void deleteAll();
}
