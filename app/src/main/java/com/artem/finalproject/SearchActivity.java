package com.artem.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.artem.finalproject.api.ApiClient;
import com.artem.finalproject.api.NewsApiService;
import com.artem.finalproject.database.AppDatabase;
import com.artem.finalproject.database.entity.SearchHistory;
import com.artem.finalproject.models.Article;
import com.artem.finalproject.models.NewsResponse;
import com.artem.finalproject.ui.adapter.LatestAdapter;
import com.artem.finalproject.utils.PreferencesHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Активность для поиска новостей
 */
public class SearchActivity extends AppCompatActivity {
    
    private TextInputEditText searchEditText;
    private MaterialButton searchButton;
    private RecyclerView historyRecyclerView;
    private RecyclerView resultsRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private TextView errorTextView;
    private TextView resultsTitle;
    private MaterialToolbar toolbar;
    private BottomNavigationView bottomNavigation;
    
    private LatestAdapter resultsAdapter;
    private List<Article> searchResults;
    private List<SearchHistory> searchHistory;
    
    private AppDatabase database;
    private ExecutorService executorService;
    private Handler mainHandler;
    private NewsApiService apiService;
    private PreferencesHelper preferencesHelper;
    
    // ВАЖНО: Получите бесплатный API ключ на https://newsapi.org/ и замените значение ниже
    // См. инструкцию в файле API_KEY_SETUP.md
    private static final String API_KEY = "YOUR_API_KEY_HERE"; // Замените на свой API ключ от NewsAPI
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        
        initViews();
        initUtils();
        setupToolbar();
        setupRecyclerView();
        setupBottomNavigation();
        setupListeners();
        loadSearchHistory();
        
        // Проверяем, есть ли переданный запрос из MainActivity
        String searchQuery = getIntent().getStringExtra("search_query");
        String searchType = getIntent().getStringExtra("search_type");
        if (searchQuery != null && searchType != null) {
            if ("trending".equals(searchType)) {
                searchEditText.setText("trending");
                searchNews("trending");
            } else if ("latest".equals(searchType)) {
                searchEditText.setText("latest");
                searchNews("latest");
            }
        }
        
        preferencesHelper = new PreferencesHelper(this);
        if (preferencesHelper.isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }
    
    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
        historyRecyclerView = findViewById(R.id.historyRecyclerView);
        resultsRecyclerView = findViewById(R.id.resultsRecyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        progressBar = findViewById(R.id.progressBar);
        errorTextView = findViewById(R.id.errorTextView);
        resultsTitle = findViewById(R.id.resultsTitle);
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }
    
    private void initUtils() {
        database = AppDatabase.getInstance(this);
        executorService = Executors.newFixedThreadPool(2);
        mainHandler = new Handler(Looper.getMainLooper());
        apiService = ApiClient.getApiService();
        searchResults = new ArrayList<>();
        searchHistory = new ArrayList<>();
    }
    
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle(""); // Убираем текст из шапки
        }
    }
    
    private void setupRecyclerView() {
        resultsAdapter = new LatestAdapter();
        resultsAdapter.setArticles(searchResults);
        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        resultsRecyclerView.setAdapter(resultsAdapter);
        
        resultsAdapter.setOnItemClickListener(article -> {
            Intent intent = new Intent(SearchActivity.this, DetailActivity.class);
            intent.putExtra("article", article);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        
        // История поиска
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    
    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
                return true;
            } else if (itemId == R.id.nav_favorites) {
                Intent intent = new Intent(SearchActivity.this, FavoritesActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            } else if (itemId == R.id.nav_profile) {
                Intent intent = new Intent(SearchActivity.this, ProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            }
            return false;
        });
        // Search вкладка временно отключена
    }
    
    private void setupListeners() {
        searchButton.setOnClickListener(v -> {
            String query = searchEditText.getText().toString().trim();
            if (!query.isEmpty()) {
                searchNews(query);
                saveSearchHistory(query);
            } else {
                Toast.makeText(this, "Enter search query", Toast.LENGTH_SHORT).show();
            }
        });
        
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            String query = searchEditText.getText().toString().trim();
            if (!query.isEmpty()) {
                searchNews(query);
                saveSearchHistory(query);
            }
            return true;
        });
        
        swipeRefreshLayout.setOnRefreshListener(() -> {
            String query = searchEditText.getText().toString().trim();
            if (!query.isEmpty()) {
                searchNews(query);
            } else {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
    
    private void loadSearchHistory() {
        executorService.execute(() -> {
            searchHistory = database.searchHistoryDao().getAll();
            mainHandler.post(() -> {
                // Можно добавить адаптер для истории поиска
            });
        });
    }
    
    private void searchNews(String query) {
        if (API_KEY == null || API_KEY.isEmpty() || API_KEY.equals("YOUR_API_KEY_HERE")) {
            showError("Error: API key not set!");
            return;
        }
        
        showProgress(true);
        resultsTitle.setVisibility(View.GONE);
        resultsRecyclerView.setVisibility(View.GONE);
        
        executorService.execute(() -> {
            Call<NewsResponse> call = apiService.getNews(
                    query,
                    API_KEY,
                    20,
                    "publishedAt",
                    "ru"
            );
            
            call.enqueue(new Callback<NewsResponse>() {
                @Override
                public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                    mainHandler.post(() -> {
                        showProgress(false);
                        swipeRefreshLayout.setRefreshing(false);
                        
                        if (response.isSuccessful() && response.body() != null) {
                            NewsResponse newsResponse = response.body();
                            if (newsResponse.getStatus() != null && newsResponse.getStatus().equals("ok")) {
                                List<Article> articles = newsResponse.getArticles();
                                if (articles != null && !articles.isEmpty()) {
                                    searchResults = articles;
                                    resultsAdapter.setArticles(searchResults);
                                    resultsTitle.setVisibility(View.VISIBLE);
                                    resultsTitle.setText("Search Results (" + articles.size() + ")");
                                    resultsRecyclerView.setVisibility(View.VISIBLE);
                                    errorTextView.setVisibility(View.GONE);
                                } else {
                                    showError("No news found for query: \"" + query + "\"");
                                }
                            } else {
                                showError("API Error: " + newsResponse.getStatus());
                            }
                        } else {
                            showError("Error loading news (code: " + response.code() + ")");
                        }
                    });
                }
                
                @Override
                public void onFailure(Call<NewsResponse> call, Throwable t) {
                    mainHandler.post(() -> {
                        showProgress(false);
                        swipeRefreshLayout.setRefreshing(false);
                        showError("Connection error: " + t.getMessage());
                    });
                }
            });
        });
    }
    
    private void saveSearchHistory(String query) {
        executorService.execute(() -> {
            SearchHistory history = new SearchHistory();
            history.setQuery(query);
            history.setResultCount(0);
            database.searchHistoryDao().insert(history);
            loadSearchHistory();
        });
    }
    
    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            errorTextView.setVisibility(View.GONE);
        }
    }
    
    private void showError(String message) {
        errorTextView.setText(message);
        errorTextView.setVisibility(View.VISIBLE);
        resultsRecyclerView.setVisibility(View.GONE);
        resultsTitle.setVisibility(View.GONE);
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
