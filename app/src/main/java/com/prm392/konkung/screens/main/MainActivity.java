package com.prm392.konkung.screens.main;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.prm392.konkung.R;
import com.prm392.konkung.screens.chat.ChatFragment;
import com.prm392.konkung.screens.home.HomeFragment;
import com.prm392.konkung.screens.profile.ProfileFragment;
import com.prm392.konkung.screens.products.ProductListFragment;
import com.prm392.konkung.screens.blogs.BlogListFragment;
import com.prm392.konkung.screens.cart.CartFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupBottomNavigation();

        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_shop) {
                selectedFragment = new ProductListFragment();
            } else if (itemId == R.id.nav_cart) {
                selectedFragment = new CartFragment();
            } else if (itemId == R.id.nav_blog) {
                selectedFragment = new BlogListFragment();
            } else if (itemId == R.id.nav_chat) {
                selectedFragment = new ChatFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                return loadFragment(selectedFragment);
            }
            return false;
        });
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            try {
                System.out.println("Loading fragment: " + fragment.getClass().getSimpleName());
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
                System.out.println("Fragment loaded successfully");
                return true;
            } catch (Exception e) {
                System.out.println("Error loading fragment: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
}
