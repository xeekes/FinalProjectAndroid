package com.artem.finalproject.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.artem.finalproject.database.entity.NotificationItem;
import java.util.List;

@Dao
public interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY createdAt DESC")
    List<NotificationItem> getAll();
    
    @Query("SELECT * FROM notifications WHERE isRead = 0 ORDER BY createdAt DESC")
    List<NotificationItem> getUnread();
    
    @Query("SELECT * FROM notifications WHERE id = :id")
    NotificationItem getById(long id);
    
    @Insert
    void insert(NotificationItem notification);
    
    @Update
    void update(NotificationItem notification);
    
    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    void markAsRead(long id);
    
    @Query("DELETE FROM notifications WHERE id = :id")
    void deleteById(long id);
    
    @Query("DELETE FROM notifications")
    void deleteAll();
}
