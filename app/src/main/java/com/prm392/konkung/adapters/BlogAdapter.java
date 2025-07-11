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
import com.prm392.konkung.models.Blog;

import java.util.ArrayList;
import java.util.List;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.BlogViewHolder> {
    
    private List<Blog> blogs;
    private OnBlogClickListener onBlogClickListener;

    public interface OnBlogClickListener {
        void onBlogClick(Blog blog);
    }

    public BlogAdapter() {
        this.blogs = new ArrayList<>();
    }

    public void setOnBlogClickListener(OnBlogClickListener listener) {
        this.onBlogClickListener = listener;
    }

    public void setBlogs(List<Blog> blogs) {
        this.blogs = blogs != null ? blogs : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addBlogs(List<Blog> newBlogs) {
        if (newBlogs != null && !newBlogs.isEmpty()) {
            int startPosition = this.blogs.size();
            this.blogs.addAll(newBlogs);
            notifyItemRangeInserted(startPosition, newBlogs.size());
        }
    }

    public void clearBlogs() {
        this.blogs.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_blog, parent, false);
        return new BlogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogViewHolder holder, int position) {
        Blog blog = blogs.get(position);
        holder.bind(blog);
    }

    @Override
    public int getItemCount() {
        return blogs.size();
    }

    class BlogViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewThumbnail;
        private TextView textViewTitle;
        private TextView textViewMetaDescription;
        private TextView textViewAuthor;
        private TextView textViewDate;
        private View statusIndicator;

        public BlogViewHolder(@NonNull View itemView) {
            super(itemView);
            
            imageViewThumbnail = itemView.findViewById(R.id.imageViewThumbnail);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewMetaDescription = itemView.findViewById(R.id.textViewMetaDescription);
            textViewAuthor = itemView.findViewById(R.id.textViewAuthor);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            statusIndicator = itemView.findViewById(R.id.statusIndicator);

            itemView.setOnClickListener(v -> {
                if (onBlogClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onBlogClickListener.onBlogClick(blogs.get(position));
                    }
                }
            });
        }

        public void bind(Blog blog) {
            // Title
            textViewTitle.setText(blog.getTitle());
            
            // Meta description or short content
            if (blog.getMetaDescription() != null && !blog.getMetaDescription().trim().isEmpty()) {
                textViewMetaDescription.setText(blog.getMetaDescription());
            } else {
                textViewMetaDescription.setText(blog.getShortContent());
            }
            
            // Author
            textViewAuthor.setText("Bởi " + blog.getAuthorName());
            
            // Date
            textViewDate.setText(blog.getFormattedCreatedDate());
            
            // Status indicator
            if (blog.isActive()) {
                statusIndicator.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.green, null));
                statusIndicator.setVisibility(View.VISIBLE);
            } else {
                statusIndicator.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.orange, null));
                statusIndicator.setVisibility(View.VISIBLE);
            }
            
            // Load thumbnail image
            Glide.with(itemView.getContext())
                    .load(blog.getThumbnail())
                    .placeholder(R.drawable.ic_milk_logo)
                    .error(R.drawable.ic_milk_logo)
                    .centerCrop()
                    .into(imageViewThumbnail);
        }
    }
}
