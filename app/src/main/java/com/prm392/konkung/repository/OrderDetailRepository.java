package com.prm392.konkung.repository;

import com.prm392.konkung.models.OrderDetailResponse;
import com.prm392.konkung.network.ApiClient;
import com.prm392.konkung.network.ApiService;
import com.prm392.konkung.network.responses.BaseResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailRepository {
    private static OrderDetailRepository instance;
    private final ApiService apiService;

    private OrderDetailRepository() {
        apiService = ApiClient.getApiService();
    }

    public static synchronized OrderDetailRepository getInstance() {
        if (instance == null) {
            instance = new OrderDetailRepository();
        }
        return instance;
    }

    // Callback interface for order detail
    public interface OrderDetailCallback {
        void onSuccess(OrderDetailResponse orderDetail);
        void onError(String errorMessage);
        void onLoading();
    }

    public void getOrderDetail(String userId, String orderId, OrderDetailCallback callback) {
        if (callback != null) {
            callback.onLoading();
        }

        Call<BaseResponse<OrderDetailResponse>> call = apiService.getOrderDetail(userId, orderId);
        call.enqueue(new Callback<BaseResponse<OrderDetailResponse>>() {
            @Override
            public void onResponse(Call<BaseResponse<OrderDetailResponse>> call, 
                                 Response<BaseResponse<OrderDetailResponse>> response) {
                if (callback == null) return;

                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<OrderDetailResponse> baseResponse = response.body();
                    
                    if (baseResponse.getStatusCode() == 200 && baseResponse.getData() != null) {
                        callback.onSuccess(baseResponse.getData());
                    } else {
                        String errorMessage = baseResponse.getMessage() != null ? 
                                baseResponse.getMessage() : "Không thể lấy chi tiết đơn hàng";
                        callback.onError(errorMessage);
                    }
                } else {
                    // Handle HTTP error
                    String errorMessage;
                    switch (response.code()) {
                        case 400:
                            errorMessage = "Yêu cầu không hợp lệ";
                            break;
                        case 401:
                            errorMessage = "Không có quyền truy cập";
                            break;
                        case 403:
                            errorMessage = "Truy cập bị từ chối";
                            break;
                        case 404:
                            errorMessage = "Không tìm thấy đơn hàng";
                            break;
                        case 500:
                            errorMessage = "Lỗi máy chủ nội bộ";
                            break;
                        default:
                            errorMessage = "Lỗi không xác định (Mã: " + response.code() + ")";
                            break;
                    }
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<OrderDetailResponse>> call, Throwable t) {
                if (callback != null) {
                    String errorMessage = "Lỗi kết nối: " + 
                            (t.getMessage() != null ? t.getMessage() : "Không thể kết nối đến máy chủ");
                    callback.onError(errorMessage);
                }
            }
        });
    }
}
