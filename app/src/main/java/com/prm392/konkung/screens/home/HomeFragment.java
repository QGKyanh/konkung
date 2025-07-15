package com.prm392.konkung.screens.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.prm392.konkung.R;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView imgStore = view.findViewById(R.id.imgStore);
        Glide.with(this)
            .load("https://ksetup.vn/wp-content/uploads/2023/02/Mo-dai-ly-sua-1.png")
            .placeholder(R.drawable.ic_milk_logo)
            .error(R.drawable.ic_milk_logo)
            .into(imgStore);
        
        // Initialize home screen components here
        setupViews(view);
    }

    private void setupViews(View view) {
        // TODO: Setup home screen views and functionality
    }
}
