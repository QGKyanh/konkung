package com.prm392.konkung.repository;

import android.util.Log;

import com.prm392.konkung.models.OrderHistory;
import com.prm392.konkung.network.ApiClient;
import com.prm392.konkung.network.ApiService;
import com.prm392.konkung.network.responses.BaseResponse;
import com.prm392.konkung.network.responses.OrderHistoryListData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryRepository {
    private static final String TAG = "OrderHistoryRepository";
    private static OrderHistoryRepository instance;
    private ApiService apiService;

    private OrderHistoryRepository() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public static synchronized OrderHistoryRepository getInstance() {
        if (instance == null) {
            instance = new OrderHistoryRepository();
        }
        return instance;
    }

    // Callback interfaces
    public interface OrderHistoryListCallback {
        void onSuccess(List<OrderHistory> orders, int page, boolean hasNextPage);
        void onError(String errorMessage);
        void onLoading();
    }

    // Get user order history with pagination
    public void getUserOrderHistory(String userId, int page, int pageSize, OrderHistoryListCallback callback) {
        if (userId == null || userId.trim().isEmpty()) {
            callback.onError("User ID không hợp lệ");
            return;
        }

        callback.onLoading();
        
        Call<BaseResponse<OrderHistoryListData>> call = apiService.getUserOrderHistory(userId, page, pageSize);
        
        Log.d(TAG, "Getting user order history - UserId: " + userId + ", Page: " + page + ", PageSize: " + pageSize);
        
        call.enqueue(new Callback<BaseResponse<OrderHistoryListData>>() {
            @Override
            public void onResponse(Call<BaseResponse<OrderHistoryListData>> call, 
                                 Response<BaseResponse<OrderHistoryListData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<OrderHistoryListData> baseResponse = response.body();
                    
                    if (baseResponse.getStatusCode() == 200 && baseResponse.getData() != null) {
                        OrderHistoryListData orderHistoryListData = baseResponse.getData();
                        List<OrderHistory> orders = orderHistoryListData.getItems();
                        
                        Log.d(TAG, "Order history loaded successfully: " + orders.size() + " items");
                        Log.d(TAG, "Page: " + orderHistoryListData.getPage() + 
                                 ", HasNextPage: " + orderHistoryListData.isHasNextPage());
                        
                        callback.onSuccess(orders, orderHistoryListData.getPage(), orderHistoryListData.isHasNextPage());
                    } else {
                        String errorMsg = baseResponse.getMessage() != null ? 
                                baseResponse.getMessage() : "Không thể tải lịch sử đơn hàng";
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
            public void onFailure(Call<BaseResponse<OrderHistoryListData>> call, Throwable t) {
                String errorMsg = "Lỗi kết nối: " + t.getMessage();
                Log.e(TAG, "Network Error: " + errorMsg, t);
                callback.onError(errorMsg);
            }
        });
    }

    // Get order history (first page) - convenience method
    public void getUserOrderHistory(String userId, OrderHistoryListCallback callback) {
        getUserOrderHistory(userId, 1, 10, callback);
    }
}
