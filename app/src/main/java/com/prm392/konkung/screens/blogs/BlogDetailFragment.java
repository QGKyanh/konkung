package com.prm392.konkung.screens.blogs;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.prm392.konkung.R;
import com.prm392.konkung.models.Blog;
import com.prm392.konkung.repository.BlogRepository;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class BlogDetailFragment extends Fragment {
    
    private static final String ARG_BLOG_ID = "blog_id";
    
    // UI Elements
    private ImageView imageViewThumbnail;
    private ImageView buttonBack;
    private TextView textViewTitle;
    private TextView textViewMetaDescription;
    private TextView textViewAuthor;
    private TextView textViewDate;
    private TextView textViewStatus;
    private TextView textViewContent;
    private Button buttonShare;
    private Button buttonBookmark;
    private ProgressBar progressBar;
    private View contentView;
    private View statusIndicator;
    
    // Data
    private String blogId;
    private Blog currentBlog;
    private BlogRepository blogRepository;
    private SimpleDateFormat dateFormat;

    public static BlogDetailFragment newInstance(String blogId) {
        BlogDetailFragment fragment = new BlogDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BLOG_ID, blogId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            blogId = getArguments().getString(ARG_BLOG_ID);
        }
        
        // Initialize date formatter
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_blog_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupClickListeners();
        initRepository();
        loadBlogDetail();
    }

    private void initViews(View view) {
        imageViewThumbnail = view.findViewById(R.id.imageViewThumbnail);
        buttonBack = view.findViewById(R.id.buttonBack);
        textViewTitle = view.findViewById(R.id.textViewTitle);
        textViewMetaDescription = view.findViewById(R.id.textViewMetaDescription);
        textViewAuthor = view.findViewById(R.id.textViewAuthor);
        textViewDate = view.findViewById(R.id.textViewDate);
        textViewStatus = view.findViewById(R.id.textViewStatus);
        textViewContent = view.findViewById(R.id.textViewContent);
        buttonShare = view.findViewById(R.id.buttonShare);
        buttonBookmark = view.findViewById(R.id.buttonBookmark);
        progressBar = view.findViewById(R.id.progressBar);
        contentView = view.findViewById(R.id.contentView);
        statusIndicator = view.findViewById(R.id.statusIndicator);
    }

    private void setupClickListeners() {
        buttonBack.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        });

        buttonShare.setOnClickListener(v -> shareBlog());
        buttonBookmark.setOnClickListener(v -> bookmarkBlog());
    }

    private void initRepository() {
        blogRepository = BlogRepository.getInstance();
    }

    private void loadBlogDetail() {
        if (blogId == null) {
            showError("ID bài viết không hợp lệ");
            return;
        }

        showLoading();
        blogRepository.getBlog(blogId, new BlogRepository.BlogCallback() {
            @Override
            public void onSuccess(Blog blog) {
                currentBlog = blog;
                displayBlogInfo();
                hideLoading();
            }

            @Override
            public void onError(String errorMessage) {
                showError(errorMessage);
                hideLoading();
            }

            @Override
            public void onLoading() {
                showLoading();
            }
        });
    }

    private void displayBlogInfo() {
        if (currentBlog == null) return;

        // Blog title
        textViewTitle.setText(currentBlog.getTitle());
        
        // Meta description
        if (currentBlog.getMetaDescription() != null && !currentBlog.getMetaDescription().trim().isEmpty()) {
            textViewMetaDescription.setText(currentBlog.getMetaDescription());
            textViewMetaDescription.setVisibility(View.VISIBLE);
        } else {
            textViewMetaDescription.setVisibility(View.GONE);
        }
        
        // Author
        textViewAuthor.setText("Tác giả: " + currentBlog.getAuthorName());
        
        // Date
        textViewDate.setText(currentBlog.getFormattedCreatedDate());
        
        // Status
        if (currentBlog.isActive()) {
            textViewStatus.setText("Hoạt động");
            textViewStatus.setTextColor(getResources().getColor(R.color.green, null));
            statusIndicator.setBackgroundColor(getResources().getColor(R.color.green, null));
        } else {
            textViewStatus.setText("Không hoạt động");
            textViewStatus.setTextColor(getResources().getColor(R.color.orange, null));
            statusIndicator.setBackgroundColor(getResources().getColor(R.color.orange, null));
        }

        // Load thumbnail image
        Glide.with(this)
                .load(currentBlog.getThumbnail())
                .placeholder(R.drawable.ic_milk_logo)
                .error(R.drawable.ic_milk_logo)
                .centerCrop()
                .into(imageViewThumbnail);

        // Blog content with formatted text
        displayFormattedContent();
    }

    private void displayFormattedContent() {
        String content = currentBlog.getContent();
        if (content != null && !content.trim().isEmpty()) {
            // Convert markdown-like formatting to HTML
            content = formatContentToHtml(content);
            
            // Set formatted text
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                textViewContent.setText(Html.fromHtml(content, Html.FROM_HTML_MODE_COMPACT));
            } else {
                textViewContent.setText(Html.fromHtml(content));
            }
            
            // Enable link clicking
            textViewContent.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            textViewContent.setText("Không có nội dung bài viết");
        }
    }

    private String formatContentToHtml(String content) {
        // Convert markdown-like formatting to HTML
        content = content.replace("\\n\\n", "<br><br>");
        content = content.replace("\\n", "<br>");
        content = content.replace("**", "<b>").replace("**", "</b>");
        
        // Format bullet points
        content = content.replaceAll("- (.+?)<br>", "• $1<br>");
        
        // Format numbered lists
        content = content.replaceAll("(\\d+)\\. (.+?)<br>", "$1. $2<br>");
        
        return content;
    }

    private void shareBlog() {
        if (currentBlog != null) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, currentBlog.getTitle());
            
            String shareText = currentBlog.getTitle() + "\n\n" + 
                             currentBlog.getMetaDescription() + "\n\n" +
                             "Xem chi tiết tại ứng dụng KonKung";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            
            try {
                startActivity(Intent.createChooser(shareIntent, "Chia sẻ bài viết"));
            } catch (Exception e) {
                Toast.makeText(getContext(), "Không thể chia sẻ bài viết", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void bookmarkBlog() {
        if (currentBlog != null) {
            // TODO: Implement bookmark functionality
            // For now, just show a message
            Toast.makeText(getContext(), "Đã lưu bài viết: " + currentBlog.getTitle(), Toast.LENGTH_SHORT).show();
            
            // You can implement this by:
            // 1. Saving to SharedPreferences
            // 2. Saving to local database
            // 3. Calling a bookmark API endpoint
        }
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        contentView.setVisibility(View.GONE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        contentView.setVisibility(View.VISIBLE);
    }

    private void showError(String errorMessage) {
        Toast.makeText(getContext(), "Lỗi: " + errorMessage, Toast.LENGTH_LONG).show();
    }
}
