package com.artem.finalproject.ui.adapter;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
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
 * Адаптер для вертикального списка Latest новостей
 */
public class LatestAdapter extends RecyclerView.Adapter<LatestAdapter.LatestViewHolder> {
    private List<Article> articles;
    private OnItemClickListener onItemClickListener;
    private OnFavoriteClickListener onFavoriteClickListener;
    
    public interface OnItemClickListener {
        void onItemClick(Article article);
    }
    
    public interface OnFavoriteClickListener {
        void onFavoriteClick(Article article, boolean isFavorite);
    }
    
    public LatestAdapter() {
        this.articles = new ArrayList<>();
    }
    
    public void setArticles(List<Article> articles) {
        this.articles = articles;
        notifyDataSetChanged();
    }
    
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
    
    public void setOnFavoriteClickListener(OnFavoriteClickListener listener) {
        this.onFavoriteClickListener = listener;
    }
    
    
    @NonNull
    @Override
    public LatestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news_latest, parent, false);
        return new LatestViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull LatestViewHolder holder, int position) {
        Article article = articles.get(position);
        holder.bind(article);
    }
    
    @Override
    public int getItemCount() {
        return articles.size();
    }
    
    class LatestViewHolder extends RecyclerView.ViewHolder {
        private android.widget.ImageView newsImageView;
        private TextView titleTextView;
        private TextView categoryTag;
        private TextView dateTextView;
        private TextView commentsCount;
        private ImageButton moreButton;
        
        public LatestViewHolder(@NonNull View itemView) {
            super(itemView);
            newsImageView = itemView.findViewById(R.id.newsImageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            categoryTag = itemView.findViewById(R.id.categoryTag);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            commentsCount = itemView.findViewById(R.id.commentsCount);
            moreButton = itemView.findViewById(R.id.moreButton);
            
            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(articles.get(position));
                    }
                }
            });
            
            if (moreButton != null) {
                moreButton.setOnClickListener(v -> {
                    if (onItemClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            showMoreMenu(v, articles.get(position));
                        }
                    }
                });
            }
        }
        
        public void bind(Article article) {
            titleTextView.setText(article.getTitle());
            
            if (article.getSource() != null && article.getSource().getName() != null) {
                categoryTag.setText(article.getSource().getName());
                categoryTag.setBackgroundResource(R.drawable.tag_background);
                categoryTag.setTextColor(itemView.getContext().getResources().getColor(R.color.white));
            }
            
            if (article.getPublishedAt() != null) {
                dateTextView.setText(formatTimeAgo(article.getPublishedAt()));
            }
            
            // Устанавливаем случайное количество комментариев для демонстрации
            if (commentsCount != null) {
                int randomComments = (int)(Math.random() * 200) + 10;
                commentsCount.setText(String.valueOf(randomComments));
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
        
        private void showMoreMenu(View view, Article article) {
            PopupMenu popupMenu = new PopupMenu(itemView.getContext(), view);
            popupMenu.getMenu().add(0, 1, 0, "Share");
            popupMenu.getMenu().add(0, 2, 0, "Open in browser");
            
            // Показываем "Remove from favorites" если есть listener для избранного, иначе "Add to favorites"
            boolean isFavorite = article.isFavorite();
            String favoriteText = (onFavoriteClickListener != null && isFavorite) ? "Remove from favorites" : "Add to favorites";
            popupMenu.getMenu().add(0, 3, 0, favoriteText);
            
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == 1) {
                    // Поделиться
                    shareArticle(article);
                } else if (item.getItemId() == 2) {
                    // Открыть в браузере
                    openInBrowser(article);
                } else if (item.getItemId() == 3) {
                    // Добавить/удалить из избранного
                    if (onFavoriteClickListener != null) {
                        onFavoriteClickListener.onFavoriteClick(article, !isFavorite);
                    } else {
                        addToFavorites(article);
                    }
                }
                return true;
            });
            popupMenu.show();
        }
        
        private void shareArticle(Article article) {
            android.content.Intent shareIntent = new android.content.Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String shareText = article.getTitle();
            if (article.getUrl() != null) {
                shareText += "\n\n" + article.getUrl();
            }
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareText);
            itemView.getContext().startActivity(android.content.Intent.createChooser(shareIntent, "Share news"));
        }
        
        private void openInBrowser(Article article) {
            if (article.getUrl() != null) {
                android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW, 
                    android.net.Uri.parse(article.getUrl()));
                itemView.getContext().startActivity(intent);
            }
        }
        
        private void addToFavorites(Article article) {
            android.widget.Toast.makeText(itemView.getContext(), "Added to favorites", android.widget.Toast.LENGTH_SHORT).show();
        }
    }
}
