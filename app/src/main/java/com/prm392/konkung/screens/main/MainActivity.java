package com.prm392.konkung.screens.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.prm392.konkung.R;
import com.prm392.konkung.screens.home.HomeFragment;
import com.prm392.konkung.screens.profile.ProfileFragment;
import com.prm392.konkung.screens.products.ProductListFragment;
import com.prm392.konkung.screens.cart.CartFragment;
import com.prm392.konkung.screens.login.LoginActivity;
import com.prm392.konkung.utils.AuthManager;
// CartManager import removed - using server APIs instead
import com.prm392.konkung.models.CartResponse;
import com.prm392.konkung.network.ApiClient;
import com.prm392.konkung.network.ApiService;
import com.prm392.konkung.network.responses.BaseResponse;

import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private BadgeDrawable cartBadge;
    private NumberFormat currencyFormat;
    private ApiService apiService;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check authentication
        AuthManager authManager = AuthManager.getInstance(this);
        if (!authManager.isLoggedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        initViews();
        setupBottomNavigation();
        apiService = ApiClient.getApiService();
        userId = AuthManager.getInstance().getUserId();
        updateCartBadgeFromServer();

        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    }

    private void setupBottomNavigation() {
        cartBadge = bottomNavigationView.getOrCreateBadge(R.id.nav_cart);
        cartBadge.setVisible(false);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_shop) {
                selectedFragment = new ProductListFragment();
            } else if (itemId == R.id.nav_cart) {
                selectedFragment = new CartFragment();
            } else if (itemId == R.id.nav_more) {
                selectedFragment = new MoreFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                boolean loaded = loadFragment(selectedFragment);
                if (itemId == R.id.nav_cart || itemId == R.id.nav_home || itemId == R.id.nav_shop) {
                    updateCartBadgeFromServer();
                }
                return loaded;
            }
            return false;
        });
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            try {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    private void updateCartBadgeFromServer() {
        if (userId == null || apiService == null || cartBadge == null) return;
        apiService.getCart(userId).enqueue(new retrofit2.Callback<BaseResponse<CartResponse>>() {
            @Override
            public void onResponse(retrofit2.Call<BaseResponse<CartResponse>> call, retrofit2.Response<BaseResponse<CartResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    int itemCount = response.body().getData().getTotalQuantity();
                    if (itemCount > 0) {
                        cartBadge.setVisible(true);
                        cartBadge.setNumber(itemCount);
                    } else {
                        cartBadge.setVisible(false);
                    }
                } else {
                    cartBadge.setVisible(false);
                }
            }
            @Override
            public void onFailure(retrofit2.Call<BaseResponse<CartResponse>> call, Throwable t) {
                cartBadge.setVisible(false);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadgeFromServer();
    }

    // Static helper để fragment gọi cập nhật badge
    public static void updateCartBadgeFromFragment(android.app.Activity activity) {
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).updateCartBadgeFromServer();
        }
    }
}
