package com.prm392.konkung.screens.blogs;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.prm392.konkung.R;
import com.prm392.konkung.adapters.BlogAdapter;
import com.prm392.konkung.models.Blog;
import com.prm392.konkung.repository.BlogRepository;

import java.util.List;

public class BlogListFragment extends Fragment {
    
    private static final int PAGE_SIZE = 10;
    
    // UI Elements
    private RecyclerView recyclerViewBlogs;
    private BlogAdapter blogAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private View emptyView;
    
    // Data
    private BlogRepository blogRepository;
    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean hasNextPage = true;
    private Handler searchHandler = new Handler();

    public static BlogListFragment newInstance() {
        return new BlogListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_blog_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupRecyclerView();
        setupSwipeRefresh();
        initRepository();
        loadBlogs();
    }

    private void initViews(View view) {
        recyclerViewBlogs = view.findViewById(R.id.recyclerViewBlogs);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        progressBar = view.findViewById(R.id.progressBar);
        emptyView = view.findViewById(R.id.emptyView);
    }

    private void setupRecyclerView() {
        blogAdapter = new BlogAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerViewBlogs.setLayoutManager(layoutManager);
        recyclerViewBlogs.setAdapter(blogAdapter);

        // Set click listener
        blogAdapter.setOnBlogClickListener(blog -> {
            // Navigate to blog detail
            BlogDetailFragment blogDetailFragment = BlogDetailFragment.newInstance(blog.getId());
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, blogDetailFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Pagination scroll listener
        recyclerViewBlogs.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                
                if (dy > 0) { // Scrolling down
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager != null) {
                        int visibleItemCount = layoutManager.getChildCount();
                        int totalItemCount = layoutManager.getItemCount();
                        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                        if (!isLoading && hasNextPage) {
                            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 3) {
                                loadMoreBlogs();
                            }
                        }
                    }
                }
            }
        });
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(this::refreshBlogs);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.blue,
                R.color.green,
                R.color.orange
        );
    }

    private void initRepository() {
        blogRepository = BlogRepository.getInstance();
    }

    private void loadBlogs() {
        currentPage = 1;
        hasNextPage = true;
        
        blogRepository.getAllBlogs(currentPage, PAGE_SIZE, new BlogRepository.BlogListCallback() {
            @Override
            public void onSuccess(List<Blog> blogs, int page, boolean hasNextPage) {
                BlogListFragment.this.hasNextPage = hasNextPage;
                isLoading = false;
                
                if (isAdded()) {
                    hideLoading();
                    blogAdapter.setBlogs(blogs);
                    
                    if (blogs.isEmpty()) {
                        showEmptyView();
                    } else {
                        hideEmptyView();
                    }
                }
            }

            @Override
            public void onError(String errorMessage) {
                isLoading = false;
                if (isAdded()) {
                    hideLoading();
                    showError(errorMessage);
                    
                    if (blogAdapter.getItemCount() == 0) {
                        showEmptyView();
                    }
                }
            }

            @Override
            public void onLoading() {
                if (currentPage == 1) {
                    showLoading();
                }
                isLoading = true;
            }
        });
    }

    private void loadMoreBlogs() {
        if (isLoading || !hasNextPage) return;
        
        currentPage++;
        
        blogRepository.getAllBlogs(currentPage, PAGE_SIZE, new BlogRepository.BlogListCallback() {
            @Override
            public void onSuccess(List<Blog> blogs, int page, boolean hasNextPage) {
                BlogListFragment.this.hasNextPage = hasNextPage;
                isLoading = false;
                
                if (isAdded()) {
                    blogAdapter.addBlogs(blogs);
                }
            }

            @Override
            public void onError(String errorMessage) {
                isLoading = false;
                currentPage--; // Reset page number on error
                if (isAdded()) {
                    showError("Không thể tải thêm bài viết: " + errorMessage);
                }
            }

            @Override
            public void onLoading() {
                isLoading = true;
            }
        });
    }

    private void refreshBlogs() {
        blogAdapter.clearBlogs();
        loadBlogs();
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerViewBlogs.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        recyclerViewBlogs.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void showEmptyView() {
        emptyView.setVisibility(View.VISIBLE);
        recyclerViewBlogs.setVisibility(View.GONE);
    }

    private void hideEmptyView() {
        emptyView.setVisibility(View.GONE);
        recyclerViewBlogs.setVisibility(View.VISIBLE);
    }

    private void showError(String errorMessage) {
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (searchHandler != null) {
            searchHandler.removeCallbacksAndMessages(null);
        }
    }
}
