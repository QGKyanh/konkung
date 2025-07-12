package com.prm392.konkung.repository;

import androidx.annotation.NonNull;

import com.prm392.konkung.models.Product;
import com.prm392.konkung.network.ApiClient;
import com.prm392.konkung.network.ApiService;
import com.prm392.konkung.network.responses.BaseResponse;
import com.prm392.konkung.network.responses.ProductListData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductRepository {
    private static ProductRepository instance;
    private ApiService apiService;

    private ProductRepository() {
        apiService = ApiClient.getApiService();
    }

    public static synchronized ProductRepository getInstance() {
        if (instance == null) {
            instance = new ProductRepository();
        }
        return instance;
    }

    // Callback interfaces for clean separation
    public interface ProductListCallback {
        void onSuccess(List<Product> products, boolean hasNextPage, int totalCount);
        void onError(String errorMessage);
        void onLoading();
    }

    public interface ProductCallback {
        void onSuccess(Product product);
        void onError(String errorMessage);
        void onLoading();
    }

    // Get all products with pagination
    public void getAllProducts(int page, int pageSize, @NonNull ProductListCallback callback) {
        callback.onLoading();
        
        System.out.println("Making API call to get products - Page: " + page + ", PageSize: " + pageSize);
        
        Call<BaseResponse<ProductListData>> call = apiService.getAllProducts(page, pageSize);
        call.enqueue(new Callback<BaseResponse<ProductListData>>() {
            @Override
            public void onResponse(Call<BaseResponse<ProductListData>> call, Response<BaseResponse<ProductListData>> response) {
                System.out.println("API Response Code: " + response.code());
                System.out.println("API Response Success: " + response.isSuccessful());
                
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<ProductListData> baseResponse = response.body();
                    System.out.println("Base Response Status: " + baseResponse.getStatus());
                    System.out.println("Base Response Message: " + baseResponse.getMessage());
                    
                    if (baseResponse.isSuccess() && baseResponse.getData() != null) {
                        ProductListData data = baseResponse.getData();
                        System.out.println("Products count: " + (data.getItems() != null ? data.getItems().size() : 0));
                        callback.onSuccess(
                                data.getItems(),
                                data.isHasNextPage(),
                                data.getTotalCount()
                        );
                    } else {
                        String errorMsg = baseResponse.getMessage() != null ? 
                                baseResponse.getMessage() : "Unknown error occurred";
                        System.out.println("API Error: " + errorMsg);
                        callback.onError(errorMsg);
                    }
                } else {
                    String errorMsg = "Network error: " + response.code();
                    System.out.println("Network Error: " + errorMsg);
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<ProductListData>> call, Throwable t) {
                String errorMsg = "Network failure: " + t.getMessage();
                System.out.println("Network Failure: " + errorMsg);
                t.printStackTrace();
                callback.onError(errorMsg);
            }
        });
    }

    // Get all products (first page)
    public void getAllProducts(@NonNull ProductListCallback callback) {
        getAllProducts(1, 10, callback);
    }

    // Search products
    public void searchProducts(String searchQuery, int page, int pageSize, @NonNull ProductListCallback callback) {
        callback.onLoading();
        
        Call<BaseResponse<ProductListData>> call = apiService.searchProducts(searchQuery, page, pageSize);
        call.enqueue(new Callback<BaseResponse<ProductListData>>() {
            @Override
            public void onResponse(Call<BaseResponse<ProductListData>> call, Response<BaseResponse<ProductListData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<ProductListData> baseResponse = response.body();
                    if (baseResponse.isSuccess() && baseResponse.getData() != null) {
                        ProductListData data = baseResponse.getData();
                        callback.onSuccess(
                                data.getItems(),
                                data.isHasNextPage(),
                                data.getTotalCount()
                        );
                    } else {
                        callback.onError(baseResponse.getMessage() != null ? 
                                baseResponse.getMessage() : "Unknown error occurred");
                    }
                } else {
                    callback.onError("Network error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<ProductListData>> call, Throwable t) {
                callback.onError("Network failure: " + t.getMessage());
            }
        });
    }

    // Get products by category
    public void getProductsByCategory(int categoryId, int page, int pageSize, @NonNull ProductListCallback callback) {
        callback.onLoading();
        
        Call<BaseResponse<ProductListData>> call = apiService.getProductsByCategory(categoryId, page, pageSize);
        call.enqueue(new Callback<BaseResponse<ProductListData>>() {
            @Override
            public void onResponse(Call<BaseResponse<ProductListData>> call, Response<BaseResponse<ProductListData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<ProductListData> baseResponse = response.body();
                    if (baseResponse.isSuccess() && baseResponse.getData() != null) {
                        ProductListData data = baseResponse.getData();
                        callback.onSuccess(
                                data.getItems(),
                                data.isHasNextPage(),
                                data.getTotalCount()
                        );
                    } else {
                        callback.onError(baseResponse.getMessage() != null ? 
                                baseResponse.getMessage() : "Unknown error occurred");
                    }
                } else {
                    callback.onError("Network error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<ProductListData>> call, Throwable t) {
                callback.onError("Network failure: " + t.getMessage());
            }
        });
    }

    // Get single product
    public void getProduct(String productId, @NonNull ProductCallback callback) {
        callback.onLoading();
        
        Call<BaseResponse<Product>> call = apiService.getProduct(productId);
        call.enqueue(new Callback<BaseResponse<Product>>() {
            @Override
            public void onResponse(Call<BaseResponse<Product>> call, Response<BaseResponse<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<Product> baseResponse = response.body();
                    if (baseResponse.isSuccess() && baseResponse.getData() != null) {
                        callback.onSuccess(baseResponse.getData());
                    } else {
                        callback.onError(baseResponse.getMessage() != null ? 
                                baseResponse.getMessage() : "Unknown error occurred");
                    }
                } else {
                    callback.onError("Network error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Product>> call, Throwable t) {
                callback.onError("Network failure: " + t.getMessage());
            }
        });
    }
}
