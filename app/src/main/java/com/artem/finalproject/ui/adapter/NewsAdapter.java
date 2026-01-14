package com.artem.finalproject.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
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
 * Адаптер для отображения списка новостей
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private List<Article> articles;
    private OnItemClickListener onItemClickListener;
    private OnFavoriteClickListener onFavoriteClickListener;
    private int lastPosition = -1;
    
    public interface OnItemClickListener {
        void onItemClick(Article article);
    }
    
    public interface OnFavoriteClickListener {
        void onFavoriteClick(Article article, boolean isFavorite);
    }
    
    public NewsAdapter() {
        this.articles = new ArrayList<>();
    }
    
    public void setArticles(List<Article> articles) {
        this.articles = articles;
        notifyDataSetChanged();
    }
    
    public void addArticles(List<Article> newArticles) {
        int startPosition = this.articles.size();
        this.articles.addAll(newArticles);
        notifyItemRangeInserted(startPosition, newArticles.size());
    }
    
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
    
    public void setOnFavoriteClickListener(OnFavoriteClickListener listener) {
        this.onFavoriteClickListener = listener;
    }
    
    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        Article article = articles.get(position);
        holder.bind(article);
        
        // Анимация появления
        setAnimation(holder.itemView, position);
    }
    
    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(),
                    android.R.anim.fade_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
    
    @Override
    public int getItemCount() {
        return articles.size();
    }
    
    class NewsViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView descriptionTextView;
        private TextView authorTextView;
        private TextView dateTextView;
        private TextView sourceTextView;
        private ImageView newsImageView;
        private ImageButton favoriteButton;
        
        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            authorTextView = itemView.findViewById(R.id.authorTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            sourceTextView = itemView.findViewById(R.id.sourceTextView);
            newsImageView = itemView.findViewById(R.id.newsImageView);
            favoriteButton = itemView.findViewById(R.id.favoriteButton);
            
            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(articles.get(position));
                    }
                }
            });
            
            favoriteButton.setOnClickListener(v -> {
                if (onFavoriteClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Article article = articles.get(position);
                        boolean newFavoriteState = !article.isFavorite();
                        article.setFavorite(newFavoriteState);
                        updateFavoriteButton(newFavoriteState);
                        onFavoriteClickListener.onFavoriteClick(article, newFavoriteState);
                    }
                }
            });
        }
        
        public void bind(Article article) {
            titleTextView.setText(article.getTitle());
            descriptionTextView.setText(article.getDescription());
            
            if (article.getAuthor() != null && !article.getAuthor().isEmpty()) {
                authorTextView.setText(article.getAuthor());
                authorTextView.setVisibility(View.VISIBLE);
            } else {
                authorTextView.setVisibility(View.GONE);
            }
            
            if (article.getPublishedAt() != null) {
                dateTextView.setText(formatDate(article.getPublishedAt()));
            }
            
            if (article.getSource() != null && article.getSource().getName() != null) {
                sourceTextView.setText(article.getSource().getName());
            }
            
            // Загрузка изображения с помощью Glide
            if (article.getUrlToImage() != null && !article.getUrlToImage().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(article.getUrlToImage())
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .error(android.R.drawable.ic_menu_report_image)
                        .centerCrop()
                        .into(newsImageView);
                newsImageView.setVisibility(View.VISIBLE);
            } else {
                newsImageView.setVisibility(View.GONE);
            }
            
            updateFavoriteButton(article.isFavorite());
        }
        
        private void updateFavoriteButton(boolean isFavorite) {
            if (isFavorite) {
                favoriteButton.setImageResource(android.R.drawable.btn_star_big_on);
            } else {
                favoriteButton.setImageResource(android.R.drawable.btn_star_big_off);
            }
        }
        
        private String formatDate(String dateString) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
                Date date = inputFormat.parse(dateString);
                if (date != null) {
                    return outputFormat.format(date);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return dateString;
        }
    }
}
