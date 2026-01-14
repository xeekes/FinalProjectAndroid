# 🎨 Руководство по стилизации Android приложения

## 📁 Структура ресурсов

В Android стилизация происходит через **ресурсы (resources)** в папке `res/`:

```
app/src/main/res/
├── layout/          # XML файлы с разметкой экранов
├── values/          # Цвета, размеры, строки, стили
│   ├── colors.xml   # Цвета приложения
│   ├── themes.xml   # Темы (глобальные стили)
│   └── strings.xml  # Текстовые строки
├── drawable/        # Фигуры, фоны, иконки
└── color/           # Цветовые селекторы
```

## 1. 🎨 Цвета (colors.xml)

### Определение цветов

```xml
<!-- app/src/main/res/values/colors.xml -->
<resources>
    <!-- Основные цвета -->
    <color name="primary">#FF6B35</color>        <!-- Оранжевый -->
    <color name="white">#FFFFFFFF</color>        <!-- Белый -->
    <color name="black">#FF000000</color>        <!-- Черный -->
    
    <!-- Текст -->
    <color name="text_primary">#212121</color>   <!-- Темный текст -->
    <color name="text_secondary">#757575</color> <!-- Светлый текст -->
    
    <!-- Фоны -->
    <color name="background">#FFFFFF</color>
    <color name="gray_light">#F5F5F5</color>
</resources>
```

### Использование цветов

В layout XML:
```xml
<TextView
    android:textColor="@color/text_primary"
    android:background="@color/white" />
```

В коде Java:
```java
textView.setTextColor(getResources().getColor(R.color.text_primary));
view.setBackgroundColor(getResources().getColor(R.color.background));
```

## 2. 📐 Layout XML - Разметка экранов

### Основная структура

```xml
<!-- activity_main.xml -->
<androidx.coordinatorlayout.widget.CoordinatorLayout
    android:layout_width="match_parent"    <!-- На всю ширину -->
    android:layout_height="match_parent"   <!-- На всю высоту -->
    android:background="@color/white">    <!-- Белый фон -->
    
    <!-- Элементы внутри -->
    <TextView
        android:id="@+id/greetingTextView"
        android:layout_width="wrap_content"  <!-- По содержимому -->
        android:layout_height="wrap_content"
        android:text="Hi, User"
        android:textSize="20sp"              <!-- Размер текста -->
        android:textStyle="bold"             <!-- Жирный -->
        android:textColor="@color/text_primary"
        android:padding="16dp"               <!-- Внутренний отступ -->
        android:layout_marginTop="8dp" />     <!-- Внешний отступ -->
</androidx.coordinatorlayout.widget.CoordinatorLayout>
```

### Размеры (размеры указываются в dp/sp)

- **dp (density-independent pixels)** - для размеров, отступов, margins
- **sp (scale-independent pixels)** - для текста (учитывает настройки шрифта пользователя)

```xml
android:textSize="16sp"        <!-- Текст -->
android:padding="16dp"         <!-- Отступы -->
android:layout_margin="8dp"     <!-- Внешние отступы -->
android:layout_width="300dp"    <!-- Ширина -->
```

### Layout параметры

```xml
<!-- match_parent - занимает все доступное пространство -->
android:layout_width="match_parent"

<!-- wrap_content - по размеру содержимого -->
android:layout_width="wrap_content"

<!-- Конкретный размер -->
android:layout_width="300dp"
```

### Отступы (Padding vs Margin)

```xml
<!-- Padding - внутренний отступ (от края элемента до содержимого) -->
android:padding="16dp"
android:paddingTop="8dp"
android:paddingStart="12dp"

<!-- Margin - внешний отступ (от элемента до других элементов) -->
android:layout_margin="16dp"
android:layout_marginTop="8dp"
android:layout_marginEnd="12dp"
```

## 3. 🎭 Drawable - Фоны и фигуры

### Простая фигура (shape)

```xml
<!-- drawable/search_background.xml -->
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    
    <!-- Цвет заливки -->
    <solid android:color="@color/gray_light" />
    
    <!-- Закругленные углы -->
    <corners android:radius="50dp" />
</shape>
```

### Использование drawable

```xml
<LinearLayout
    android:background="@drawable/search_background" />
```

