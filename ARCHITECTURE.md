# Архитектура проекта - Новостное приложение

## 📋 Общее описание

**Новостное приложение** - это Android-клиент для NewsAPI, который позволяет пользователям просматривать, искать и сохранять новости. Приложение использует Material Design 3, Room Database для локального хранения и Retrofit для работы с API.

---

## 🏗️ Архитектура проекта

### Структура пакетов

```
com.artem.finalproject/
├── api/                    # Работа с NewsAPI
│   ├── ApiClient.java      # Retrofit клиент
│   └── NewsApiService.java # Интерфейс API
├── database/               # Room Database
│   ├── AppDatabase.java    # Главная БД
│   ├── dao/                # Data Access Objects
│   │   ├── FavoriteArticleDao.java
│   │   ├── SearchHistoryDao.java
│   │   ├── CategoryDao.java
│   │   ├── AppSettingsDao.java
│   │   ├── NotificationDao.java
│   │   └── CachedArticleDao.java
│   └── entity/             # Таблицы БД
│       ├── FavoriteArticle.java
│       ├── SearchHistory.java
│       ├── Category.java
│       ├── AppSettings.java
│       ├── NotificationItem.java
│       └── CachedArticle.java
├── models/                 # Модели данных
│   ├── Article.java        # Модель новости
│   └── NewsResponse.java   # Ответ от API
├── ui/                     # UI компоненты
│   └── adapter/            # RecyclerView адаптеры
│       ├── LatestAdapter.java
│       ├── TrendingAdapter.java
│       └── NewsAdapter.java
├── utils/                  # Утилиты
│   ├── PreferencesHelper.java
│   └── NotificationHelper.java
└── Activities              # Экраны приложения
    ├── MainActivity.java
    ├── DetailActivity.java
    ├── FavoritesActivity.java
    ├── ProfileActivity.java
    └── SearchActivity.java
```

---

## 📱 Activity (Экраны приложения)

### 1. MainActivity (Главный экран)

**Путь:** `com.artem.finalproject.MainActivity`

**Функционал:**
- Отображение новостей в двух секциях:
  - **Trending** - горизонтальный список (первые 5 новостей)
  - **Latest** - вертикальный список (до 20 новостей)
- Поиск новостей по запросу
- Фильтрация по категориям (General, Business, Technology, Sports и т.д.)
- Управление избранным
- Навигация через BottomNavigationView

**Ключевые компоненты:**
- `searchEditText` - поле поиска
- `trendingRecyclerView` - горизонтальный список Trending
- `newsRecyclerView` - вертикальный список Latest
- `categoriesContainer` - контейнер для категорий
- `bottomNavigation` - нижняя навигация

**Методы:**
- `loadTopHeadlines()` - загрузка топ новостей
- `loadCategoryNews(String category)` - загрузка по категории
- `searchNews(String query)` - поиск новостей
- `setupCategories()` - настройка категорий
- `setupBottomNavigation()` - настройка навигации

**API вызовы:**
- `apiService.getTopHeadlines()` - получение топ новостей
- `apiService.getNews()` - поиск новостей

---

### 2. DetailActivity (Детальная страница новости)

**Путь:** `com.artem.finalproject.DetailActivity`

**Функционал:**
- Отображение полной информации о новости
- Добавление/удаление из избранного (FAB)
- Навигация через BottomNavigationView

**Ключевые компоненты:**
- `detailImageView` - изображение новости
- `detailTitleTextView` - заголовок
- `detailDescriptionTextView` - описание
- `detailContentTextView` - полный текст
- `fabFavorite` - кнопка избранного

**Методы:**
- `loadArticleData()` - загрузка данных новости
- `checkFavoriteStatus()` - проверка статуса избранного
- `addToFavorites()` - добавление в избранное
- `removeFromFavorites()` - удаление из избранного

**База данных:**
- Использует `FavoriteArticleDao` для работы с избранным

