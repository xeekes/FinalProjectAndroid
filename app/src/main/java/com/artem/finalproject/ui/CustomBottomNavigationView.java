package com.artem.finalproject.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CustomBottomNavigationView extends BottomNavigationView {
    
    public CustomBottomNavigationView(@NonNull Context context) {
        super(context);
    }
    
    public CustomBottomNavigationView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    
    public CustomBottomNavigationView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupSelectionListener();
    }
    
    private void setupSelectionListener() {
        // Слушатель будет установлен в MainActivity
        // Здесь просто обновляем layout при изменении выбора
    }
    
    public void refreshLayout() {
        isLayoutSetup = false;
        setupHorizontalLayout();
    }
    
    private boolean isLayoutSetup = false;
    
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!isLayoutSetup) {
            post(this::setupHorizontalLayout);
        }
    }
    
    private void setupHorizontalLayout() {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) getChildAt(0);
        if (menuView == null) {
            post(this::setupHorizontalLayout);
            return;
        }
        
        isLayoutSetup = true;
        
        for (int i = 0; i < menuView.getChildCount(); i++) {
            View itemView = menuView.getChildAt(i);
            if (itemView == null) continue;
            
            // Ищем иконку и текст
            ImageView iconView = findViewInChildren(itemView, ImageView.class);
            TextView textView = findViewInChildren(itemView, TextView.class);
            
            if (iconView != null && textView != null) {
                // Проверяем, не настроен ли уже горизонтальный layout
                if (itemView instanceof LinearLayout && 
                    ((LinearLayout) itemView).getOrientation() == LinearLayout.HORIZONTAL) {
                    // Обновляем видимость текста для уже настроенных элементов
                    updateTextVisibility(itemView);
                    continue;
                }
                
                // Проверяем, выбран ли элемент через ID меню
                int selectedId = getSelectedItemId();
                // Получаем ID элемента меню из позиции
                android.view.Menu menu = getMenu();
                android.view.MenuItem menuItem = menu.getItem(i);
                boolean isSelected = (menuItem != null && menuItem.getItemId() == selectedId);
                
                // Создаем горизонтальный layout
                LinearLayout horizontalLayout = new LinearLayout(getContext());
                horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
                horizontalLayout.setGravity(android.view.Gravity.CENTER_VERTICAL);
                
                // Копируем параметры из родителя
                ViewGroup.LayoutParams originalParams = itemView.getLayoutParams();
                if (originalParams != null) {
                    horizontalLayout.setLayoutParams(originalParams);
                }
                
                horizontalLayout.setPadding(
                    itemView.getPaddingLeft(),
                    itemView.getPaddingTop(),
                    itemView.getPaddingRight(),
                    itemView.getPaddingBottom()
                );
                
                // Настраиваем иконку
                ImageView newIcon = new ImageView(getContext());
                newIcon.setImageDrawable(iconView.getDrawable());
                newIcon.setColorFilter(iconView.getColorFilter());
                LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                    (int) (24 * getResources().getDisplayMetrics().density),
                    (int) (24 * getResources().getDisplayMetrics().density)
                );
                iconParams.setMargins(0, 0, (int) (8 * getResources().getDisplayMetrics().density), 0);
                newIcon.setLayoutParams(iconParams);
                
                // Настраиваем текст - показываем ТОЛЬКО для выбранного элемента
                TextView newText = new TextView(getContext());
                newText.setText(textView.getText());
                newText.setTextSize(14);
                
                if (isSelected) {
                    // Активный элемент: белый текст, жирный, видимый
                    newText.setTextColor(getResources().getColor(com.artem.finalproject.R.color.white));
                    newText.setTypeface(null, android.graphics.Typeface.BOLD);
                    newText.setVisibility(android.view.View.VISIBLE);
                } else {
                    // Неактивный элемент: скрываем текст полностью
                    newText.setVisibility(android.view.View.GONE);
                }
                
                newText.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                
                // Сохраняем ссылку для обновления
                newText.setTag("nav_text");
                horizontalLayout.setTag("nav_item_" + i);
                
                // Добавляем в горизонтальный layout
                horizontalLayout.addView(newIcon);
                horizontalLayout.addView(newText);
                
                // Заменяем содержимое
                if (itemView instanceof ViewGroup) {
                    ViewGroup parent = (ViewGroup) itemView.getParent();
                    if (parent != null) {
                        int index = parent.indexOfChild(itemView);
                        parent.removeViewAt(index);
                        parent.addView(horizontalLayout, index);
                    }
                }
            }
        }
    }
    
    private void updateTextVisibility(View itemView) {
        TextView textView = findViewInChildren(itemView, TextView.class);
        if (textView != null && "nav_text".equals(textView.getTag())) {
            // Определяем позицию элемента
            BottomNavigationMenuView menuView = (BottomNavigationMenuView) getChildAt(0);
            if (menuView != null) {
                int position = menuView.indexOfChild(itemView);
                if (position >= 0) {
                    android.view.Menu menu = getMenu();
                    android.view.MenuItem menuItem = menu.getItem(position);
                    int selectedId = getSelectedItemId();
                    boolean isSelected = (menuItem != null && menuItem.getItemId() == selectedId);
                    
                    if (isSelected) {
                        textView.setVisibility(android.view.View.VISIBLE);
                        textView.setTextColor(getResources().getColor(com.artem.finalproject.R.color.white));
                        textView.setTypeface(null, android.graphics.Typeface.BOLD);
                    } else {
                        textView.setVisibility(android.view.View.GONE);
                    }
                }
            }
        }
    }
    
    private <T extends View> T findViewInChildren(View parent, Class<T> clazz) {
        if (clazz.isInstance(parent)) {
            return clazz.cast(parent);
        }
        if (parent instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) parent;
            for (int i = 0; i < group.getChildCount(); i++) {
                T found = findViewInChildren(group.getChildAt(i), clazz);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
}
