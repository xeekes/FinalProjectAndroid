# Оптимизация Android эмулятора для лучшей производительности

## 1. Настройки эмулятора в Android Studio

### AVD Manager → Edit → Show Advanced Settings:

**RAM:**
- Рекомендуется: 2048-4096 MB (не больше половины RAM компьютера)
- Если у вас 8GB RAM → используйте 2048 MB
- Если у вас 16GB+ RAM → используйте 4096 MB

**VM heap:**
- Рекомендуется: 512 MB

**Internal Storage:**
- Минимум: 2 GB
- Рекомендуется: 4-8 GB

**SD Card:**
- Можно не создавать для тестирования

**Graphics:**
- **Automatic** - автоматический выбор
- **Hardware - GLES 2.0** - быстрее, но требует поддержку GPU
- **Software - GLES 2.0** - медленнее, но работает везде

**Boot option:**
- **Cold Boot** - полная загрузка (медленнее)
- **Quick Boot** - быстрая загрузка (быстрее, но может быть нестабильно)

## 2. Настройки Windows для лучшей производительности

### Включить виртуализацию в BIOS:
- Intel: включить "Intel Virtualization Technology (VT-x)"
- AMD: включить "AMD-V" или "SVM Mode"

### Проверить включена ли виртуализация:
```bash
# В командной строке Windows
systeminfo
# Ищите строку "Hyper-V Requirements" → "Virtualization Enabled In Firmware: Yes"
```

### Отключить Hyper-V (если не нужен):
- Windows Features → снять галочку с Hyper-V
- Перезагрузить компьютер

## 3. Использовать x86/x86_64 образы

**ВАЖНО:** Используйте x86 или x86_64 образы вместо ARM:
- x86/x86_64 работают намного быстрее на ПК
- ARM образы эмулируются и очень медленные

**Где проверить:**
- AVD Manager → Device Details → System Image
- Выберите образ с пометкой "x86" или "x86_64"

## 4. Настройки эмулятора при запуске

### Через командную строку (быстрее):
```bash
# Запуск с оптимизациями
emulator -avd YourAVDName -gpu host -no-snapshot-load
```

### Параметры:
- `-gpu host` - использовать GPU хоста (быстрее)
- `-no-snapshot-load` - не загружать снимок (быстрее первый запуск)
- `-no-audio` - отключить звук (немного быстрее)
- `-no-window` - запуск без окна (для CI/CD)

## 5. Настройки Android Studio

### File → Settings → Appearance & Behavior → System Settings → Memory Settings:
- **IDE max heap size:** 2048-4096 MB
- **Gradle max heap size:** 2048-4096 MB

### File → Settings → Build, Execution, Deployment → Compiler:
- ✅ Build process heap size: 2048 MB
- ✅ Additional build process VM options: `-Xmx2048m`

## 6. Использовать физическое устройство

**Самый быстрый вариант:**
- Подключите реальный Android телефон через USB
- Включите "Отладка по USB" в настройках разработчика
- Намного быстрее эмулятора!

## 7. Закрыть ненужные программы

- Закройте браузер с множеством вкладок
- Закройте другие тяжелые приложения
- Освободите RAM для эмулятора

## 8. Использовать более легкий эмулятор

### Рекомендуемые настройки для тестирования:
- **API Level:** 28-30 (не самый новый)
- **System Image:** x86_64, Google APIs (не Google Play)
- **RAM:** 2048 MB
- **Graphics:** Hardware - GLES 2.0

## 9. Быстрые команды для оптимизации

### Проверить доступную RAM:
```bash
# Windows
wmic OS get TotalVisibleMemorySize,FreePhysicalMemory
```

### Очистить кэш Gradle (если сборка медленная):
```bash
# В корне проекта
./gradlew clean
./gradlew --stop
```

## 10. Альтернативы эмулятору

- **Genymotion** - быстрый эмулятор (платный)
- **BlueStacks** - для игр, но можно для разработки
- **Физическое устройство** - самый быстрый вариант

## Рекомендуемые настройки для вашего проекта:

1. **AVD:**
   - API 28-30
   - x86_64 образ
   - RAM: 2048-4096 MB
   - Graphics: Hardware - GLES 2.0

2. **Gradle:**
   - Уже оптимизирован в gradle.properties

3. **Android Studio:**
   - Увеличьте heap size до 4096 MB

4. **Лучший вариант:**
   - Используйте физическое устройство для тестирования!
