package com.prm392.konkung.repository;

import androidx.annotation.NonNull;

import com.prm392.konkung.models.User;
import com.prm392.konkung.network.ApiClient;
import com.prm392.konkung.network.ApiService;
import com.prm392.konkung.network.responses.BaseResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    private static AuthRepository instance;
    private ApiService apiService;

    private AuthRepository() {
        apiService = ApiClient.getApiService();
    }

    public static synchronized AuthRepository getInstance() {
        if (instance == null) {
            instance = new AuthRepository();
        }
        return instance;
    }

    // Callback interfaces
    public interface AuthCallback {
        void onSuccess(User user, String token);
        void onError(String errorMessage);
    }

    public interface RegisterCallback {
        void onSuccess(User user, String token);
        void onError(String errorMessage);
    }

    // Login
    public void login(String email, String password, @NonNull AuthCallback callback) {
        // For demo purposes, simulate API call
        // In real app, you would make actual API call here
        
        // Simulate network delay
        new android.os.Handler().postDelayed(() -> {
            // Demo login - accept any valid email/password
            if (email.contains("@") && password.length() >= 6) {
                User user = new User(email, "Demo User");
                user.setId("user_" + System.currentTimeMillis());
                user.setRole("USER");
                user.setActive(true);
                
                String token = "demo_token_" + System.currentTimeMillis();
                callback.onSuccess(user, token);
            } else {
                callback.onError("Invalid email or password");
            }
        }, 1000);
    }

    // Register (API thực tế)
    public void register(String username, String firstName, String lastName, String phoneNumber, String email, String password, String confirmPassword, @NonNull RegisterCallback callback) {
        // Lưu email để sử dụng trong callback
        final String userEmail = email;
        
        // Sử dụng Retrofit hoặc OkHttp để gửi request
        // Giả sử đã có ApiService với phương thức signUp
        apiService.signUp(new SignUpRequest(username, firstName, lastName, phoneNumber, email, password, confirmPassword))
            .enqueue(new Callback<BaseResponse<User>>() {
                @Override
                public void onResponse(Call<BaseResponse<User>> call, Response<BaseResponse<User>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        BaseResponse<User> baseResponse = response.body();
                        
                        // Kiểm tra response thành công
                        if (baseResponse.isSuccess() && baseResponse.getData() != null) {
                            User user = baseResponse.getData();
                            String token = baseResponse.getToken();
                            
                            // Đảm bảo user có đầy đủ thông tin cần thiết
                            if (user.getEmail() == null || user.getEmail().isEmpty()) {
                                // Nếu server không trả về email, sử dụng email từ request
                                user.setEmail(userEmail);
                            }
                            
                            callback.onSuccess(user, token);
                        } else {
                            // Response không thành công nhưng có message
                            String errorMessage = baseResponse.getMessage();
                            if (errorMessage != null && !errorMessage.isEmpty()) {
                                callback.onError(errorMessage);
                            } else {
                                callback.onError("Đăng ký thất bại");
                            }
                        }
                    } else if (response.errorBody() != null) {
                        try {
                            String errorMsg = response.errorBody().string();
                            String message = errorMsg;
                            try {
                                org.json.JSONObject errorObj = new org.json.JSONObject(errorMsg);
                                message = errorObj.optString("message", errorMsg);
                            } catch (Exception ignore) {}
                            callback.onError(message);
                        } catch (Exception e) {
                            callback.onError("Đăng ký thất bại");
                        }
                    } else {
                        callback.onError("Đăng ký thất bại");
                    }
                }
                @Override
                public void onFailure(Call<BaseResponse<User>> call, Throwable t) {
                    callback.onError("Đăng ký thất bại");
                }
            });
    }

    // Forgot Password
    public void forgotPassword(String email, @NonNull AuthCallback callback) {
        new android.os.Handler().postDelayed(() -> {
            if (email.contains("@")) {
                // Simulate success
                User user = new User(email, "Demo User");
                callback.onSuccess(user, null);
            } else {
                callback.onError("Invalid email address");
            }
        }, 1000);
    }

    // Update Profile
    public void updateProfile(User user, @NonNull AuthCallback callback) {
        new android.os.Handler().postDelayed(() -> {
            // Simulate success
            callback.onSuccess(user, null);
        }, 500);
    }

    // Change Password
    public void changePassword(String currentPassword, String newPassword, @NonNull AuthCallback callback) {
        new android.os.Handler().postDelayed(() -> {
            if (newPassword.length() >= 6) {
                // Simulate success
                User user = new User("demo@email.com", "Demo User");
                callback.onSuccess(user, null);
            } else {
                callback.onError("New password must be at least 6 characters");
            }
        }, 500);
    }

    public void loginWithUsername(String username, String password, @NonNull AuthCallback callback) {
        apiService.loginDashboard(new LoginRequest(username, password))
            .enqueue(new Callback<BaseResponse<User>>() {
                @Override
                public void onResponse(Call<BaseResponse<User>> call, Response<BaseResponse<User>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        User user = response.body().getData();
                        // accessToken, refreshToken nằm trong data
                        String accessToken = user.getAccessToken();
                        String refreshToken = user.getRefreshToken();
                        callback.onSuccess(user, accessToken); // truyền accessToken làm token
                    } else if (response.errorBody() != null) {
                        try {
                            String errorMsg = response.errorBody().string();
                            String message = errorMsg;
                            try {
                                org.json.JSONObject errorObj = new org.json.JSONObject(errorMsg);
                                message = errorObj.optString("message", errorMsg);
                            } catch (Exception ignore) {}
                            callback.onError(message);
                        } catch (Exception e) {
                            callback.onError("Đăng nhập thất bại");
                        }
                    } else {
                        callback.onError("Đăng nhập thất bại");
                    }
                }
                @Override
                public void onFailure(Call<BaseResponse<User>> call, Throwable t) {
                    callback.onError("Đăng nhập thất bại");
                }
            });
    }

    // Thêm class SignUpRequest nếu chưa có
    public static class SignUpRequest {
        private String username;
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String email;
        private String password;
        private String confirmPassword;
        
        public SignUpRequest(String username, String firstName, String lastName, String phoneNumber, String email, String password, String confirmPassword) {
            this.username = username;
            this.firstName = firstName;
            this.lastName = lastName;
            this.phoneNumber = phoneNumber;
            this.email = email;
            this.password = password;
            this.confirmPassword = confirmPassword;
        }
        
        // Getters
        public String getUsername() { return username; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getPhoneNumber() { return phoneNumber; }
        public String getEmail() { return email; }
        public String getPassword() { return password; }
        public String getConfirmPassword() { return confirmPassword; }
    }

    public static class LoginRequest {
        private String username;
        private String password;
        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }
} 