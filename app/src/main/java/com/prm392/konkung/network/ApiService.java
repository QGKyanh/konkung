package com.prm392.konkung.network;

import com.prm392.konkung.models.Blog;
import com.prm392.konkung.models.Order;
import com.prm392.konkung.models.Product;
import com.prm392.konkung.network.responses.BaseResponse;
import com.prm392.konkung.network.responses.BlogListData;
import com.prm392.konkung.network.responses.ProductListData;
import com.prm392.konkung.repository.AuthRepository.SignUpRequest;
import com.prm392.konkung.models.User;
import com.prm392.konkung.repository.AuthRepository.LoginRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    
    // Product endpoints
    @GET("api/products")
    Call<BaseResponse<ProductListData>> getAllProducts();
    
    @GET("api/products")
    Call<BaseResponse<ProductListData>> getAllProducts(
            @Query("page") int page,
            @Query("pageSize") int pageSize
    );
    
    @GET("api/products")
    Call<BaseResponse<ProductListData>> searchProducts(
            @Query("search") String searchQuery,
            @Query("page") int page,
            @Query("pageSize") int pageSize
    );
    
    @GET("api/products/{id}")
    Call<BaseResponse<Product>> getProduct(@Path("id") String productId);
    
    @GET("api/products")
    Call<BaseResponse<ProductListData>> getProductsByCategory(
            @Query("categoryId") int categoryId,
            @Query("page") int page,
            @Query("pageSize") int pageSize
    );
    
    // Blog endpoints
    @GET("api/posts")
    Call<BaseResponse<BlogListData>> getAllBlogs();
    
    @GET("api/posts")
    Call<BaseResponse<BlogListData>> getAllBlogs(
            @Query("page") int page,
            @Query("pageSize") int pageSize
    );
    
    @GET("api/posts/{id}")
    Call<BaseResponse<Blog>> getBlog(@Path("id") String blogId);
    
    // Order endpoints
    @POST("api/orders")
    Call<BaseResponse<Order>> createOrder(@Body Order order);
    
    @GET("api/orders/{id}")
    Call<BaseResponse<Order>> getOrder(@Path("id") String orderId);
    
    @GET("api/orders")
    Call<BaseResponse<java.util.List<Order>>> getUserOrders();
    
    @PUT("api/orders/{id}/cancel")
    Call<BaseResponse<Order>> cancelOrder(@Path("id") String orderId);

    @POST("/api/authentication/sign-up")
    Call<BaseResponse<User>> signUp(@Body SignUpRequest request);

    @POST("/api/authentication/login")
    Call<BaseResponse<User>> loginDashboard(@Body LoginRequest request);
}
