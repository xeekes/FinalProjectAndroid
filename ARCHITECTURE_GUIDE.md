# Руководство по архитектуре приложения News App

## 📱 Общая структура приложения

Ваше приложение - это **клиент для NewsAPI**, который показывает новости в удобном формате. Приложение использует **архитектуру MVC (Model-View-Controller)**.

## 🏗️ Структура проекта

```
app/src/main/java/com/artem/finalproject/
├── MainActivity.java          # Главный экран
├── SearchActivity.java        # Экран поиска
├── ProfileActivity.java       # Экран профиля
├── FavoritesActivity.java     # Экран избранного
├── DetailActivity.java         # Детали новости
│
├── api/                       # Работа с API
│   ├── ApiClient.java         # Настройка Retrofit
│   └── NewsApiService.java    # Интерфейс API запросов
│
├── models/                    # Модели данных
│   ├── Article.java           # Модель статьи
│   └── NewsResponse.java      # Ответ от API
│
├── ui/adapter/                # Адаптеры для списков
│   ├── TrendingAdapter.java   # Адаптер для Trending новостей
│   ├── LatestAdapter.java     # Адаптер для Latest новостей
│   └── NewsAdapter.java       # Адаптер для обычных новостей
│
├── database/                  # База данных (Room)
│   ├── AppDatabase.java       # Главная БД
│   ├── dao/                   # Data Access Objects
│   └── entity/                # Сущности БД
│
└── utils/                     # Утилиты
    ├── PreferencesHelper.java # Работа с настройками
    └── NotificationHelper.java # Уведомления
```

## 🔄 Как работает приложение

### 1. Запуск приложения (MainActivity)

**onCreate()** - главный метод, который вызывается при открытии приложения:

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);  // Загружаем layout
    
    // Инициализация всех компонентов
    initViews();              // Находим все виджеты в layout
    initUtils();              // Инициализируем утилиты
    setupRecyclerView();      // Настраиваем списки
    setupCategories();        // Создаем категории
    setupBottomNavigation();  // Настраиваем нижнюю навигацию
    setupListeners();         // Вешаем обработчики событий
    loadTopHeadlines();       // Загружаем новости
}
```

### 2. Инициализация компонентов

#### initViews() - находим виджеты
```java
private void initViews() {
    // Находим все элементы из layout по их ID
    searchEditText = findViewById(R.id.searchEditText);
    profileImageView = findViewById(R.id.profileImageView);
    trendingRecyclerView = findViewById(R.id.trendingRecyclerView);
    // ... и т.д.
}
```

#### initUtils() - инициализируем утилиты
```java
private void initUtils() {
    preferencesHelper = new PreferencesHelper(this);
    database = AppDatabase.getInstance(this);
    apiService = ApiClient.getApiService();  // Получаем API сервис
    executorService = Executors.newFixedThreadPool(2);  // Потоки для БД
}
```

### 3. Работа с API

#### ApiClient - настройка Retrofit
```java
// Создает Retrofit клиент для работы с NewsAPI
Retrofit retrofit = new Retrofit.Builder()
    .baseUrl("https://newsapi.org/v2/")
    .addConverterFactory(GsonConverterFactory.create())  // Конвертация JSON
    .build();
```

#### NewsApiService - интерфейс запросов
```java
public interface NewsApiService {
    @GET("top-headlines")
    Call<NewsResponse> getTopHeadlines(
        @Query("category") String category,
        @Query("apiKey") String apiKey,
        @Query("pageSize") int pageSize,
        @Query("country") String country
    );
}
```

#### Загрузка новостей
```java
private void loadTopHeadlines() {
    // Создаем запрос
    Call<NewsResponse> call = apiService.getTopHeadlines(
        "general",      // Категория
        API_KEY,        // Ваш ключ
        20,             // Количество новостей
        "us"            // Страна
    );
    
    // Асинхронный запрос
    call.enqueue(new Callback<NewsResponse>() {
        @Override
        public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
            if (response.isSuccessful()) {
                List<Article> articles = response.body().getArticles();
                // Обновляем UI
                latestAdapter.setArticles(articles);
            }
        }
        
        @Override
        public void onFailure(Call<NewsResponse> call, Throwable t) {
            // Обработка ошибки
        }
    });
}
```

### 4. RecyclerView и Адаптеры

#### Что такое RecyclerView?
**RecyclerView** - это список элементов. Он эффективно отображает большие списки, переиспользуя элементы.

#### Как работает адаптер:

```java
public class LatestAdapter extends RecyclerView.Adapter<LatestAdapter.LatestViewHolder> {
    private List<Article> articles;  // Данные
    
    // 1. Создание ViewHolder (один раз для каждого типа элемента)
    @Override
    public LatestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_news_latest, parent, false);
        return new LatestViewHolder(view);
    }
    
    // 2. Заполнение данными (вызывается для каждого элемента)
    @Override
    public void onBindViewHolder(LatestViewHolder holder, int position) {
        Article article = articles.get(position);
        holder.bind(article);  // Заполняем данными
    }
    
    // 3. Количество элементов
    @Override
    public int getItemCount() {
        return articles.size();
    }
}
```

#### ViewHolder - хранит ссылки на виджеты
```java
class LatestViewHolder extends RecyclerView.ViewHolder {
    private TextView titleTextView;
    private ImageView newsImageView;
    
