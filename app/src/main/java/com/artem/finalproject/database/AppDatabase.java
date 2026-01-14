package com.artem.finalproject.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.artem.finalproject.database.dao.AppSettingsDao;
import com.artem.finalproject.database.dao.CachedArticleDao;
import com.artem.finalproject.database.dao.CategoryDao;
import com.artem.finalproject.database.dao.FavoriteArticleDao;
import com.artem.finalproject.database.dao.NotificationDao;
import com.artem.finalproject.database.dao.SearchHistoryDao;
import com.artem.finalproject.database.entity.AppSettings;
import com.artem.finalproject.database.entity.CachedArticle;
import com.artem.finalproject.database.entity.Category;
import com.artem.finalproject.database.entity.FavoriteArticle;
import com.artem.finalproject.database.entity.NotificationItem;
import com.artem.finalproject.database.entity.SearchHistory;

/**
 * База данных Room с 6 таблицами
 */
@Database(
    entities = {
        FavoriteArticle.class,
        SearchHistory.class,
        Category.class,
        AppSettings.class,
        NotificationItem.class,
        CachedArticle.class
    },
    version = 1,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;
    
    public abstract FavoriteArticleDao favoriteArticleDao();
    public abstract SearchHistoryDao searchHistoryDao();
    public abstract CategoryDao categoryDao();
    public abstract AppSettingsDao appSettingsDao();
    public abstract NotificationDao notificationDao();
    public abstract CachedArticleDao cachedArticleDao();
    
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "news_database"
            ).build();
        }
        return instance;
    }
}
