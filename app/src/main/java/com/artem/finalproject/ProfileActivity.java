package com.artem.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.artem.finalproject.database.AppDatabase;
import com.artem.finalproject.database.entity.FavoriteArticle;
import com.artem.finalproject.database.entity.SearchHistory;
import com.artem.finalproject.utils.PreferencesHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Активность профиля и настроек
 */
public class ProfileActivity extends AppCompatActivity {
    
    private MaterialToolbar toolbar;
    private Switch darkModeSwitch;
    private Switch notificationsSwitch;
    private CheckBox autoRefreshCheckBox;
    private TextView favoritesCountTextView;
    private TextView searchHistoryCountTextView;
    private MaterialButton aboutButton;
    private BottomNavigationView bottomNavigation;
    
    private AppDatabase database;
    private ExecutorService executorService;
    private Handler mainHandler;
    private PreferencesHelper preferencesHelper;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        
        initViews();
        initUtils();
        setupToolbar();
        setupBottomNavigation();
        setupListeners();
        loadPreferences();
        loadStatistics();
        
        preferencesHelper = new PreferencesHelper(this);
        if (preferencesHelper.isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }
    
    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        notificationsSwitch = findViewById(R.id.notificationsSwitch);
        autoRefreshCheckBox = findViewById(R.id.autoRefreshCheckBox);
        favoritesCountTextView = findViewById(R.id.favoritesCountTextView);
        searchHistoryCountTextView = findViewById(R.id.searchHistoryCountTextView);
        aboutButton = findViewById(R.id.aboutButton);
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }
    
    private void initUtils() {
        database = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
        preferencesHelper = new PreferencesHelper(this);
    }
    
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle(""); // Убираем текст из шапки
        }
    }
    
    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
                return true;
            } else if (itemId == R.id.nav_favorites) {
                Intent intent = new Intent(ProfileActivity.this, FavoritesActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            } else if (itemId == R.id.nav_profile) {
                // Уже на странице профиля
                return true;
            }
            return false;
        });
        bottomNavigation.setSelectedItemId(R.id.nav_profile);
    }
    
    private void setupListeners() {
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferencesHelper.setDarkMode(isChecked);
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });
        
        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferencesHelper.setNotificationsEnabled(isChecked);
            Toast.makeText(this, isChecked ? "Notifications enabled" : "Notifications disabled", 
                    Toast.LENGTH_SHORT).show();
        });
        
        autoRefreshCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferencesHelper.setAutoRefresh(isChecked);
        });
        
        aboutButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("About");
            builder.setMessage("NewsAPI Client\n\n" +
                    "Version: 1.0\n" +
                    "Developer: Artem\n\n" +
                    "News viewing app using NewsAPI.\n\n" +
                    "© 2026");
            builder.setPositiveButton("OK", null);
            builder.show();
        });
    }
    
    private void loadPreferences() {
        darkModeSwitch.setChecked(preferencesHelper.isDarkMode());
        notificationsSwitch.setChecked(preferencesHelper.isNotificationsEnabled());
        autoRefreshCheckBox.setChecked(preferencesHelper.isAutoRefresh());
    }
    
    private void loadStatistics() {
        executorService.execute(() -> {
            List<FavoriteArticle> favorites = database.favoriteArticleDao().getAll();
            List<SearchHistory> history = database.searchHistoryDao().getAll();
            
            mainHandler.post(() -> {
                favoritesCountTextView.setText(String.valueOf(favorites.size()));
                searchHistoryCountTextView.setText(String.valueOf(history.size()));
            });
        });
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
