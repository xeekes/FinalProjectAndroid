package com.artem.finalproject.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.artem.finalproject.database.entity.Category;
import java.util.List;

@Dao
public interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY `order` ASC")
    List<Category> getAll();
    
    @Query("SELECT * FROM categories WHERE isSelected = 1")
    List<Category> getSelected();
    
    @Query("SELECT * FROM categories WHERE id = :id")
    Category getById(long id);
    
    @Insert
    void insert(Category category);
    
    @Insert
    void insertAll(List<Category> categories);
    
    @Update
    void update(Category category);
    
    @Query("DELETE FROM categories")
    void deleteAll();
}