### Типы фигур

```xml
<!-- Прямоугольник -->
android:shape="rectangle"

<!-- Овал/Круг -->
android:shape="oval"

<!-- Линия -->
android:shape="line"

<!-- Кольцо -->
android:shape="ring"
```

### Примеры drawable из вашего проекта

#### 1. Фон поисковой строки
```xml
<!-- drawable/search_background.xml -->
<shape android:shape="rectangle">
    <solid android:color="@color/gray_light" />
    <corners android:radius="50dp" />  <!-- Очень круглые края -->
</shape>
```

#### 2. Тег категории
```xml
<!-- drawable/tag_background.xml -->
<shape android:shape="rectangle">
    <solid android:color="@color/tag_world" />  <!-- Синий -->
    <corners android:radius="12dp" />            <!-- Закругленные -->
</shape>
```

#### 3. Круглая кнопка фильтра
```xml
<!-- drawable/filter_button_background.xml -->
<shape android:shape="oval">
    <solid android:color="@color/gray_light" />
</shape>
```

## 4. 🎯 Темы (themes.xml)

### Глобальные стили приложения

```xml
<!-- values/themes.xml -->
<style name="Base.Theme.FinalProject" 
       parent="Theme.Material3.DayNight.NoActionBar">
    
    <!-- Основные цвета темы -->
    <item name="colorPrimary">@color/primary</item>        <!-- Оранжевый -->
    <item name="colorOnPrimary">@color/white</item>         <!-- Текст на оранжевом -->
    <item name="colorSurface">@color/white</item>            <!-- Фон -->
    
    <!-- Системные цвета -->
    <item name="android:windowBackground">@color/white</item>
    <item name="android:statusBarColor">@color/white</item>
    <item name="android:navigationBarColor">@color/white</item>
</style>
```

### Применение темы

В `AndroidManifest.xml`:
```xml
<application
    android:theme="@style/Theme.FinalProject">
```

## 5. 📱 Стилизация конкретных элементов

### TextView

```xml
<TextView
    android:id="@+id/titleTextView"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text="Заголовок"
    android:textSize="18sp"              <!-- Размер -->
    android:textStyle="bold"             <!-- Жирный -->
    android:textColor="@color/text_primary"
    android:maxLines="2"                <!-- Максимум 2 строки -->
    android:ellipsize="end"              <!-- "..." в конце -->
    android:lineSpacingMultiplier="1.2"  <!-- Межстрочный интервал -->
    android:padding="16dp" />
```

### Button

```xml
<Button
    android:text="Нажми меня"
    android:textSize="16sp"
    android:textColor="@color/white"
    android:background="@color/orange"
    android:padding="12dp" />
```

### ImageView

```xml
<ImageView
    android:layout_width="100dp"
    android:layout_height="100dp"
    android:scaleType="centerCrop"       <!-- Как обрезать изображение -->
    android:background="@color/gray_light"
    android:src="@drawable/ic_launcher" />
```

### CardView (Карточка)

```xml
<com.google.android.material.card.MaterialCardView
    android:layout_width="300dp"
    android:layout_height="wrap_content"
    app:cardCornerRadius="16dp"          <!-- Закругленные углы -->
    app:cardElevation="4dp"              <!-- Тень -->
    app:cardBackgroundColor="@color/white">
    
    <!-- Содержимое карточки -->
</com.google.android.material.card.MaterialCardView>
```

## 6. 🎨 Стилизация программно (в коде Java)

### Изменение цвета текста
```java
TextView textView = findViewById(R.id.textView);
textView.setTextColor(getResources().getColor(R.color.text_primary));
```

### Изменение фона
```java
View view = findViewById(R.id.view);
view.setBackgroundColor(getResources().getColor(R.color.white));
// Или drawable
view.setBackgroundResource(R.drawable.search_background);
```

### Изменение размера текста
```java
textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
```

### Изменение отступов
```java
view.setPadding(16, 16, 16, 16);  // left, top, right, bottom в пикселях
```

### Изменение видимости
```java
view.setVisibility(View.VISIBLE);    // Видимый
view.setVisibility(View.INVISIBLE);  // Невидимый, но занимает место
view.setVisibility(View.GONE);       // Скрыт, не занимает место
```

