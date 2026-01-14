package com.artem.finalproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.artem.finalproject.api.ApiClient;
import com.artem.finalproject.api.NewsApiService;
import com.artem.finalproject.database.AppDatabase;
import com.artem.finalproject.database.entity.FavoriteArticle;
import com.artem.finalproject.models.Article;
import com.artem.finalproject.models.NewsResponse;
import com.artem.finalproject.ui.adapter.NewsAdapter;
import com.artem.finalproject.ui.adapter.TrendingAdapter;
import com.artem.finalproject.ui.adapter.LatestAdapter;
import com.artem.finalproject.utils.NotificationHelper;
import com.artem.finalproject.utils.PreferencesHelper;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Главная активность приложения - клиент для NewsAPI
 */
public class MainActivity extends AppCompatActivity {
    
    // Виджеты
    private TextInputEditText searchEditText;
    private ImageView profileImageView;
    private TextView greetingTextView;
    private TextView dateHeaderTextView;
    private ImageButton notificationsButton;
    private RecyclerView newsRecyclerView;
    private RecyclerView trendingRecyclerView;
    private LinearLayout categoriesContainer;
    private TextView viewMoreTrending;
    private TextView viewMoreLatest;
    private ProgressBar progressBar;
    private TextView errorTextView;
    private com.google.android.material.bottomnavigation.BottomNavigationView bottomNavigation;
    
    // Адаптеры и данные
    private TrendingAdapter trendingAdapter;
    private LatestAdapter latestAdapter;
    private List<Article> trendingArticles;
    private List<Article> latestArticles;
    
    // Утилиты
    private PreferencesHelper preferencesHelper;
    private NotificationHelper notificationHelper;
    private AppDatabase database;
    private ExecutorService executorService;
    private Handler mainHandler;
    
    // API
    private NewsApiService apiService;
    // ВАЖНО: Получите бесплатный API ключ на https://newsapi.org/ и замените значение ниже
    // См. инструкцию в файле API_KEY_SETUP.md
    private static final String API_KEY = "YOUR_API_KEY_HERE"; // Замените на свой API ключ от NewsAPI
    
    // Категории новостей
    private static final String[] CATEGORIES = {
        "general", "business", "entertainment", "health", "science", "sports", "technology"
    };
    
    private static final String[] CATEGORY_NAMES = {
        "General", "Business", "Entertainment", "Health", "Science", "Sports", "Technology"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Инициализация
        initViews();
        initUtils();
        setupRecyclerView();
        setupTrendingRecyclerView();
        setupCategories();
        setupBottomNavigation();
        setupListeners();
        loadPreferences();
        updateGreeting();
        
        // Загрузка новостей по умолчанию
        loadTopHeadlines();
        
        // Обработка window insets для статус бара
        setupWindowInsets();
        
        // Устанавливаем белый цвет для навигационной панели
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
            getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        }
        
