package com.prm392.konkung.network;

import com.prm392.konkung.models.Blog;
import com.prm392.konkung.models.Order;
import com.prm392.konkung.models.Product;
import com.prm392.konkung.network.responses.BaseResponse;
import com.prm392.konkung.network.responses.BlogListData;
import com.prm392.konkung.network.responses.ProductListData;
import com.prm392.konkung.network.responses.CategoryListData;
import com.prm392.konkung.repository.AuthRepository.SignUpRequest;
import com.prm392.konkung.models.User;
import com.prm392.konkung.repository.AuthRepository.LoginRequest;
import com.prm392.konkung.models.CartResponse;
import com.prm392.konkung.models.AddToCartRequest;
import com.prm392.konkung.models.UpdateCartItemRequest;
import com.prm392.konkung.models.CheckoutRequest;
import com.prm392.konkung.models.CheckoutResponse;
import com.prm392.konkung.models.Address;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.PUT;

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
            @Query("CategoryIds") String categoryIds,
            @Query("page") int page,
            @Query("pageSize") int pageSize
    );

    @GET("api/products/featured")
    Call<BaseResponse<List<Product>>> getFeaturedProducts();

    @GET("api/products/categories")
    Call<BaseResponse<CategoryListData>> getAllCategories(@Query("PageSize") int pageSize);
    
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

    @GET("api/user/{userId}/cart")
    Call<BaseResponse<CartResponse>> getCart(@Path("userId") String userId);

    @POST("api/user/{userId}/cart")
    Call<BaseResponse<CartResponse>> addToCart(@Path("userId") String userId, @Body AddToCartRequest request);

    @PATCH("api/user/{userId}/cart/{productId}")
    Call<BaseResponse<CartResponse>> updateCartItem(@Path("userId") String userId, @Path("productId") String productId, @Body UpdateCartItemRequest request);

    @DELETE("api/user/{userId}/cart")
    Call<BaseResponse<Void>> deleteAllCart(@Path("userId") String userId);

    @DELETE("api/user/{userId}/cart/{productId}")
    Call<BaseResponse<CartResponse>> deleteCartItem(@Path("userId") String userId, @Path("productId") String productId);

    @POST("api/checkout")
    Call<BaseResponse<CheckoutResponse>> checkout(@Body CheckoutRequest request);

    @GET("api/users/{userId}/addresses")
    Call<BaseResponse<List<Address>>> getUserAddresses(@Path("userId") String userId);
}
