# Настройка API ключа

Для работы приложения необходим API ключ от NewsAPI.

## Инструкция:

1. Получите бесплатный API ключ на https://newsapi.org/
2. Зарегистрируйтесь и скопируйте ваш API ключ
3. Откройте файл `app/src/main/java/com/artem/finalproject/MainActivity.java`
4. Найдите строку: `private static final String API_KEY = "YOUR_API_KEY_HERE";`
5. Замените `YOUR_API_KEY_HERE` на ваш реальный API ключ
6. Откройте файл `app/src/main/java/com/artem/finalproject/SearchActivity.java`
7. Найдите строку: `private static final String API_KEY = "YOUR_API_KEY_HERE";`
8. Замените `YOUR_API_KEY_HERE` на ваш реальный API ключ

## Важно:
- Не публикуйте ваш API ключ в открытом доступе
- Не коммитьте файлы с реальным API ключом в Git
- API ключ уже добавлен в .gitignore
