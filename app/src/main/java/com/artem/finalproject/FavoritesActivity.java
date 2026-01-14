package com.artem.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.artem.finalproject.database.AppDatabase;
import com.artem.finalproject.database.entity.FavoriteArticle;
import com.artem.finalproject.models.Article;
import com.artem.finalproject.ui.adapter.LatestAdapter;
import com.artem.finalproject.utils.PreferencesHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Активность для просмотра избранных новостей
 */
public class FavoritesActivity extends AppCompatActivity {
    
    private RecyclerView favoritesRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View emptyStateLayout;
    private LatestAdapter latestAdapter;
    private List<Article> articles;
    private BottomNavigationView bottomNavigation;
    
    private AppDatabase database;
    private ExecutorService executorService;
    private PreferencesHelper preferencesHelper;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        
        initViews();
        setupToolbar();
        setupRecyclerView();
        setupBottomNavigation();
        loadFavorites();
        
        preferencesHelper = new PreferencesHelper(this);
        if (preferencesHelper.isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }
    
    private MaterialToolbar toolbar;
    
    private void initViews() {
        favoritesRecyclerView = findViewById(R.id.favoritesRecyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        toolbar = findViewById(R.id.toolbar);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        
        database = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();
        articles = new ArrayList<>();
    }
    
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle(""); // Убираем текст из шапки
        }
    }
    
    private void setupRecyclerView() {
        latestAdapter = new LatestAdapter();
        latestAdapter.setArticles(articles);
        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        favoritesRecyclerView.setAdapter(latestAdapter);
        
        // Обработчик клика на новость
        latestAdapter.setOnItemClickListener(article -> {
            Intent intent = new Intent(FavoritesActivity.this, DetailActivity.class);
            intent.putExtra("article", article);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        
        // Обработчик клика на избранное (удаление)
        latestAdapter.setOnFavoriteClickListener((article, isFavorite) -> {
            if (!isFavorite) {
                removeFromFavorites(article);
            }
        });
        
        // Swipe to refresh
        swipeRefreshLayout.setOnRefreshListener(this::loadFavorites);
    }
    
    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(FavoritesActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
                return true;
            } else if (itemId == R.id.nav_favorites) {
                // Уже на странице избранного
                return true;
            } else if (itemId == R.id.nav_profile) {
                Intent intent = new Intent(FavoritesActivity.this, ProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            }
            return false;
        });
        bottomNavigation.setSelectedItemId(R.id.nav_favorites);
    }
    
    private void loadFavorites() {
        executorService.execute(() -> {
            List<FavoriteArticle> favorites = database.favoriteArticleDao().getAll();
            
            // Конвертируем FavoriteArticle в Article
            List<Article> articleList = new ArrayList<>();
            for (FavoriteArticle favorite : favorites) {
                Article article = new Article();
                article.setTitle(favorite.getTitle());
                article.setDescription(favorite.getDescription());
                article.setUrl(favorite.getUrl());
                article.setUrlToImage(favorite.getUrlToImage());
                article.setAuthor(favorite.getAuthor());
                article.setPublishedAt(favorite.getPublishedAt());
                
                // Создаем Source
                Article.Source source = new Article.Source();
                source.setName(favorite.getSourceName());
                article.setSource(source);
                
                article.setFavorite(true);
                articleList.add(article);
            }
            
            runOnUiThread(() -> {
                articles = articleList;
                latestAdapter.setArticles(articles);
                latestAdapter.notifyDataSetChanged();
                
                // Показываем/скрываем пустое состояние
                if (articles.isEmpty()) {
                    emptyStateLayout.setVisibility(View.VISIBLE);
                    favoritesRecyclerView.setVisibility(View.GONE);
                } else {
                    emptyStateLayout.setVisibility(View.GONE);
                    favoritesRecyclerView.setVisibility(View.VISIBLE);
                }
                
                swipeRefreshLayout.setRefreshing(false);
            });
        });
    }
    
    private void removeFromFavorites(Article article) {
        executorService.execute(() -> {
            FavoriteArticle favorite = database.favoriteArticleDao().getByUrl(article.getUrl());
            if (favorite != null) {
                database.favoriteArticleDao().delete(favorite);
            }
            
            // Перезагружаем список
            loadFavorites();
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
    protected void onResume() {
        super.onResume();
        // Обновляем список при возврате на экран
        loadFavorites();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