---

### 3. FavoritesActivity (Избранное)

**Путь:** `com.artem.finalproject.FavoritesActivity`

**Функционал:**
- Отображение всех сохраненных новостей
- Удаление из избранного
- Swipe to refresh
- Пустое состояние при отсутствии избранного

**Ключевые компоненты:**
- `favoritesRecyclerView` - список избранных
- `swipeRefreshLayout` - обновление свайпом
- `emptyStateLayout` - пустое состояние
- `latestAdapter` - адаптер (использует тот же, что и Latest)

**Методы:**
- `loadFavorites()` - загрузка избранного из БД
- `removeFromFavorites()` - удаление из избранного

**База данных:**
- `FavoriteArticleDao.getAll()` - получение всех избранных
- `FavoriteArticleDao.delete()` - удаление

---

### 4. ProfileActivity (Профиль и настройки)

**Путь:** `com.artem.finalproject.ProfileActivity`

**Функционал:**
- Переключение темной темы
- Включение/выключение уведомлений
- Автообновление новостей
- Статистика (количество избранных, история поиска)
- О приложении

**Ключевые компоненты:**
- `darkModeSwitch` - переключатель темной темы
- `notificationsSwitch` - переключатель уведомлений
- `autoRefreshCheckBox` - автообновление
- `favoritesCountTextView` - количество избранных
- `searchHistoryCountTextView` - количество запросов

**Методы:**
- `loadPreferences()` - загрузка настроек
- `loadStatistics()` - загрузка статистики
- `savePreferences()` - сохранение настроек

**Хранение данных:**
- `PreferencesHelper` - для настроек (SharedPreferences)
- `AppDatabase` - для статистики

---

### 5. SearchActivity (Поиск)

**Путь:** `com.artem.finalproject.SearchActivity`

**Функционал:**
- Расширенный поиск новостей
- История поиска
- Фильтры поиска

**API вызовы:**
- `apiService.getNews()` - поиск с параметрами

---

## 🗄️ База данных (Room)

### AppDatabase

**Путь:** `com.artem.finalproject.database.AppDatabase`

**Описание:** Singleton база данных Room с 6 таблицами

**Таблицы:**
1. `favorite_articles` - избранные новости
2. `search_history` - история поиска
3. `categories` - категории
4. `app_settings` - настройки приложения
5. `notifications` - уведомления
6. `cached_articles` - кэшированные новости

**Методы:**
- `getInstance(Context)` - получение экземпляра БД

---

### Entity (Таблицы)

#### 1. FavoriteArticle
**Поля:**
- `id` (PrimaryKey, autoGenerate)
- `title`, `description`, `url`, `urlToImage`
- `author`, `publishedAt`, `sourceName`
- `savedAt` - время сохранения

**DAO:** `FavoriteArticleDao`
- `getAll()` - все избранные
- `getByUrl(String url)` - по URL
- `insert()`, `delete()`, `update()`

---

#### 2. SearchHistory
**Поля:**
- `id` (PrimaryKey, autoGenerate)
- `query` - поисковый запрос
- `searchedAt` - время поиска
- `resultCount` - количество результатов

**DAO:** `SearchHistoryDao`

---

#### 3. AppSettings
**Поля:**
- `key` (PrimaryKey) - ключ настройки
- `value` - значение
- `updatedAt` - время обновления

**DAO:** `AppSettingsDao`

---

#### 4. NotificationItem
**Поля:**
- `id` (PrimaryKey, autoGenerate)
- `title`, `message`
- `createdAt` - время создания
- `isRead` - прочитано ли
- `articleUrl` - ссылка на новость

**DAO:** `NotificationDao`

---

#### 5. Category
**Поля:**
- `id` (PrimaryKey, autoGenerate)
- `name` - название категории
- `key` - ключ категории (для API)

**DAO:** `CategoryDao`

---