    public LatestViewHolder(View itemView) {
        super(itemView);
        // Находим виджеты в layout элемента списка
        titleTextView = itemView.findViewById(R.id.titleTextView);
        newsImageView = itemView.findViewById(R.id.newsImageView);
    }
    
    public void bind(Article article) {
        // Заполняем данными
        titleTextView.setText(article.getTitle());
        Glide.with(itemView.getContext())
            .load(article.getUrlToImage())
            .into(newsImageView);
    }
}
```

### 5. База данных (Room)

#### Что такое Room?
**Room** - это библиотека для работы с SQLite базой данных в Android.

#### Структура:
- **Entity** - таблица (например, FavoriteArticle)
- **DAO** - методы для работы с таблицей
- **Database** - главный класс БД

#### Пример:
```java
// Entity - таблица
@Entity(tableName = "favorites")
public class FavoriteArticle {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public String title;
    public String url;
}

// DAO - методы работы с таблицей
@Dao
public interface FavoriteArticleDao {
    @Query("SELECT * FROM favorites")
    List<FavoriteArticle> getAll();
    
    @Insert
    void insert(FavoriteArticle article);
    
    @Delete
    void delete(FavoriteArticle article);
}
```

### 6. Обработка событий (Listeners)

#### Клики на элементы
```java
// Клик на карточку новости
latestAdapter.setOnItemClickListener(article -> {
    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
    intent.putExtra("article", article);  // Передаем данные
    startActivity(intent);  // Открываем новую Activity
});

// Клик на кнопку профиля
profileImageView.setOnClickListener(v -> {
    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
    startActivity(intent);
});
```

#### Поиск
```java
searchEditText.setOnEditorActionListener((v, actionId, event) -> {
    String query = searchEditText.getText().toString();
    searchNews(query);  // Выполняем поиск
    return true;
});
```

### 7. Навигация между экранами

#### Intent - способ перехода между Activity
```java
// Переход на другой экран
Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
intent.putExtra("key", value);  // Передача данных
startActivity(intent);
```

#### Bottom Navigation
```java
bottomNavigation.setOnItemSelectedListener(item -> {
    int itemId = item.getItemId();
    if (itemId == R.id.nav_home) {
        // Уже на главной
        return true;
    } else if (itemId == R.id.nav_favorites) {
        Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
        startActivity(intent);
        return true;
    }
    return false;
});
```

## 📝 Основные паттерны

### 1. Singleton (ApiClient, AppDatabase)
```java
// Один экземпляр на все приложение
public static Retrofit getClient() {
    if (retrofit == null) {
        retrofit = new Retrofit.Builder()...build();
    }
    return retrofit;
}
```

### 2. Callback (API запросы)
```java
call.enqueue(new Callback<NewsResponse>() {
    @Override
    public void onResponse(...) { /* Успех */ }
    @Override
    public void onFailure(...) { /* Ошибка */ }
});
```

### 3. Adapter Pattern (RecyclerView)
Адаптер преобразует данные (List<Article>) в визуальные элементы списка.

## 🎯 Как добавить новый функционал

### Пример: Добавить кнопку "Поделиться"

1. **Добавить в layout:**
```xml
<Button
    android:id="@+id/shareButton"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Share" />
```

2. **Найти в коде:**
```java
private Button shareButton;

private void initViews() {
    shareButton = findViewById(R.id.shareButton);
}
```

3. **Добавить обработчик:**
```java
private void setupListeners() {
    shareButton.setOnClickListener(v -> {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this news!");
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    });
}
```

## 🔑 Важные концепции

### 1. Асинхронность
- API запросы выполняются **асинхронно** (не блокируют UI)
- Используйте `Handler` для обновления UI из фонового потока:
```java
mainHandler.post(() -> {
    // Обновление UI
    adapter.setArticles(articles);
});
```

### 2. Жизненный цикл Activity
- `onCreate()` - создание
- `onStart()` - становится видимой
- `onResume()` - активна
- `onPause()` - приостановлена
- `onStop()` - не видна
- `onDestroy()` - уничтожена

### 3. View Binding / findViewById
```java
// Старый способ
TextView textView = findViewById(R.id.textView);

// Новый способ (если включен View Binding)
binding.textView.setText("Hello");
```

## 📚 Полезные ресурсы

- **Retrofit**: https://square.github.io/retrofit/
- **RecyclerView**: https://developer.android.com/guide/topics/ui/layout/recyclerview
- **Room Database**: https://developer.android.com/training/data-storage/room
- **Glide** (загрузка изображений): https://github.com/bumptech/glide

## 💡 Советы для написания кода

1. **Всегда инициализируйте переменные** перед использованием
2. **Проверяйте на null** перед вызовом методов
3. **Используйте try-catch** для обработки ошибок
4. **Комментируйте сложную логику**
5. **Разбивайте большие методы** на маленькие
6. **Используйте понятные имена** переменных и методов

## 🐛 Отладка

### Логирование
```java
Log.d("TAG", "Debug message");
Log.e("TAG", "Error message", exception);
```

### Проверка данных
```java
if (articles != null && !articles.isEmpty()) {
    // Работаем с данными
} else {
    Log.d("MainActivity", "Articles list is empty");
}
```

---

**Удачи в разработке! 🚀**