        // Для Android 11+ используем WindowInsetsController
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            getWindow().getInsetsController().setSystemBarsAppearance(
                    0, // Скрываем системные панели
                    android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
            );
        }
    }
    
    private void setupWindowInsets() {
        // AppBarLayout и layout_behavior автоматически обрабатывают позиционирование
        // Не нужно добавлять padding вручную, так как это создает двойной отступ
        // Оставляем метод пустым для возможных будущих изменений
    }
    
    private void initViews() {
        // Инициализация Toolbar
        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                getSupportActionBar().setTitle(""); // Убираем текст из шапки
            }
        }
        
        searchEditText = findViewById(R.id.searchEditText);
        profileImageView = findViewById(R.id.profileImageView);
        greetingTextView = findViewById(R.id.greetingTextView);
        dateHeaderTextView = findViewById(R.id.dateTextView);
        notificationsButton = findViewById(R.id.notificationsButton);
        categoriesContainer = findViewById(R.id.categoriesContainer);
        trendingRecyclerView = findViewById(R.id.trendingRecyclerView);
        viewMoreTrending = findViewById(R.id.viewMoreTrending);
        viewMoreLatest = findViewById(R.id.viewMoreLatest);
        newsRecyclerView = findViewById(R.id.newsRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        errorTextView = findViewById(R.id.errorTextView);
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }
    
    private void initUtils() {
        preferencesHelper = new PreferencesHelper(this);
        notificationHelper = new NotificationHelper(this);
        database = AppDatabase.getInstance(this);
        executorService = Executors.newFixedThreadPool(2);
        mainHandler = new Handler(Looper.getMainLooper());
        apiService = ApiClient.getApiService();
        trendingArticles = new ArrayList<>();
        latestArticles = new ArrayList<>();
    }
    
    private void setupRecyclerView() {
        // Latest новости (вертикальный список)
        latestAdapter = new LatestAdapter();
        latestAdapter.setArticles(latestArticles);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setAutoMeasureEnabled(true); // Включаем автоматическое измерение
        newsRecyclerView.setLayoutManager(layoutManager);
        newsRecyclerView.setNestedScrollingEnabled(false);
        newsRecyclerView.setHasFixedSize(false); // Должно быть false для правильного измерения внутри ScrollView
        newsRecyclerView.setAdapter(latestAdapter);
        newsRecyclerView.setVisibility(View.VISIBLE);
        
        // Обработчик клика на новость
        latestAdapter.setOnItemClickListener(article -> {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("article", article);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }
    
    private void setupTrendingRecyclerView() {
        // Trending новости (горизонтальный список)
        trendingAdapter = new TrendingAdapter();
        trendingAdapter.setArticles(trendingArticles);
        trendingRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        trendingRecyclerView.setAdapter(trendingAdapter);
        
        // Обработчик клика
        trendingAdapter.setOnItemClickListener(article -> {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("article", article);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }
    
    private void setupCategories() {
        categoriesContainer.removeAllViews();
        
        // Add "All" as first category
        String[] allCategories = new String[CATEGORY_NAMES.length + 1];
        String[] allCategoryKeys = new String[CATEGORIES.length + 1];
        allCategories[0] = "All";
        allCategoryKeys[0] = "all";
        System.arraycopy(CATEGORY_NAMES, 0, allCategories, 1, CATEGORY_NAMES.length);
        System.arraycopy(CATEGORIES, 0, allCategoryKeys, 1, CATEGORIES.length);
        
        for (int i = 0; i < allCategories.length; i++) {
            TextView categoryTab = new TextView(this);
            categoryTab.setText(allCategories[i]);
            categoryTab.setPadding(50, 20, 50, 20);  // Увеличен горизонтальный padding
            categoryTab.setTextSize(13);  // Уменьшен размер текста
            categoryTab.setBackgroundResource(R.drawable.category_tab_background);
            categoryTab.setSelected(i == 0); // Первая категория выбрана по умолчанию
            
            // Устанавливаем цвета текста
            if (i == 0) {
                categoryTab.setTextColor(getResources().getColor(R.color.category_selected_text));
            } else {
                categoryTab.setTextColor(getResources().getColor(R.color.category_unselected_text));
            }
            
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 20, 0);  // Увеличено расстояние между тегами
            categoryTab.setLayoutParams(params);
            
            final int categoryIndex = i;
            categoryTab.setOnClickListener(v -> {
                // Очищаем поисковую строку при выборе категории
                searchEditText.setText("");
                
                // Снимаем выделение со всех табов
                for (int j = 0; j < categoriesContainer.getChildCount(); j++) {
                    View tab = categoriesContainer.getChildAt(j);
                    tab.setSelected(false);
                    if (tab instanceof TextView) {
                        ((TextView) tab).setTextColor(getResources().getColor(R.color.category_unselected_text));
                    }
                }
                // Выделяем выбранный таб
                v.setSelected(true);
                if (v instanceof TextView) {
                    ((TextView) v).setTextColor(getResources().getColor(R.color.category_selected_text));
                }
                // Загружаем новости выбранной категории
                if (categoryIndex == 0) {
                    loadTopHeadlines();
                } else {
                    loadCategoryNews(allCategoryKeys[categoryIndex]);
                }
            });
            
            categoriesContainer.addView(categoryTab);
        }
    }
    
    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            
            if (itemId == R.id.nav_home) {
                // Уже на главной
                return true;
            } else if (itemId == R.id.nav_favorites) {
                Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            } else if (itemId == R.id.nav_profile) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            }
            return false;
        });
        bottomNavigation.setSelectedItemId(R.id.nav_home);
    }
    
    private void updateGreeting() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        int hour = calendar.get(java.util.Calendar.HOUR_OF_DAY);
        
        String greeting = "Hi";
        
        greetingTextView.setText(greeting + ", User");
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd", Locale.ENGLISH);
        dateHeaderTextView.setText("Today, " + dateFormat.format(calendar.getTime()));
    }
    
    private void loadCategoryNews(String category) {
        showProgress(true);
        
        executorService.execute(() -> {
            Call<NewsResponse> call = apiService.getTopHeadlines(
                    category,
                    API_KEY,
                    50,
                    "us"
            );
            
            call.enqueue(new Callback<NewsResponse>() {
                @Override
                public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                    mainHandler.post(() -> {
                        showProgress(false);
                        
                        if (response.isSuccessful() && response.body() != null) {
                            NewsResponse newsResponse = response.body();
                            if (newsResponse.getStatus() != null && newsResponse.getStatus().equals("ok")) {
                                List<Article> allArticles = newsResponse.getArticles();
                                if (allArticles != null && !allArticles.isEmpty()) {
                                    android.util.Log.d("NewsAPI", "Category articles loaded: " + allArticles.size());
                                    
                                    // Разделяем на Trending (первые 5) и Latest (максимум 20)
                                    int trendingCount = Math.min(5, allArticles.size());
                                    trendingArticles = new ArrayList<>(allArticles.subList(0, trendingCount));
                                    if (allArticles.size() > trendingCount) {
                                        // Ограничиваем Latest до 20 карточек
                                        int latestStart = trendingCount;
                                        int latestEnd = Math.min(latestStart + 20, allArticles.size());
                                        latestArticles = new ArrayList<>(allArticles.subList(latestStart, latestEnd));
                                    } else {
                                        latestArticles = new ArrayList<>();
                                    }
                                    
                                    android.util.Log.d("NewsAPI", "Category - Trending: " + trendingArticles.size() + ", Latest: " + latestArticles.size());
                                    
                                    trendingAdapter.setArticles(trendingArticles);
                                    trendingAdapter.notifyDataSetChanged();
                                    latestAdapter.setArticles(latestArticles);
                                    latestAdapter.notifyDataSetChanged();
                                    
                                    // Принудительно запрашиваем перерисовку RecyclerView для правильного измерения
                                    trendingRecyclerView.post(() -> {
                                        trendingRecyclerView.requestLayout();
                                    });
                                    newsRecyclerView.post(() -> {
                                        newsRecyclerView.requestLayout();
                                    });
                                    
                                    // Показываем оба RecyclerView
                                    trendingRecyclerView.setVisibility(View.VISIBLE);
                                    newsRecyclerView.setVisibility(View.VISIBLE);
                                    
                                    errorTextView.setVisibility(View.GONE);
                                }
                            }
                        }
                    });
                }
                
                @Override
                public void onFailure(Call<NewsResponse> call, Throwable t) {
                    mainHandler.post(() -> {
                        showProgress(false);
                        showError("Ошибка загрузки новостей");
                    });
                }
            });
        });
    }
    
    
    private void setupListeners() {
        // Поиск при нажатии Enter
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            String query = searchEditText.getText().toString().trim();
            if (!query.isEmpty()) {
                // Сбрасываем выбор категории при поиске
                for (int j = 0; j < categoriesContainer.getChildCount(); j++) {
                    View tab = categoriesContainer.getChildAt(j);
                    tab.setSelected(false);
                    if (tab instanceof TextView) {
                        ((TextView) tab).setTextColor(getResources().getColor(R.color.category_unselected_text));
                    }
                }
                // Выделяем первую категорию "All" при поиске
                if (categoriesContainer.getChildCount() > 0) {
                    View firstTab = categoriesContainer.getChildAt(0);
                    firstTab.setSelected(true);
                    if (firstTab instanceof TextView) {
                        ((TextView) firstTab).setTextColor(getResources().getColor(R.color.category_selected_text));
                    }
                }
                searchNews(query);
                saveSearchHistory(query);
            } else {
                // Если поиск пустой, загружаем топ новости
                loadTopHeadlines();
            }
            return true;
        });
        
        // Кнопка профиля
        if (profileImageView != null) {
            profileImageView.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }
        
        // Кнопка уведомлений
        if (notificationsButton != null) {
            notificationsButton.setOnClickListener(v -> {
                showNotificationsDialog();
            });
        }
        
        // Кнопка фильтра
        ImageButton filterButton = findViewById(R.id.filterButton);
        if (filterButton != null) {
            filterButton.setOnClickListener(v -> {
                showFilterDialog();
            });
        }
        
        // Swipe to refresh отключен для правильного скроллинга
        
        // View More для Trending
        if (viewMoreTrending != null) {
            viewMoreTrending.setOnClickListener(v -> {
                showAllTrendingNews();
            });
        }
        
        // View More для Latest
        if (viewMoreLatest != null) {
            viewMoreLatest.setOnClickListener(v -> {
                showAllLatestNews();
            });
        }
    }
    
    private void loadPreferences() {
        if (preferencesHelper.isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }
    
    private void loadTopHeadlines() {
        // Проверка API ключа
        if (API_KEY == null || API_KEY.isEmpty() || API_KEY.equals("YOUR_API_KEY_HERE")) {
            showProgress(false);
            showError("Error: API key not set!\n\nGet a free key at:\nhttps://newsapi.org/\n\nAnd replace API_KEY in MainActivity.java");
            showApiKeyDialog();
            return;
        }
        
        showProgress(true);
        String category = "general"; // Категория по умолчанию
        
        executorService.execute(() -> {
            Call<NewsResponse> call = apiService.getTopHeadlines(
                    category,
                    API_KEY,
                    50,
                    "us"
            );
            
            call.enqueue(new Callback<NewsResponse>() {
                @Override
                public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                    mainHandler.post(() -> {
                        showProgress(false);
                        
                        // Проверка типа контента
                        String contentType = response.headers().get("Content-Type");
                        if (contentType != null && contentType.contains("text/html")) {
                            android.util.Log.e("NewsAPI", "Received HTML instead of JSON!");
                            showError("Error: API returned HTML instead of JSON\n\n" +
                                    "This means:\n" +
                                    "1. API key is invalid or expired\n" +
                                    "2. Request is blocked by protection\n\n" +
                                    "Check API key at https://newsapi.org/account");
                            return;
                        }
                        
                        if (response.isSuccessful() && response.body() != null) {
                            NewsResponse newsResponse = response.body();
                            android.util.Log.d("NewsAPI", "Response status: " + newsResponse.getStatus());
                            android.util.Log.d("NewsAPI", "Total results: " + newsResponse.getTotalResults());
                            
                            // Логируем полный JSON ответ для просмотра формата данных
                            try {
                                com.google.gson.Gson gson = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
                                String jsonResponse = gson.toJson(newsResponse);
                                android.util.Log.d("NewsAPI", "Full JSON Response:\n" + jsonResponse);
                            } catch (Exception e) {
                                android.util.Log.e("NewsAPI", "Error logging JSON", e);
                            }
                            
                            // Проверка статуса ответа
                            if (newsResponse.getStatus() != null && newsResponse.getStatus().equals("ok")) {
                                List<Article> allArticles = newsResponse.getArticles();
                                if (allArticles != null && !allArticles.isEmpty()) {
                                    android.util.Log.d("NewsAPI", "Articles loaded: " + allArticles.size());
                                    
                                    // Разделяем на Trending (первые 5) и Latest (максимум 20)
                                    int trendingCount = Math.min(5, allArticles.size());
                                    trendingArticles = new ArrayList<>(allArticles.subList(0, trendingCount));
                                    // Ограничиваем Latest до 20 карточек
                                    int latestStart = trendingCount;
                                    int latestEnd = Math.min(latestStart + 20, allArticles.size());
                                    latestArticles = new ArrayList<>(allArticles.subList(latestStart, latestEnd));
                                    
                                    android.util.Log.d("NewsAPI", "Trending: " + trendingArticles.size() + ", Latest: " + latestArticles.size());
                                    
                                    trendingAdapter.setArticles(trendingArticles);
                                    trendingAdapter.notifyDataSetChanged();
                                    latestAdapter.setArticles(latestArticles);
                                    latestAdapter.notifyDataSetChanged();
                                    
                                    // Принудительно запрашиваем перерисовку RecyclerView для правильного измерения
                                    newsRecyclerView.post(() -> {
                                        newsRecyclerView.requestLayout();
                                    });
                                    
                                    errorTextView.setVisibility(View.GONE);
                                    trendingRecyclerView.setVisibility(View.VISIBLE);
                                    newsRecyclerView.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                    
                                    // Показываем уведомление
                                    if (preferencesHelper.isNotificationsEnabled()) {
                                        notificationHelper.showNotification(
                                                "New news",
                                                "Loaded " + allArticles.size() + " news",
                                                null
                                        );
                                    }
                                } else {
                                    android.util.Log.w("NewsAPI", "Articles list is null or empty");
                                    showError("No news found for category: " + category);
                                }
                            } else {
                                android.util.Log.e("NewsAPI", "API returned error status: " + newsResponse.getStatus());
                                showError("API Error: " + (newsResponse.getStatus() != null ? newsResponse.getStatus() : "unknown"));
                            }
                        } else {
                            String errorMessage = "Error loading news (code: " + response.code() + ")";
                            if (response.code() == 401) {
                                errorMessage = "Error: Invalid API key!\n\nCheck the key in MainActivity.java";
                            } else if (response.code() == 429) {
                                errorMessage = "Request limit exceeded.\nTry again later.";
                            } else if (response.errorBody() != null) {
                                try {
                                    String errorBody = response.errorBody().string();
                                    android.util.Log.e("NewsAPI", "Error response: " + errorBody);
                                    
                                    // Проверка на HTML ответ (Cloudflare защита)
                                    if (errorBody.contains("<!DOCTYPE html") || errorBody.contains("<html") || errorBody.contains("Just a moment")) {
                                        errorMessage = "Error: API returned HTML instead of JSON\n\n" +
                                                "Possible reasons:\n" +
                                                "1. API key is invalid or expired\n" +
                                                "2. Request is blocked by protection\n" +
                                                "3. Connection problem\n\n" +
                                                "Check API key at https://newsapi.org/account";
                                    } else if (errorBody.contains("apiKey") || errorBody.contains("Invalid API key")) {
                                        errorMessage = "Error: API key problem\n\n" + errorBody;
                                    } else if (errorBody.contains("rateLimit")) {
                                        errorMessage = "Request limit exceeded";
                                    } else {
                                        errorMessage = "API Error: " + (errorBody.length() > 200 ? errorBody.substring(0, 200) + "..." : errorBody);
                                    }
                                } catch (Exception e) {
                                    android.util.Log.e("NewsAPI", "Error parsing response", e);
                                    errorMessage = "Error loading (code " + response.code() + ")";
                                }
                            }
                            showError(errorMessage);
                        }
                    });
                }
                
                @Override
                public void onFailure(Call<NewsResponse> call, Throwable t) {
                    mainHandler.post(() -> {
                        showProgress(false);
                        String errorMsg = "Connection error";
                        if (t.getMessage() != null) {
                            if (t.getMessage().contains("Unable to resolve host")) {
                                errorMsg = "No internet connection";
                            } else if (t.getMessage().contains("timeout")) {
                                errorMsg = "Connection timeout";
                            } else {
                                errorMsg = "Connection error: " + t.getMessage();
                            }
                        }
                        showError(errorMsg);
                    });
                }
            });
        });
    }
    
    private void searchNews(String query) {
        // Проверка API ключа
        if (API_KEY == null || API_KEY.isEmpty() || API_KEY.equals("YOUR_API_KEY_HERE")) {
            showProgress(false);
            showError("Error: API key not set!\n\nGet a free key at:\nhttps://newsapi.org/");
            showApiKeyDialog();
            return;
        }
        
        showProgress(true);
        String sortBy = "publishedAt"; // Сортировка по дате по умолчанию
        
        executorService.execute(() -> {
            Call<NewsResponse> call = apiService.getNews(
                    query,
                    API_KEY,
                    20,
                    sortBy,
                    "ru" // Язык для поиска (можно изменить на "en" для английского)
            );
            
            call.enqueue(new Callback<NewsResponse>() {
                @Override
                public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                    mainHandler.post(() -> {
                        showProgress(false);
                        
                        // Проверка типа контента
                        String contentType = response.headers().get("Content-Type");
                        if (contentType != null && contentType.contains("text/html")) {
                            android.util.Log.e("NewsAPI", "Search: Received HTML instead of JSON!");
                            showError("Error: API returned HTML instead of JSON\n\n" +
                                    "This means:\n" +
                                    "1. API key is invalid or expired\n" +
                                    "2. Request is blocked by protection\n\n" +
                                    "Check API key at https://newsapi.org/account");
                            return;
                        }
                        
                        if (response.isSuccessful() && response.body() != null) {
                            NewsResponse newsResponse = response.body();
                            android.util.Log.d("NewsAPI", "Search response status: " + newsResponse.getStatus());
                            android.util.Log.d("NewsAPI", "Search total results: " + newsResponse.getTotalResults());
                            
                            // Логируем полный JSON ответ для просмотра формата данных
                            try {
                                com.google.gson.Gson gson = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
                                String jsonResponse = gson.toJson(newsResponse);
                                android.util.Log.d("NewsAPI", "Full JSON Response:\n" + jsonResponse);
                            } catch (Exception e) {
                                android.util.Log.e("NewsAPI", "Error logging JSON", e);
                            }
                            
                            // Проверка статуса ответа
                            if (newsResponse.getStatus() != null && newsResponse.getStatus().equals("ok")) {
                                List<Article> searchArticles = newsResponse.getArticles();
                                if (searchArticles != null && !searchArticles.isEmpty()) {
                                    android.util.Log.d("NewsAPI", "Search articles loaded: " + searchArticles.size());
                                    
                                    // Разделяем на Trending (первые 5) и Latest (максимум 20)
                                    int trendingCount = Math.min(5, searchArticles.size());
                                    trendingArticles = new ArrayList<>(searchArticles.subList(0, trendingCount));
                                    int latestStart = trendingCount;
                                    int latestEnd = Math.min(latestStart + 20, searchArticles.size());
                                    latestArticles = new ArrayList<>(searchArticles.subList(latestStart, latestEnd));
                                    
                                    trendingAdapter.setArticles(trendingArticles);
                                    trendingAdapter.notifyDataSetChanged();
                                    latestAdapter.setArticles(latestArticles);
                                    latestAdapter.notifyDataSetChanged();
                                    
                                    // Принудительно запрашиваем перерисовку RecyclerView для правильного измерения
                                    trendingRecyclerView.post(() -> {
                                        trendingRecyclerView.requestLayout();
                                    });
                                    newsRecyclerView.post(() -> {
                                        newsRecyclerView.requestLayout();
                                    });
                                    
                                    // Показываем оба RecyclerView
                                    trendingRecyclerView.setVisibility(View.VISIBLE);
                                    newsRecyclerView.setVisibility(View.VISIBLE);
                                    
                                    errorTextView.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.GONE);
                                    
                                    // Сохранение в кэш
                                    saveToCache(searchArticles, query);
                                    
                                    // Показываем Toast для подтверждения
                                    Toast.makeText(MainActivity.this, 
                                            "Found news: " + searchArticles.size(), 
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    android.util.Log.w("NewsAPI", "Search: Articles list is null or empty");
                                    showError("No news found for query: \"" + query + "\"\n\nTry another query");
                                }
                            } else {
                                android.util.Log.e("NewsAPI", "Search API returned error status: " + newsResponse.getStatus());
                                showError("API error during search: " + (newsResponse.getStatus() != null ? newsResponse.getStatus() : "unknown"));
                            }
                        } else {
                            String errorMessage = "Ошибка загрузки новостей (код: " + response.code() + ")";
                            if (response.code() == 401) {
                                errorMessage = "Ошибка: Неверный API ключ!";
                            } else if (response.code() == 429) {
                                errorMessage = "Превышен лимит запросов.\nПопробуйте позже.";
                            } else if (response.errorBody() != null) {
                                try {
                                    String errorBody = response.errorBody().string();
                                    android.util.Log.e("NewsAPI", "Search error: " + errorBody);
                                    
                                    // Проверка на HTML ответ (Cloudflare защита)
                                    if (errorBody.contains("<!DOCTYPE html") || errorBody.contains("<html") || errorBody.contains("Just a moment")) {
                                        errorMessage = "Ошибка: API вернул HTML вместо JSON\n\n" +
                                                "Возможные причины:\n" +
                                                "1. API ключ неверный или истек\n" +
                                                "2. Запрос блокируется защитой\n" +
                                                "3. Проблема с подключением\n\n" +
                                                "Проверьте API ключ на https://newsapi.org/account";
                                    } else if (errorBody.contains("apiKey") || errorBody.contains("Invalid API key")) {
                                        errorMessage = "Ошибка: Проблема с API ключом\n\n" + errorBody;
                                    } else {
                                        errorMessage = "Ошибка API: " + (errorBody.length() > 200 ? errorBody.substring(0, 200) + "..." : errorBody);
                                    }
                                } catch (Exception e) {
                                    android.util.Log.e("NewsAPI", "Error parsing search response", e);
                                }
                            }
                            showError(errorMessage);
                        }
                    });
                }
                
                @Override
                public void onFailure(Call<NewsResponse> call, Throwable t) {
                    mainHandler.post(() -> {
                        showProgress(false);
                        String errorMsg = "Connection error";
                        if (t.getMessage() != null) {
                            if (t.getMessage().contains("Unable to resolve host")) {
                                errorMsg = "No internet connection";
                            } else if (t.getMessage().contains("timeout")) {
                                errorMsg = "Connection timeout";
                            } else {
                                errorMsg = "Connection error: " + t.getMessage();
                            }
                        }
                        showError(errorMsg);
                    });
                }
            });
        });
    }
    
    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        newsRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
    
    private void showError(String message) {
        errorTextView.setText(message);
        errorTextView.setVisibility(View.VISIBLE);
        newsRecyclerView.setVisibility(View.GONE);
    }
    
    private void showArticleDialog(Article article) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(article.getTitle());
        builder.setMessage(article.getDescription());
        builder.setPositiveButton("Open in browser", (dialog, which) -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(article.getUrl()));
            startActivity(intent);
        });
        builder.setNeutralButton("Поделиться", (dialog, which) -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, article.getTitle() + "\n" + article.getUrl());
            startActivity(Intent.createChooser(shareIntent, "Поделиться"));
        });
        builder.setNegativeButton("Close", null);
        
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    
    private void showFavoritesDialog() {
        executorService.execute(() -> {
            List<FavoriteArticle> favorites = database.favoriteArticleDao().getAll();
            mainHandler.post(() -> {
                if (favorites.isEmpty()) {
                    Toast.makeText(this, "No favorite news", Toast.LENGTH_SHORT).show();
                } else {
                    StringBuilder message = new StringBuilder();
                    for (FavoriteArticle fav : favorites) {
                        message.append(fav.getTitle()).append("\n\n");
                    }
                    
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Favorite News (" + favorites.size() + ")");
                    builder.setMessage(message.toString());
                    builder.setPositiveButton("OK", null);
                    builder.show();
                }
            });
        });
    }
    
    private void saveToFavorites(Article article) {
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
            mainHandler.post(() -> {
                Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
            });
        });
    }
    
    private void removeFromFavorites(Article article) {
        executorService.execute(() -> {
            FavoriteArticle favorite = database.favoriteArticleDao().getByUrl(article.getUrl());
            if (favorite != null) {
                database.favoriteArticleDao().delete(favorite);
            }
            mainHandler.post(() -> {
                Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
            });
        });
    }
    
    private void saveSearchHistory(String query) {
        executorService.execute(() -> {
            com.artem.finalproject.database.entity.SearchHistory history = 
                    new com.artem.finalproject.database.entity.SearchHistory();
            history.setQuery(query);
            history.setResultCount(latestArticles.size());
            database.searchHistoryDao().insert(history);
        });
    }
    
    private void saveToCache(List<Article> articles, String query) {
        // Реализация кэширования (упрощенная версия)
        executorService.execute(() -> {
            // Здесь можно добавить логику кэширования
        });
    }
    
    private void showNotification(String title, String message, String articleUrl) {
        if (preferencesHelper.isNotificationsEnabled()) {
            notificationHelper.showNotification(title, message, articleUrl);
        }
    }
    
    private void showApiKeyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("API key required");
        builder.setMessage("An API key from NewsAPI is required for the app to work.\n\n" +
                "1. Go to https://newsapi.org/\n" +
                "2. Register and get a free key\n" +
                "3. Open MainActivity.java file\n" +
                "4. Find the line: private static final String API_KEY = ...\n" +
                "5. Replace YOUR_API_KEY_HERE with your key\n" +
                "6. Rebuild the app\n\n" +
                "Free plan: 100 requests per day");
        builder.setPositiveButton("Open website", (dialog, which) -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://newsapi.org/"));
            startActivity(intent);
        });
        builder.setNegativeButton("Close", null);
        builder.setCancelable(false);
        builder.show();
    }
    
    private void showProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Profile");
        builder.setMessage("Name: User\nEmail: user@example.com\n\nProfile settings will be available in the next version.");
        builder.setPositiveButton("Settings", (dialog, which) -> {
            Toast.makeText(this, "Profile settings", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Close", null);
        builder.show();
    }
    
    private void showNotificationsDialog() {
        executorService.execute(() -> {
            // Получаем последние уведомления из базы данных или создаем пример
            mainHandler.post(() -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Notifications (1)");
                builder.setMessage("New news loaded!\n\nYou received " + (trendingArticles.size() + latestArticles.size()) + " new articles.");
                builder.setPositiveButton("Clear", (dialog, which) -> {
                    Toast.makeText(this, "Notifications cleared", Toast.LENGTH_SHORT).show();
                });
                builder.setNegativeButton("Close", null);
                builder.show();
            });
        });
    }
    
    private void showFilterDialog() {
        String[] sortOptions = {"By relevancy", "By publication date", "By popularity"};
        String[] dateOptions = {"All time", "Last hour", "Last 24 hours", "Last week"};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filters");
        
        View dialogView = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, null);
        builder.setView(dialogView);
        
        android.widget.ListView listView = new android.widget.ListView(this);
        String[] options = {"Sort: By relevancy", "Period: All time", "Source: All sources"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, options);
        listView.setAdapter(adapter);
        
        builder.setView(listView);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0:
                    showSortDialog();
                    break;
                case 1:
                    showDateFilterDialog();
                    break;
                case 2:
                    Toast.makeText(this, "Filter by sources", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
        
        builder.setPositiveButton("Apply", (dialog, which) -> {
            // Apply filters
            String query = searchEditText.getText().toString().trim();
            if (!query.isEmpty()) {
                searchNews(query);
            } else {
                loadTopHeadlines();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    
    private void showSortDialog() {
        String[] sortOptions = {"By relevancy", "By publication date", "By popularity"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sort");
        builder.setItems(sortOptions, (dialog, which) -> {
            String selectedSort = sortOptions[which];
            Toast.makeText(this, "Selected: " + selectedSort, Toast.LENGTH_SHORT).show();
            // Here you can apply sorting
        });
        builder.show();
    }
    
    private void showDateFilterDialog() {
        String[] dateOptions = {"All time", "Last hour", "Last 24 hours", "Last week"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Period");
        builder.setItems(dateOptions, (dialog, which) -> {
            String selectedDate = dateOptions[which];
            Toast.makeText(this, "Selected: " + selectedDate, Toast.LENGTH_SHORT).show();
            // Here you can apply date filter
        });
        builder.show();
    }
    
    private void showAllTrendingNews() {
        // Открываем SearchActivity с запросом для trending новостей
        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
        intent.putExtra("search_query", "trending");
        intent.putExtra("search_type", "trending");
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    
    private void showAllLatestNews() {
        // Открываем SearchActivity с запросом для latest новостей
        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
        intent.putExtra("search_query", "latest");
        intent.putExtra("search_type", "latest");
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Меню отключено - убираем иконку гаечного ключа
        return false;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
