package com.prm392.konkung.screens.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.prm392.konkung.R;
import com.prm392.konkung.screens.login.LoginActivity;
import com.prm392.konkung.utils.AuthManager;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupViews(view);
    }

    private void setupViews(View view) {
        AuthManager authManager = AuthManager.getInstance(requireContext());
        
        // Initialize views
        ImageView imageViewAvatar = view.findViewById(R.id.imageViewAvatar);
        TextView textViewName = view.findViewById(R.id.textViewName);
        TextView textViewEmail = view.findViewById(R.id.textViewEmail);
        TextView textViewPhone = view.findViewById(R.id.textViewPhone);
        TextView textViewAddress = view.findViewById(R.id.textViewAddress);
        Button buttonEditProfile = view.findViewById(R.id.buttonEditProfile);
        Button buttonChangePassword = view.findViewById(R.id.buttonChangePassword);
        Button buttonLogout = view.findViewById(R.id.buttonLogout);

        // Set user data
        if (authManager.getCurrentUser() != null) {
            textViewName.setText(authManager.getUserDisplayName());
            textViewEmail.setText(authManager.getUserEmail());
            
            if (authManager.getCurrentUser().getPhone() != null && !authManager.getCurrentUser().getPhone().isEmpty()) {
                textViewPhone.setText(authManager.getCurrentUser().getPhone());
            } else {
                textViewPhone.setText("Not provided");
            }
            
            if (authManager.getCurrentUser().getAddress() != null && !authManager.getCurrentUser().getAddress().isEmpty()) {
                textViewAddress.setText(authManager.getCurrentUser().getAddress());
            } else {
                textViewAddress.setText("Not provided");
            }
        }

        // Setup click listeners
        buttonEditProfile.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Edit Profile coming soon!", Toast.LENGTH_SHORT).show();
        });

        buttonChangePassword.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Change Password coming soon!", Toast.LENGTH_SHORT).show();
        });

        buttonLogout.setOnClickListener(v -> {
            authManager.logout();
            Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
            
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