#### 6. CachedArticle
**Поля:**
- `id` (PrimaryKey, autoGenerate)
- Данные новости для кэширования
- `cachedAt` - время кэширования

**DAO:** `CachedArticleDao`

---

## 🌐 API (Retrofit)

### ApiClient

**Путь:** `com.artem.finalproject.api.ApiClient`

**Описание:** Singleton Retrofit клиент

**Настройки:**
- Base URL: `https://newsapi.org/v2/`
- Interceptor для User-Agent (обход Cloudflare)
- HttpLoggingInterceptor для отладки
- GsonConverterFactory для JSON

**Методы:**
- `getClient()` - получение Retrofit клиента
- `getApiService()` - получение API сервиса

---

### NewsApiService

**Путь:** `com.artem.finalproject.api.NewsApiService`

**Endpoints:**

1. **getTopHeadlines()**
   - Метод: `GET /top-headlines`
   - Параметры:
     - `category` - категория (general, business, technology и т.д.)
     - `apiKey` - API ключ
     - `pageSize` - количество результатов
     - `country` - страна (us, ru и т.д.)
   - Возвращает: `Call<NewsResponse>`

2. **getNews()**
   - Метод: `GET /everything`
   - Параметры:
     - `q` - поисковый запрос
     - `apiKey` - API ключ
     - `pageSize` - количество результатов
     - `sortBy` - сортировка (publishedAt, popularity, relevancy)
     - `language` - язык (ru, en и т.д.)
   - Возвращает: `Call<NewsResponse>`

---

## 📦 Модели данных

### Article

**Путь:** `com.artem.finalproject.models.Article`

**Поля:**
- `source` (Source) - источник новости
- `author` - автор
- `title` - заголовок
- `description` - описание
- `url` - ссылка на новость
- `urlToImage` - ссылка на изображение
- `publishedAt` - дата публикации
- `content` - полный текст
- `isFavorite` - флаг избранного (не из API)

**Вложенный класс Source:**
- `id` - ID источника
- `name` - название источника

---

### NewsResponse

**Путь:** `com.artem.finalproject.models.NewsResponse`

**Поля:**
- `status` - статус ответа ("ok" или "error")
- `totalResults` - общее количество результатов
- `articles` - список новостей (List<Article>)

---

## 🎨 UI Адаптеры

### LatestAdapter

**Путь:** `com.artem.finalproject.ui.adapter.LatestAdapter`

**Описание:** Адаптер для вертикального списка Latest новостей

**Layout:** `item_news_latest.xml`

**Компоненты карточки:**
- `newsImageView` - изображение
- `titleTextView` - заголовок
- `categoryTag` - тег категории (оранжевый фон)
- `dateTextView` - время публикации (формат: "2h ago", "3d ago")
- `commentsCount` - количество комментариев (случайное число)
- `moreButton` - кнопка "три точки" (меню)

**Интерфейсы:**
- `OnItemClickListener` - клик на карточку
- `OnFavoriteClickListener` - добавление/удаление из избранного

**Методы:**
- `setArticles(List<Article>)` - установка списка новостей
- `showMoreMenu()` - показ меню (Share, Open in browser, Add/Remove favorites)

---

### TrendingAdapter

**Путь:** `com.artem.finalproject.ui.adapter.TrendingAdapter`

**Описание:** Адаптер для горизонтального списка Trending новостей

**Layout:** `item_news_trending.xml`

**Компоненты карточки:**
- `newsImageView` - изображение
- `titleTextView` - заголовок
- `categoryTag` - тег категории
- `dateTextView` - время публикации
- `commentsCount` - количество комментариев
- `videoIndicator` - индикатор видео (скрыт, т.к. видео нет)

**Интерфейсы:**
- `OnItemClickListener` - клик на карточку

---

## 🛠️ Утилиты

### PreferencesHelper

**Путь:** `com.artem.finalproject.utils.PreferencesHelper`

**Описание:** Работа с SharedPreferences

