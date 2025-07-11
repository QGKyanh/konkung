package com.prm392.konkung.network;

import com.prm392.konkung.models.Product;
import com.prm392.konkung.network.responses.BaseResponse;
import com.prm392.konkung.network.responses.ProductListData;

import retrofit2.Call;
import retrofit2.http.GET;
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
}
