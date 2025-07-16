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
import com.prm392.konkung.models.User;
import com.prm392.konkung.network.ApiClient;
import com.prm392.konkung.network.ApiService;
import com.prm392.konkung.network.responses.BaseResponse;
import com.bumptech.glide.Glide;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.prm392.konkung.screens.login.LoginActivity;
import com.prm392.konkung.utils.AuthManager;
import com.prm392.konkung.screens.orders.OrderHistoryFragment;

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
        // Initialize views
        ImageView imageViewAvatar = view.findViewById(R.id.imageViewAvatar);
        TextView textViewFullName = view.findViewById(R.id.textViewFullName);
        TextView textViewUsername = view.findViewById(R.id.textViewUsername);
        TextView textViewEmail = view.findViewById(R.id.textViewEmail);
        TextView textViewPhone = view.findViewById(R.id.textViewPhone);
        Button buttonLogout = view.findViewById(R.id.buttonLogout);
        
        // Order History option
        View optionOrderHistory = view.findViewById(R.id.optionOrderHistory);
        optionOrderHistory.setOnClickListener(v -> {
            // Navigate to Order History Fragment
            String userId = AuthManager.getInstance(requireContext()).getUserId();
            OrderHistoryFragment orderHistoryFragment = OrderHistoryFragment.newInstance(userId);
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, orderHistoryFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Gọi API lấy profile
        ApiService apiService = ApiClient.getApiService();
        apiService.getUserProfile().enqueue(new Callback<BaseResponse<User>>() {
            @Override
            public void onResponse(Call<BaseResponse<User>> call, Response<BaseResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    User user = response.body().getData();
                    String fullName = (user.getFirstName() != null ? user.getFirstName() : "") +
                            (user.getLastName() != null ? (" " + user.getLastName()) : "");
                    textViewFullName.setText(fullName.trim());
                    textViewUsername.setText("@" + (user.getUsername() != null ? user.getUsername() : ""));
                    textViewEmail.setText(user.getEmail() != null ? user.getEmail() : "");
                    textViewPhone.setText("Số điện thoại: " + (user.getPhoneNumber() != null ? user.getPhoneNumber() : ""));
                    if (user.getProfilePictureUrl() != null && !user.getProfilePictureUrl().isEmpty()) {
                        Glide.with(requireContext())
                            .load(user.getProfilePictureUrl())
                            .placeholder(R.drawable.ic_profile_placeholder)
                            .error(R.drawable.ic_profile_placeholder)
                            .circleCrop()
                            .into(imageViewAvatar);
                    } else {
                        imageViewAvatar.setImageResource(R.drawable.ic_profile_placeholder);
                    }
                } else {
                    Toast.makeText(requireContext(), "Không lấy được thông tin tài khoản", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<BaseResponse<User>> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        buttonLogout.setOnClickListener(v -> {
            com.prm392.konkung.utils.AuthManager.getInstance(requireContext()).logout();
            Toast.makeText(requireContext(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(requireContext(), com.prm392.konkung.screens.login.LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
