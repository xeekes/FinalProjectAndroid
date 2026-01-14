package com.artem.finalproject.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Утилита для работы с SharedPreferences
 */
public class PreferencesHelper {
    private static final String PREFS_NAME = "news_app_prefs";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_NOTIFICATIONS_ENABLED = "notifications_enabled";
    private static final String KEY_AUTO_REFRESH = "auto_refresh";
    private static final String KEY_API_KEY = "api_key";
    private static final String KEY_SELECTED_CATEGORY = "selected_category";
    private static final String KEY_SORT_BY = "sort_by";
    
    private SharedPreferences prefs;
    
    public PreferencesHelper(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    public boolean isDarkMode() {
        return prefs.getBoolean(KEY_DARK_MODE, false);
    }
    
    public void setDarkMode(boolean enabled) {
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply();
    }
    
    public boolean isNotificationsEnabled() {
        return prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true);
    }
    
    public void setNotificationsEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply();
    }
    
    public boolean isAutoRefresh() {
        return prefs.getBoolean(KEY_AUTO_REFRESH, false);
    }
    
    public void setAutoRefresh(boolean enabled) {
        prefs.edit().putBoolean(KEY_AUTO_REFRESH, enabled).apply();
    }
    
    public String getApiKey() {
        return prefs.getString(KEY_API_KEY, "");
    }
    
    public void setApiKey(String apiKey) {
        prefs.edit().putString(KEY_API_KEY, apiKey).apply();
    }
    
    public String getSelectedCategory() {
        return prefs.getString(KEY_SELECTED_CATEGORY, "general");
    }
    
    public void setSelectedCategory(String category) {
        prefs.edit().putString(KEY_SELECTED_CATEGORY, category).apply();
    }
    
    public String getSortBy() {
        return prefs.getString(KEY_SORT_BY, "publishedAt");
    }
    
    public void setSortBy(String sortBy) {
        prefs.edit().putString(KEY_SORT_BY, sortBy).apply();
    }
}