## 7. 📐 Layout Managers (для RecyclerView)

### LinearLayoutManager (вертикальный список)
```java
LinearLayoutManager layoutManager = new LinearLayoutManager(this);
layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
recyclerView.setLayoutManager(layoutManager);
```

### GridLayoutManager (сетка)
```java
GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
recyclerView.setLayoutManager(gridLayoutManager);
```

## 8. 🎯 Практические примеры из вашего проекта

### Пример 1: Поисковая строка

**Layout:**
```xml
<LinearLayout
    android:background="@drawable/search_background"
    android:padding="16dp">
    
    <ImageView
        android:src="@android:drawable/ic_menu_search"
        android:tint="@color/text_secondary" />
    
    <EditText
        android:hint="Search articles"
        android:textColorHint="@color/text_secondary" />
</LinearLayout>
```

**Drawable (search_background.xml):**
```xml
<shape android:shape="rectangle">
    <solid android:color="@color/gray_light" />
    <corners android:radius="50dp" />
</shape>
```

### Пример 2: Тег категории

**Layout:**
```xml
<TextView
    android:text="World"
    android:background="@drawable/tag_background"
    android:textColor="@color/white"
    android:padding="12dp" />
```

**Drawable (tag_background.xml):**
```xml
<shape android:shape="rectangle">
    <solid android:color="@color/tag_world" />
    <corners android:radius="12dp" />
</shape>
```

### Пример 3: Карточка новости

**Layout:**
```xml
<com.google.android.material.card.MaterialCardView
    app:cardCornerRadius="16dp"
    app:cardElevation="4dp"
    app:cardBackgroundColor="@color/white">
    
    <ImageView
        android:layout_height="200dp"
        android:scaleType="centerCrop" />
    
    <TextView
        android:textSize="16sp"
        android:textStyle="bold" />
</com.google.android.material.card.MaterialCardView>
```

## 9. 💡 Советы по стилизации

### 1. Используйте ресурсы, а не хардкод
```xml
<!-- ❌ Плохо -->
android:textColor="#FF6B35"

<!-- ✅ Хорошо -->
android:textColor="@color/orange"
```

### 2. Используйте dp для размеров, sp для текста
```xml
android:textSize="16sp"      <!-- ✅ Для текста -->
android:padding="16dp"       <!-- ✅ Для размеров -->
```

### 3. Создавайте переиспользуемые drawable
Вместо повторения одних и тех же стилей, создайте drawable и используйте его:
```xml
<!-- Создайте drawable/button_primary.xml -->
<!-- Используйте везде: android:background="@drawable/button_primary" -->
```

### 4. Используйте темы для глобальных стилей
Не дублируйте стили в каждом layout, используйте темы.

### 5. Material Design 3
Ваше приложение использует Material 3, следуйте его принципам:
- Используйте `MaterialCardView` вместо обычных View
- Используйте `MaterialToolbar`
- Следуйте цветовой схеме Material

## 10. 🔧 Частые задачи стилизации

### Как изменить цвет фона экрана?
```xml
<!-- В layout -->
<CoordinatorLayout
    android:background="@color/white" />
```

### Как сделать закругленные углы?
```xml
<!-- Создайте drawable -->
<shape android:shape="rectangle">
    <corners android:radius="16dp" />
</shape>

<!-- Используйте -->
android:background="@drawable/rounded_background"
```

### Как добавить тень?
```xml
<!-- Для CardView -->
app:cardElevation="4dp"

<!-- Для обычного View (через drawable) -->
<shape>
    <solid android:color="@color/white" />
    <corners android:radius="8dp" />
</shape>
```

### Как сделать круглую кнопку?
```xml
<!-- drawable/circle_button.xml -->
<shape android:shape="oval">
    <solid android:color="@color/orange" />
</shape>
```

### Как изменить цвет текста программно?
```java
textView.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
```

## 📚 Полезные ресурсы

- **Material Design 3**: https://m3.material.io/
- **Android Developers - Styles**: https://developer.android.com/guide/topics/ui/look-and-feel/themes
- **Color Tool**: https://material.io/resources/color/

---

**Теперь вы знаете, как стилизовать Android приложение! 🎨**
