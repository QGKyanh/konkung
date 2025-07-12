package com.prm392.konkung.repository;

import android.util.Log;

import com.prm392.konkung.models.Blog;
import com.prm392.konkung.network.ApiClient;
import com.prm392.konkung.network.ApiService;
import com.prm392.konkung.network.responses.BaseResponse;
import com.prm392.konkung.network.responses.BlogListData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlogRepository {
    private static final String TAG = "BlogRepository";
    private static BlogRepository instance;
    private ApiService apiService;

    private BlogRepository() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public static synchronized BlogRepository getInstance() {
        if (instance == null) {
            instance = new BlogRepository();
        }
        return instance;
    }

    // Callback interfaces
    public interface BlogListCallback {
        void onSuccess(List<Blog> blogs, int page, boolean hasNextPage);
        void onError(String errorMessage);
        void onLoading();
    }

    public interface BlogCallback {
        void onSuccess(Blog blog);
        void onError(String errorMessage);
        void onLoading();
    }

    // Get all blogs (first page)
    public void getAllBlogs(BlogListCallback callback) {
        getAllBlogs(1, 10, callback);
    }

    // Get blogs with pagination
    public void getAllBlogs(int page, int pageSize, BlogListCallback callback) {
        callback.onLoading();
        
        Call<BaseResponse<BlogListData>> call = apiService.getAllBlogs(page, pageSize);
        
        Log.d(TAG, "Getting blogs - Page: " + page + ", PageSize: " + pageSize);
        
        call.enqueue(new Callback<BaseResponse<BlogListData>>() {
            @Override
            public void onResponse(Call<BaseResponse<BlogListData>> call, 
                                 Response<BaseResponse<BlogListData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<BlogListData> baseResponse = response.body();
                    
                    if (baseResponse.getStatusCode() == 200 && baseResponse.getData() != null) {
                        BlogListData blogListData = baseResponse.getData();
                        List<Blog> blogs = blogListData.getItems();
                        
                        Log.d(TAG, "Blogs loaded successfully: " + blogs.size() + " items");
                        Log.d(TAG, "Page: " + blogListData.getPage() + 
                                 ", HasNextPage: " + blogListData.isHasNextPage());
                        
                        callback.onSuccess(blogs, blogListData.getPage(), blogListData.isHasNextPage());
                    } else {
                        String errorMsg = baseResponse.getMessage() != null ? 
                                baseResponse.getMessage() : "Không thể tải danh sách bài viết";
                        Log.e(TAG, "API Error: " + errorMsg);
                        callback.onError(errorMsg);
                    }
                } else {
                    String errorMsg = "Lỗi server: " + response.code();
                    Log.e(TAG, "HTTP Error: " + errorMsg);
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<BlogListData>> call, Throwable t) {
                String errorMsg = "Lỗi kết nối: " + t.getMessage();
                Log.e(TAG, "Network Error: " + errorMsg, t);
                callback.onError(errorMsg);
            }
        });
    }

    // Get single blog by ID
    public void getBlog(String blogId, BlogCallback callback) {
        if (blogId == null || blogId.trim().isEmpty()) {
            callback.onError("ID bài viết không hợp lệ");
            return;
        }

        callback.onLoading();
        
        Call<BaseResponse<Blog>> call = apiService.getBlog(blogId);
        
        Log.d(TAG, "Getting blog with ID: " + blogId);
        
        call.enqueue(new Callback<BaseResponse<Blog>>() {
            @Override
            public void onResponse(Call<BaseResponse<Blog>> call, Response<BaseResponse<Blog>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<Blog> baseResponse = response.body();
                    
                    if (baseResponse.getStatusCode() == 200 && baseResponse.getData() != null) {
                        Blog blog = baseResponse.getData();
                        Log.d(TAG, "Blog loaded successfully: " + blog.getTitle());
                        callback.onSuccess(blog);
                    } else {
                        String errorMsg = baseResponse.getMessage() != null ? 
                                baseResponse.getMessage() : "Không thể tải bài viết";
                        Log.e(TAG, "API Error: " + errorMsg);
                        callback.onError(errorMsg);
                    }
                } else {
                    String errorMsg = "Lỗi server: " + response.code();
                    Log.e(TAG, "HTTP Error: " + errorMsg);
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Blog>> call, Throwable t) {
                String errorMsg = "Lỗi kết nối: " + t.getMessage();
                Log.e(TAG, "Network Error: " + errorMsg, t);
                callback.onError(errorMsg);
            }
        });
    }
}
