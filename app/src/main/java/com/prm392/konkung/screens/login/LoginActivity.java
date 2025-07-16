package com.prm392.konkung.screens.login;

import android.content.Intent;
import android.os.Bundle;
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
import com.prm392.konkung.screens.register.RegisterActivity;
import com.prm392.konkung.utils.AuthManager;
import com.prm392.konkung.repository.AuthRepository;
import com.bumptech.glide.Glide;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword;
    private Button buttonLogin;
    private TextView textViewRegister;
    private ProgressBar progressBar;
    private ImageView imageViewLogo;

    private AuthRepository authRepository;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Check if user is already logged in
        authManager = AuthManager.getInstance(this);
        if (authManager.isLoggedIn()) {
            navigateToMain();
            return;
        }

        initViews();
        setupClickListeners();
        initRepositories();
    }

    private void initViews() {
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewRegister = findViewById(R.id.textViewRegister);
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
        buttonLogin.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (validateInput(username, password)) {
                performLogin(username, password);
            }
        });

        textViewRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void initRepositories() {
        authRepository = AuthRepository.getInstance();
    }

    private boolean validateInput(String username, String password) {
        if (username.isEmpty()) {
            editTextUsername.setError("Username is required");
            return false;
        }
        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            return false;
        }
        if (password.length() < 6) {
            editTextPassword.setError("Password must be at least 6 characters");
            return false;
        }
        return true;
    }

    private void performLogin(String username, String password) {
        showLoading(true);
        authRepository.loginWithUsername(username, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(com.prm392.konkung.models.User user, String token) {
                runOnUiThread(() -> {
                    showLoading(false);
                    authManager.login(user, token);
                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    navigateToMain();
                });
            }
            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        buttonLogin.setEnabled(!show);
        editTextUsername.setEnabled(!show); // Changed to editTextUsername
        editTextPassword.setEnabled(!show);
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
