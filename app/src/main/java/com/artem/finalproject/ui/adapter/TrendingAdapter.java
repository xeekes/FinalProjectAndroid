package com.artem.finalproject.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.artem.finalproject.R;
import com.artem.finalproject.models.Article;
import com.bumptech.glide.Glide;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Адаптер для горизонтального списка Trending новостей
 */
public class TrendingAdapter extends RecyclerView.Adapter<TrendingAdapter.TrendingViewHolder> {
    private List<Article> articles;
    private OnItemClickListener onItemClickListener;
    
    public interface OnItemClickListener {
        void onItemClick(Article article);
    }
    
    public TrendingAdapter() {
        this.articles = new ArrayList<>();
    }
    
    public void setArticles(List<Article> articles) {
        this.articles = articles;
        notifyDataSetChanged();
    }
    
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
    
    @NonNull
    @Override
    public TrendingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news_trending, parent, false);
        return new TrendingViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull TrendingViewHolder holder, int position) {
        Article article = articles.get(position);
        holder.bind(article);
    }
    
    @Override
    public int getItemCount() {
        return articles.size();
    }
    
    class TrendingViewHolder extends RecyclerView.ViewHolder {
        private com.google.android.material.card.MaterialCardView cardView;
        private android.widget.ImageView newsImageView;
        private TextView titleTextView;
        private TextView categoryTag;
        private TextView dateTextView;
        private TextView commentsCount;
        private android.view.View videoIndicator;
        private TextView videoDuration;
        
        public TrendingViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (com.google.android.material.card.MaterialCardView) itemView;
            newsImageView = itemView.findViewById(R.id.newsImageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            categoryTag = itemView.findViewById(R.id.categoryTag);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            commentsCount = itemView.findViewById(R.id.commentsCount);
            videoIndicator = itemView.findViewById(R.id.videoIndicator);
            videoDuration = itemView.findViewById(R.id.videoDuration);
            
            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(articles.get(position));
                    }
                }
            });
        }
        
        public void bind(Article article) {
            titleTextView.setText(article.getTitle());
            
            // Устанавливаем категорию - для Trending используем "World" или название источника
            String category = "World";
            if (article.getSource() != null && article.getSource().getName() != null) {
                String sourceName = article.getSource().getName();
                // Используем короткое название или "World" по умолчанию
                if (sourceName.toLowerCase().contains("world") || sourceName.toLowerCase().contains("news")) {
                    category = "World";
                } else if (sourceName.length() > 10) {
                    category = sourceName.substring(0, 10);
                } else {
                    category = sourceName;
                }
            }
            categoryTag.setText(category);
            categoryTag.setBackgroundResource(R.drawable.tag_background);
            
            if (article.getPublishedAt() != null) {
                dateTextView.setText(formatTimeAgo(article.getPublishedAt()));
            }
            
            // Устанавливаем случайное количество комментариев для демонстрации
            if (commentsCount != null) {
                int randomComments = (int)(Math.random() * 500) + 50;
                commentsCount.setText(String.valueOf(randomComments));
            }
            
            // Скрываем видео индикатор, так как видео нет
            if (videoIndicator != null) {
                videoIndicator.setVisibility(android.view.View.GONE);
            }
            
            if (article.getUrlToImage() != null && !article.getUrlToImage().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(article.getUrlToImage())
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .error(android.R.drawable.ic_menu_report_image)
                        .centerCrop()
                        .into(newsImageView);
            }
        }
        
        private String formatTimeAgo(String dateString) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                Date date = inputFormat.parse(dateString);
                if (date != null) {
                    long diff = System.currentTimeMillis() - date.getTime();
                    long hours = diff / (1000 * 60 * 60);
                    if (hours < 1) {
                        long minutes = diff / (1000 * 60);
                        return minutes + "m ago";
                    } else if (hours < 24) {
                        return hours + "h ago";
                    } else {
                        long days = hours / 24;
                        return days + "d ago";
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return dateString;
        }
    }
}