**Хранимые настройки:**
- `KEY_DARK_MODE` - темная тема
- `KEY_NOTIFICATIONS_ENABLED` - уведомления
- `KEY_AUTO_REFRESH` - автообновление
- `KEY_API_KEY` - API ключ
- `KEY_SELECTED_CATEGORY` - выбранная категория
- `KEY_SORT_BY` - способ сортировки

**Методы:**
- `isDarkMode()`, `setDarkMode(boolean)`
- `isNotificationsEnabled()`, `setNotificationsEnabled(boolean)`
- `isAutoRefresh()`, `setAutoRefresh(boolean)`
- И т.д.

---

### NotificationHelper

**Путь:** `com.artem.finalproject.utils.NotificationHelper`

**Описание:** Работа с уведомлениями

**Канал уведомлений:**
- ID: `news_channel`
- Название: "Новости"
- Важность: `IMPORTANCE_DEFAULT`

**Методы:**
- `createNotificationChannel()` - создание канала (Android 8.0+)
- `showNotification(String title, String message, String articleUrl)` - показ уведомления

---

## 🎨 UI Ресурсы

### Layouts

1. **activity_main.xml**
   - CoordinatorLayout
   - AppBarLayout с MaterialToolbar
   - NestedScrollView (для правильного скроллинга)
   - RecyclerView для Trending (горизонтальный)
   - RecyclerView для Latest (вертикальный)
   - BottomNavigationView

2. **item_news_latest.xml**
   - MaterialCardView
   - ImageView для изображения
   - TextView для заголовка, категории, даты, комментариев
   - ImageButton для меню

3. **item_news_trending.xml**
   - MaterialCardView (горизонтальная карточка)
   - Аналогичные компоненты

4. **activity_detail.xml**
   - ScrollView с содержимым новости
   - FloatingActionButton для избранного
   - BottomNavigationView

5. **activity_favorites.xml**
   - SwipeRefreshLayout
   - RecyclerView
   - Empty state layout

6. **activity_profile.xml**
   - MaterialCardView с настройками
   - Switch для темной темы и уведомлений
   - CheckBox для автообновления
   - TextView для статистики

---

### Drawables

1. **bottom_nav_background.xml**
   - Layer-list с градиентной тенью сверху и белым фоном

2. **tag_background.xml**
   - Оранжевый фон для тегов категорий

3. **category_tab_background.xml**
   - Фон для неактивных категорий (светло-серый)

4. **category_tab_selected.xml**
   - Фон для активной категории

5. **shadow_top.xml** и **shadow_bottom.xml**
   - Градиентные тени

---

### Colors

