package com.prm392.konkung.screens.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.prm392.konkung.R;
import com.prm392.konkung.screens.main.MainActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private TextView textViewRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewRegister = findViewById(R.id.textViewRegister);
    }

    private void setupClickListeners() {
        buttonLogin.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (validateInput(email, password)) {
                // For demo purposes, accept any email and password
                performLogin();
            }
        });

        textViewRegister.setOnClickListener(v -> {
            // TODO: Navigate to register screen
            Toast.makeText(this, "Register functionality coming soon!", Toast.LENGTH_SHORT).show();
        });
    }

    private boolean validateInput(String email, String password) {
        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
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

    private void performLogin() {
        // Simulate login process
        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
        
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
