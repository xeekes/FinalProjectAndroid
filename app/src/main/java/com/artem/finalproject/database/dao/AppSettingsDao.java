package com.artem.finalproject.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.artem.finalproject.database.entity.AppSettings;

@Dao
public interface AppSettingsDao {
    @Query("SELECT * FROM app_settings WHERE `key` = :key")
    AppSettings getByKey(String key);
    
    @Query("SELECT * FROM app_settings")
    java.util.List<AppSettings> getAll();
    
    @Insert
    void insert(AppSettings settings);
    
    @Update
    void update(AppSettings settings);
    
    @Query("DELETE FROM app_settings WHERE `key` = :key")
    void deleteByKey(String key);
}
