package com.prm392.konkung.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.prm392.konkung.R;
import com.prm392.konkung.models.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<Category> categories;
    private OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public CategoryAdapter(List<Category> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }

    public void updateCategories(List<Category> newCategories) {
        this.categories = newCategories;
        notifyDataSetChanged();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageCategoryIcon;
        private TextView textCategoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageCategoryIcon = itemView.findViewById(R.id.imageCategoryIcon);
            textCategoryName = itemView.findViewById(R.id.textCategoryName);
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCategoryClick(categories.get(getAdapterPosition()));
                }
            });
        }

        public void bind(Category category) {
            if (category.getDescription() != null && category.getDescription().startsWith("http")) {
                Glide.with(imageCategoryIcon.getContext())
                    .load(category.getDescription())
                    .placeholder(category.getIconResId())
                    .error(category.getIconResId())
                    .into(imageCategoryIcon);
            } else {
                imageCategoryIcon.setImageResource(category.getIconResId());
            }
            textCategoryName.setText(category.getName());
        }
    }
} 