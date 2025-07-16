package com.prm392.konkung.screens.register;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.prm392.konkung.R;
import com.prm392.konkung.screens.main.MainActivity;
import com.prm392.konkung.utils.AuthManager;
import com.prm392.konkung.repository.AuthRepository;
import com.bumptech.glide.Glide;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextFirstName, editTextLastName, editTextPhoneNumber, editTextEmail, editTextPassword, editTextConfirmPassword;
    private Button buttonRegister;
    private TextView textViewLogin;
    private ProgressBar progressBar;
    private ImageView imageViewLogo;

    private AuthRepository authRepository;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupClickListeners();
        initRepositories();
    }

    private void initViews() {
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewLogin = findViewById(R.id.textViewLogin);
        progressBar = findViewById(R.id.progressBar);
        imageViewLogo = findViewById(R.id.imageViewLogo);
        if (imageViewLogo != null) {
            Glide.with(this)
                .load("https://res.cloudinary.com/doqd4s5no/image/upload/v1752647278/dlpizjbkuiwdqerrwkig.png")
                .placeholder(R.drawable.ic_milk_logo)
                .error(R.drawable.ic_milk_logo)
                .circleCrop()
                .into(imageViewLogo);
        }
    }

    private void setupClickListeners() {
        buttonRegister.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();
            String firstName = editTextFirstName.getText().toString().trim();
            String lastName = editTextLastName.getText().toString().trim();
            String phoneNumber = editTextPhoneNumber.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String confirmPassword = editTextConfirmPassword.getText().toString().trim();

            if (validateInput(username, firstName, lastName, phoneNumber, email, password, confirmPassword)) {
                performRegister(username, firstName, lastName, phoneNumber, email, password, confirmPassword);
            }
        });

        textViewLogin.setOnClickListener(v -> {
            finish(); // Go back to login
        });
    }

    private void initRepositories() {
        authRepository = AuthRepository.getInstance();
        authManager = AuthManager.getInstance(this);
    }

    private boolean validateInput(String username, String firstName, String lastName, String phoneNumber, String email, String password, String confirmPassword) {
        if (username.isEmpty()) {
            editTextUsername.setError("Username is required");
            return false;
        }
        if (firstName.isEmpty()) {
            editTextFirstName.setError("First name is required");
            return false;
        }
        if (lastName.isEmpty()) {
            editTextLastName.setError("Last name is required");
            return false;
        }
        if (phoneNumber.isEmpty()) {
            editTextPhoneNumber.setError("Phone number is required");
            return false;
        }
        if (!phoneNumber.matches("^\\d{10}$")) {
            editTextPhoneNumber.setError("Phone number must be exactly 10 digits");
            return false;
        }
        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email");
            return false;
        }
        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            return false;
        }
        if (!isValidPassword(password)) {
            editTextPassword.setError("Password must be at least 8 characters, include uppercase, digit, special char");
            return false;
        }
        if (confirmPassword.isEmpty()) {
            editTextConfirmPassword.setError("Please confirm your password");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            editTextConfirmPassword.setError("Passwords do not match");
            return false;
        }
        return true;
    }

    private boolean isValidPassword(String password) {
        // At least 8 chars, 1 uppercase, 1 digit, 1 special char
        return password.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\[\\]{};':\"\\\\|,.<>/?-]).{8,}$");
    }

    private void performRegister(String username, String firstName, String lastName, String phoneNumber, String email, String password, String confirmPassword) {
        showLoading(true);
        authRepository.register(username, firstName, lastName, phoneNumber, email, password, confirmPassword, new AuthRepository.RegisterCallback() {
            @Override
            public void onSuccess(com.prm392.konkung.models.User user, String token) {
                runOnUiThread(() -> {
                    showLoading(false);
                    handleRegistrationSuccess(user, token);
                });
            }
            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    showLoading(false);
                    handleApiError(errorMessage);
                });
            }
        });
    }

    private void handleRegistrationSuccess(com.prm392.konkung.models.User user, String token) {
        try {
            // Xử lý dữ liệu user từ response
            if (user != null) {
                // Đảm bảo user có đầy đủ thông tin
                if (user.getEmail() == null || user.getEmail().isEmpty()) {
                    user.setEmail(editTextEmail.getText().toString().trim());
                }
                if (user.getFirstName() == null || user.getFirstName().isEmpty()) {
                    user.setFirstName(editTextFirstName.getText().toString().trim());
                }
                if (user.getLastName() == null || user.getLastName().isEmpty()) {
                    user.setLastName(editTextLastName.getText().toString().trim());
                }
                if (user.getPhone() == null || user.getPhone().isEmpty()) {
                    user.setPhone(editTextPhoneNumber.getText().toString().trim());
                }
                if (user.getUsername() == null || user.getUsername().isEmpty()) {
                    user.setUsername(editTextUsername.getText().toString().trim());
                }
                
                // Đặt role mặc định nếu chưa có
                if (user.getRole() == null || user.getRole().isEmpty()) {
                    user.setRole("USER");
                }
                
                // Đặt trạng thái active
                user.setActive(true);
            }
            
            // Lưu thông tin đăng nhập
            authManager.login(user, token);
            
            // Hiển thị thông báo thành công
            Toast.makeText(RegisterActivity.this, "Đăng ký thành công! Chào mừng bạn đến với Konkung", Toast.LENGTH_LONG).show();
            
            // Chuyển hướng về trang chủ
            navigateToMain();
            
        } catch (Exception e) {
            // Xử lý lỗi nếu có
            Toast.makeText(RegisterActivity.this, "Có lỗi xảy ra khi xử lý dữ liệu: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void handleApiError(String errorMessage) {
        // Chỉ hiển thị message từ server
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        buttonRegister.setEnabled(!show);
        editTextUsername.setEnabled(!show);
        editTextFirstName.setEnabled(!show);
        editTextLastName.setEnabled(!show);
        editTextPhoneNumber.setEnabled(!show);
        editTextEmail.setEnabled(!show);
        editTextPassword.setEnabled(!show);
        editTextConfirmPassword.setEnabled(!show);
    }

    private void navigateToMain() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
} 