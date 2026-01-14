package com.artem.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.artem.finalproject.database.AppDatabase;
import com.artem.finalproject.database.entity.FavoriteArticle;
import com.artem.finalproject.models.Article;
import com.artem.finalproject.utils.PreferencesHelper;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Активность для детального просмотра новости
 */
public class DetailActivity extends AppCompatActivity {
    
    private Article article;
    private ImageView detailImageView;
    private TextView detailTitleTextView;
    private TextView detailSourceTextView;
    private TextView detailDateTextView;
    private TextView detailAuthorTextView;
    private TextView detailDescriptionTextView;
    private TextView detailContentTextView;
    private FloatingActionButton fabFavorite;
    private MaterialToolbar toolbar;
    private BottomNavigationView bottomNavigation;
    
    private AppDatabase database;
    private ExecutorService executorService;
    private PreferencesHelper preferencesHelper;
    private AtomicBoolean isFavorite = new AtomicBoolean(false);
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        
        // Получаем переданную новость
        article = (Article) getIntent().getSerializableExtra("article");
        if (article == null) {
            Toast.makeText(this, "Error: news not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        initViews();
        setupToolbar();
        setupBottomNavigation();
        loadArticleData();
        checkFavoriteStatus();
        setupListeners();
        
        preferencesHelper = new PreferencesHelper(this);
        if (preferencesHelper.isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }
    
    private void initViews() {
        detailImageView = findViewById(R.id.detailImageView);
        detailTitleTextView = findViewById(R.id.detailTitleTextView);
        detailSourceTextView = findViewById(R.id.detailSourceTextView);
        detailDateTextView = findViewById(R.id.detailDateTextView);
        detailAuthorTextView = findViewById(R.id.detailAuthorTextView);
        detailDescriptionTextView = findViewById(R.id.detailDescriptionTextView);
        detailContentTextView = findViewById(R.id.detailContentTextView);
        fabFavorite = findViewById(R.id.fabFavorite);
        toolbar = findViewById(R.id.toolbar);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        
        database = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();
    }
    
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle("");
        }
    }
    
    private void setupBottomNavigation() {
        if (bottomNavigation != null) {
            bottomNavigation.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
                    return true;
                } else if (itemId == R.id.nav_favorites) {
                    Intent intent = new Intent(DetailActivity.this, FavoritesActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    Intent intent = new Intent(DetailActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    return true;
                }
                return false;
            });
        }
    }
    
    private void loadArticleData() {
        // Заголовок
        detailTitleTextView.setText(article.getTitle());
        
        // Источник
        if (article.getSource() != null && article.getSource().getName() != null) {
            detailSourceTextView.setText(article.getSource().getName());
        }
        
        // Дата
        if (article.getPublishedAt() != null) {
            detailDateTextView.setText(formatDate(article.getPublishedAt()));
        }
        
        // Автор
        if (article.getAuthor() != null && !article.getAuthor().isEmpty()) {
            detailAuthorTextView.setText("Author: " + article.getAuthor());
            detailAuthorTextView.setVisibility(View.VISIBLE);
        }
        
        // Описание
        if (article.getDescription() != null && !article.getDescription().isEmpty()) {
            detailDescriptionTextView.setText(article.getDescription());
        }
        
        // Содержание
        if (article.getContent() != null && !article.getContent().isEmpty()) {
            // Убираем [+XXX chars] в конце
            String content = article.getContent();
            int index = content.indexOf("[+");
            if (index > 0) {
                content = content.substring(0, index).trim();
            }
            detailContentTextView.setText(content);
        } else {
            detailContentTextView.setVisibility(View.GONE);
        }
        
        // Изображение
        if (article.getUrlToImage() != null && !article.getUrlToImage().isEmpty()) {
            Glide.with(this)
                    .load(article.getUrlToImage())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_report_image)
                    .centerCrop()
                    .into(detailImageView);
        } else {
            detailImageView.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }
    
    private void checkFavoriteStatus() {
        executorService.execute(() -> {
            FavoriteArticle favorite = database.favoriteArticleDao().getByUrl(article.getUrl());
            boolean favoriteStatus = favorite != null;
            isFavorite.set(favoriteStatus);
            
            runOnUiThread(() -> updateFavoriteButton(favoriteStatus));
        });
    }
    
    private void updateFavoriteButton(boolean isFavorite) {
        if (isFavorite) {
            fabFavorite.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            fabFavorite.setImageResource(android.R.drawable.btn_star_big_off);
        }
    }
    
    private void setupListeners() {
        // FAB избранного
        fabFavorite.setOnClickListener(v -> {
            boolean newFavoriteState = !isFavorite.get();
            isFavorite.set(newFavoriteState);
            updateFavoriteButton(newFavoriteState);
            
            if (newFavoriteState) {
                saveToFavorites();
            } else {
                removeFromFavorites();
            }
        });
    }
    
    private void saveToFavorites() {
        executorService.execute(() -> {
            FavoriteArticle favorite = new FavoriteArticle();
            favorite.setTitle(article.getTitle());
            favorite.setDescription(article.getDescription());
            favorite.setUrl(article.getUrl());
            favorite.setUrlToImage(article.getUrlToImage());
            favorite.setAuthor(article.getAuthor());
            favorite.setPublishedAt(article.getPublishedAt());
            if (article.getSource() != null) {
                favorite.setSourceName(article.getSource().getName());
            }
            
            database.favoriteArticleDao().insert(favorite);
            runOnUiThread(() -> {
                Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
            });
        });
    }
    
    private void removeFromFavorites() {
        executorService.execute(() -> {
            FavoriteArticle favorite = database.favoriteArticleDao().getByUrl(article.getUrl());
            if (favorite != null) {
                database.favoriteArticleDao().delete(favorite);
            }
            runOnUiThread(() -> {
                Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
            });
        });
    }
    
    
    private String formatDate(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
            Date date = inputFormat.parse(dateString);
            if (date != null) {
                return outputFormat.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateString;
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