**Основные цвета:**
- `orange` (#FF6B35) - основной цвет приложения
- `white` (#FFFFFF) - фон
- `text_primary` (#212121) - основной текст
- `text_secondary` (#757575) - вторичный текст
- `category_selected_bg` (#424242) - фон активной категории
- `category_unselected_text` (#424242) - текст неактивной категории

---

### Themes

**Путь:** `res/values/themes.xml`

**Стили:**
- `Theme.FinalProject` - основная тема
- `Widget.App.BottomNavigationView` - стиль нижней навигации
- `BottomNavActiveIndicator` - стиль активного индикатора (оранжевый круг 48dp)

---

## 🔄 Потоки данных

### Загрузка новостей

1. **Пользователь открывает приложение**
   - `MainActivity.onCreate()` → `loadTopHeadlines()`
   - `executorService.execute()` → асинхронный запрос
   - `apiService.getTopHeadlines()` → Retrofit
   - `onResponse()` → обработка ответа
   - Разделение на Trending (5) и Latest (20)
   - `trendingAdapter.setArticles()` и `latestAdapter.setArticles()`
   - Обновление UI через `mainHandler.post()`

2. **Выбор категории**
   - `setupCategories()` → создание тегов категорий
   - Клик на категорию → `loadCategoryNews(category)`
   - Аналогичный процесс загрузки

3. **Поиск**
   - Ввод в `searchEditText` → `searchNews(query)`
   - `apiService.getNews()` → поиск
   - Результаты отображаются в Trending и Latest

---

### Работа с избранным

1. **Добавление в избранное**
   - Клик на FAB в `DetailActivity`
   - `addToFavorites()` → создание `FavoriteArticle`
   - `executorService.execute()` → `favoriteArticleDao.insert()`
   - Обновление иконки FAB

2. **Просмотр избранного**
   - `FavoritesActivity.onCreate()` → `loadFavorites()`
   - `favoriteArticleDao.getAll()` → получение из БД
   - Конвертация `FavoriteArticle` → `Article`
   - Отображение через `LatestAdapter`

3. **Удаление из избранного**
   - Меню "Remove from favorites" → `removeFromFavorites()`
   - `favoriteArticleDao.delete()` → удаление из БД
   - Обновление списка

---

## 🔐 Безопасность

### API ключ

**Проблема:** API ключ не должен попадать в Git

**Решение:**
- Использование placeholder: `"YOUR_API_KEY_HERE"`
- Инструкция в `API_KEY_SETUP.md`
- `.gitignore` для защиты `local.properties`

**Где используется:**
- `MainActivity.java` (строка ~93)
- `SearchActivity.java` (строка ~67)

---

## 🎯 Ключевые особенности

### 1. Асинхронность

- Все сетевые запросы через `ExecutorService`
- Обновление UI через `Handler` и `runOnUiThread()`
- Работа с БД в фоновых потоках

### 2. Кэширование

- Таблица `CachedArticle` для офлайн режима
- Glide кэширует изображения автоматически

### 3. Навигация

- BottomNavigationView на всех экранах
- Анимации переходов (`slide_in_right`, `slide_out_left`)
- `FLAG_ACTIVITY_CLEAR_TOP` для правильной навигации

### 4. Material Design 3

- MaterialToolbar
- MaterialCardView
- FloatingActionButton
- BottomNavigationView
- SwipeRefreshLayout

### 5. Обработка ошибок

- Проверка API ключа
- Обработка HTML ответов (Cloudflare)
- Проверка статуса ответа
- Показ понятных сообщений об ошибках

---

## 📊 Статистика проекта

- **Activity:** 5 экранов
- **Таблицы БД:** 6 таблиц
- **DAO:** 6 интерфейсов
- **Адаптеры:** 3 адаптера
- **API endpoints:** 2 метода
- **Модели:** 2 класса
- **Утилиты:** 2 класса

---

## 🚀 Зависимости (build.gradle.kts)

- **Material Design:** `com.google.android.material:material`
- **Retrofit:** `com.squareup.retrofit2:retrofit`
- **Gson:** `com.google.code.gson:gson`
- **Room:** `androidx.room:room-runtime`
- **Glide:** `com.github.bumptech.glide:glide`
- **Lifecycle:** `androidx.lifecycle:lifecycle-viewmodel`, `lifecycle-livedata`

---

## 📝 Важные заметки

1. **API ключ обязателен** - без него приложение не работает
2. **Бесплатный лимит NewsAPI** - 100 запросов в день
3. **Минимальная версия Android:** API 24 (Android 7.0)
4. **Целевая версия:** API 36
5. **ViewBinding включен** - для удобной работы с views

---

## 🔍 Где что находится

- **API ключ:** `MainActivity.java:93`, `SearchActivity.java:67`
- **Настройка футера:** `themes.xml` → `Widget.App.BottomNavigationView`
- **Цвета:** `colors.xml`
- **Анимации:** `res/anim/`
- **Drawables:** `res/drawable/`
- **База данных:** `database/AppDatabase.java`
- **API клиент:** `api/ApiClient.java`

---

Это полная документация архитектуры проекта. Теперь вы можете ответить на любой вопрос о структуре и работе приложения!
