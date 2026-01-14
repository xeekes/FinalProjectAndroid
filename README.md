# Клиент для NewsAPI - Финальный проект по Android разработке

## Описание

Приложение-клиент для работы с NewsAPI, которое позволяет:

- Просматривать новости по категориям
- Искать новости по запросу
- Сохранять новости в избранное
- Настраивать тему, уведомления и другие параметры
- Получать уведомления о новых новостях

## Требования

- Android Studio
- Минимальная версия Android: API 24 (Android 7.0)
- Целевая версия Android: API 36

## Установка

1. Клонируйте репозиторий
2. Откройте проект в Android Studio
3. **ВАЖНО**: Получите бесплатный API ключ на [NewsAPI.org](https://newsapi.org/)
4. Замените `YOUR_API_KEY_HERE` на ваш API ключ в двух файлах:

   - `app/src/main/java/com/artem/finalproject/MainActivity.java` (строка ~92)
   - `app/src/main/java/com/artem/finalproject/SearchActivity.java` (строка ~67)

   ```java
   private static final String API_KEY = "ваш_api_ключ";
   ```

5. Синхронизируйте Gradle
6. Запустите приложение на эмуляторе или устройстве

**Подробная инструкция**: См. файл `API_KEY_SETUP.md`

## Реализованные требования

### ✅ 1. Настройка проекта

- Проект создан в Android Studio
- Правильный AndroidManifest.xml с разрешениями
- Минимальная версия API: 24

### ✅ 2. Material Design

- Использованы Material компоненты:
  - MaterialToolbar
  - FloatingActionButton
  - CardView (MaterialCardView)
  - RecyclerView
  - MaterialButton
  - TextInputLayout
  - Switch, CheckBox, RadioGroup

### ✅ 3. Виджеты (10+)

- TextView
- EditText (TextInputEditText)
- Button (MaterialButton)
- Switch
- CheckBox
- RadioGroup с RadioButton
- Spinner
- RecyclerView с адаптером
- ImageView
- ProgressBar
- SwipeRefreshLayout
- FloatingActionButton

### ✅ 4. Диалоги и уведомления

- AlertDialog с кастомным layout
- Notification с каналом (Android 8.0+)
- PendingIntent для действий в уведомлениях

### ✅ 5. Графика и анимация

- Загрузка изображений из интернета (Glide)
- Fade-in анимация для элементов списка
- Анимация появления карточек

### ✅ 6. Хранение данных

- SharedPreferences для настроек (PreferencesHelper)
- Room база данных с 6 таблицами:
  1. favorite_articles - избранные новости
  2. search_history - история поиска
  3. categories - категории новостей
  4. app_settings - настройки приложения
  5. notifications - уведомления
  6. cached_articles - кэш новостей
- CRUD операции для всех таблиц

### ✅ 7. Асинхронность

- ExecutorService для фоновых задач
- Handler для обновления UI из фонового потока
- Retrofit Callback для сетевых запросов

### ✅ 8. Сетевые возможности

- HTTP-запросы к NewsAPI (REST API)
- Retrofit + Gson для работы с API
- Отображение результатов в RecyclerView
- Обработка ошибок сети

### ✅ 9. Качество кода

- Логическая структура пакетов:
  - `api` - работа с API
  - `database` - база данных Room
  - `models` - модели данных
  - `ui` - UI компоненты
  - `utils` - утилиты
- Соблюдение Java naming conventions
- Комментарии в коде
- Ресурсы вынесены в strings.xml, colors.xml, themes.xml

### ✅ 10-12. Презентация, GitHub, Демонстрация

- Требуется от студента

## Структура проекта

```
app/src/main/java/com/artem/finalproject/
├── api/
│   ├── ApiClient.java
│   └── NewsApiService.java
├── database/
│   ├── AppDatabase.java
│   ├── dao/
│   │   ├── FavoriteArticleDao.java
│   │   ├── SearchHistoryDao.java
│   │   ├── CategoryDao.java
│   │   ├── AppSettingsDao.java
│   │   ├── NotificationDao.java
│   │   └── CachedArticleDao.java
│   └── entity/
│       ├── FavoriteArticle.java
│       ├── SearchHistory.java
│       ├── Category.java
│       ├── AppSettings.java
│       ├── NotificationItem.java
│       └── CachedArticle.java
├── models/
│   ├── Article.java
│   └── NewsResponse.java
├── ui/
│   └── adapter/
│       └── NewsAdapter.java
├── utils/
│   ├── PreferencesHelper.java
│   └── NotificationHelper.java
└── MainActivity.java
```

## Использованные библиотеки

- **Retrofit 2.9.0** - для HTTP-запросов
- **Gson 2.10.1** - для парсинга JSON
- **Room 2.6.1** - для работы с базой данных
- **Glide 4.16.0** - для загрузки изображений
- **Material Components 1.13.0** - Material Design компоненты
- **Lifecycle 2.8.6** - для работы с жизненным циклом

## Функциональность

1. **Поиск новостей**: Введите запрос и нажмите "Поиск"
2. **Категории**: Выберите категорию из списка
3. **Сортировка**: Выберите способ сортировки (релевантность или дата)
4. **Избранное**: Нажмите на звездочку, чтобы добавить новость в избранное
5. **Настройки**:
   - Темная тема
   - Уведомления
   - Автообновление
6. **Обновление**: Потяните вниз для обновления списка или нажмите FAB

## Примечания

- Для работы приложения необходим активный API ключ от NewsAPI
- Бесплатный план NewsAPI ограничен 100 запросами в день
- Приложение работает в офлайн режиме с кэшированными данными

## Автор

Финальный проект по Android разработке
