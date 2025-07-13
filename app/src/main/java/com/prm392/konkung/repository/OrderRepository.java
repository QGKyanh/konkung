package com.prm392.konkung.repository;

import androidx.annotation.NonNull;

import com.prm392.konkung.models.Order;
import com.prm392.konkung.network.ApiClient;
import com.prm392.konkung.network.ApiService;
import com.prm392.konkung.network.responses.BaseResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderRepository {
    private static OrderRepository instance;
    private ApiService apiService;

    private OrderRepository() {
        apiService = ApiClient.getApiService();
    }

    public static synchronized OrderRepository getInstance() {
        if (instance == null) {
            instance = new OrderRepository();
        }
        return instance;
    }

    // Callback interfaces
    public interface OrderCallback {
        void onSuccess(Order order);

        void onError(String errorMessage);
    }

    public interface OrderListCallback {
        void onSuccess(java.util.List<Order> orders);

        void onError(String errorMessage);
    }

    // Create new order
    public void createOrder(Order order, @NonNull OrderCallback callback) {
        System.out.println("Creating order: " + order.getCustomerName());

        Call<BaseResponse<Order>> call = apiService.createOrder(order);
        call.enqueue(new Callback<BaseResponse<Order>>() {
            @Override
            public void onResponse(Call<BaseResponse<Order>> call, Response<BaseResponse<Order>> response) {
                System.out.println("Order API Response Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<Order> baseResponse = response.body();
                    System.out.println("Order API Status: " + baseResponse.getStatus());

                    if (baseResponse.isSuccess() && baseResponse.getData() != null) {
                        Order createdOrder = baseResponse.getData();
                        System.out.println("Order created with ID: " + createdOrder.getId());
                        callback.onSuccess(createdOrder);
                    } else {
                        String errorMsg = baseResponse.getMessage() != null ? baseResponse.getMessage()
                                : "Unknown error occurred";
                        System.out.println("Order API Error: " + errorMsg);
                        callback.onError(errorMsg);
                    }
                } else {
                    String errorMsg = "Network error: " + response.code();
                    System.out.println("Order Network Error: " + errorMsg);
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Order>> call, Throwable t) {
                String errorMsg = "Network failure: " + t.getMessage();
                System.out.println("Order Network Failure: " + errorMsg);
                t.printStackTrace();
                callback.onError(errorMsg);
            }
        });
    }

    // Get order by ID
    public void getOrder(String orderId, @NonNull OrderCallback callback) {
        Call<BaseResponse<Order>> call = apiService.getOrder(orderId);
        call.enqueue(new Callback<BaseResponse<Order>>() {
            @Override
            public void onResponse(Call<BaseResponse<Order>> call, Response<BaseResponse<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<Order> baseResponse = response.body();
                    if (baseResponse.isSuccess() && baseResponse.getData() != null) {
                        callback.onSuccess(baseResponse.getData());
                    } else {
                        callback.onError(baseResponse.getMessage() != null ? baseResponse.getMessage()
                                : "Unknown error occurred");
                    }
                } else {
                    callback.onError("Network error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Order>> call, Throwable t) {
                callback.onError("Network failure: " + t.getMessage());
            }
        });
    }

    // Get user orders
    public void getUserOrders(@NonNull OrderListCallback callback) {
        Call<BaseResponse<java.util.List<Order>>> call = apiService.getUserOrders();
        call.enqueue(new Callback<BaseResponse<java.util.List<Order>>>() {
            @Override
            public void onResponse(Call<BaseResponse<java.util.List<Order>>> call,
                    Response<BaseResponse<java.util.List<Order>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<java.util.List<Order>> baseResponse = response.body();
                    if (baseResponse.isSuccess() && baseResponse.getData() != null) {
                        callback.onSuccess(baseResponse.getData());
                    } else {
                        callback.onError(baseResponse.getMessage() != null ? baseResponse.getMessage()
                                : "Unknown error occurred");
                    }
                } else {
                    callback.onError("Network error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<java.util.List<Order>>> call, Throwable t) {
                callback.onError("Network failure: " + t.getMessage());
            }
        });
    }

    // Cancel order
    public void cancelOrder(String orderId, @NonNull OrderCallback callback) {
        Call<BaseResponse<Order>> call = apiService.cancelOrder(orderId);
        call.enqueue(new Callback<BaseResponse<Order>>() {
            @Override
            public void onResponse(Call<BaseResponse<Order>> call, Response<BaseResponse<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<Order> baseResponse = response.body();
                    if (baseResponse.isSuccess() && baseResponse.getData() != null) {
                        callback.onSuccess(baseResponse.getData());
                    } else {
                        callback.onError(baseResponse.getMessage() != null ? baseResponse.getMessage()
                                : "Unknown error occurred");
                    }
                } else {
                    callback.onError("Network error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Order>> call, Throwable t) {
                callback.onError("Network failure: " + t.getMessage());
            }
        });
    }
}