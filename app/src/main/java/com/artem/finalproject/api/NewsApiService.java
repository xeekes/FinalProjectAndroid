package com.artem.finalproject.api;

import com.artem.finalproject.models.NewsResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Интерфейс для работы с NewsAPI
 */
public interface NewsApiService {
    
    /**
     * Получить новости по запросу
     * @param query поисковый запрос
     * @param apiKey API ключ
     * @param pageSize количество результатов
     * @param sortBy способ сортировки
     * @param language язык (опционально)
     * @return список новостей
     */
    @GET("everything")
    Call<NewsResponse> getNews(
            @Query("q") String query,
            @Query("apiKey") String apiKey,
            @Query("pageSize") int pageSize,
            @Query("sortBy") String sortBy,
            @Query("language") String language
    );
    
    /**
     * Получить новости по категории
     * @param category категория новостей
     * @param apiKey API ключ
     * @return список новостей
     */
    @GET("top-headlines")
    Call<NewsResponse> getTopHeadlines(
            @Query("category") String category,
            @Query("apiKey") String apiKey,
            @Query("pageSize") int pageSize,
            @Query("country") String country
    );
}
