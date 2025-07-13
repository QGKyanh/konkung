package com.prm392.konkung.screens.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.prm392.konkung.R;
import com.prm392.konkung.screens.blogs.BlogListFragment;
import com.prm392.konkung.screens.chat.ChatFragment;

public class MoreFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_more, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupViews(view);
    }

    private void setupViews(View view) {
        // Blog option
        LinearLayout blogOption = view.findViewById(R.id.optionBlog);
        blogOption.setOnClickListener(v -> {
            // Navigate to BlogListFragment
            BlogListFragment blogFragment = new BlogListFragment();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, blogFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Chat option
        LinearLayout chatOption = view.findViewById(R.id.optionChat);
        chatOption.setOnClickListener(v -> {
            // Navigate to ChatFragment
            ChatFragment chatFragment = new ChatFragment();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, chatFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }
} 