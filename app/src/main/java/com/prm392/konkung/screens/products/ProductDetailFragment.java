package com.prm392.konkung.screens.products;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.prm392.konkung.R;
import com.prm392.konkung.models.Product;
import com.prm392.konkung.repository.ProductRepository;
// CartManager import removed - using server APIs instead
import com.prm392.konkung.screens.checkout.CheckoutFragment;
import com.prm392.konkung.network.ApiClient;
import com.prm392.konkung.network.ApiService;
import com.prm392.konkung.network.responses.BaseResponse;
import com.prm392.konkung.models.AddToCartRequest;
import com.prm392.konkung.models.CartResponse;
import com.prm392.konkung.utils.AuthManager;
import com.prm392.konkung.screens.main.MainActivity;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProductDetailFragment extends Fragment {
    
    private static final String ARG_PRODUCT_ID = "product_id";
    
    // UI Elements
    private ImageView imageViewProduct;
    private TextView textViewProductName;
    private TextView textViewBrand;
    private TextView textViewCategory;
    private TextView textViewCurrentPrice;
    private TextView textViewOriginalPrice;
    private TextView textViewDiscountPercentage;
    private TextView textViewQuantity;
    private TextView textViewStatus;
    private TextView textViewUnit;
    private TextView textViewRating;
    private TextView textViewOrderCount;
    private TextView textViewCreatedDate;
    private TextView textViewDescription;
    private Button buttonAddToCart;
    private Button buttonBuyNow;
    private ImageView buttonBack;
    private ProgressBar progressBar;
    private View discountBadge;
    private View contentView;
    
    // Data
    private String productId;
    private Product currentProduct;
    private ProductRepository productRepository;
    private ApiService apiService;
    private NumberFormat currencyFormat;
    private SimpleDateFormat dateFormat;

    public static ProductDetailFragment newInstance(String productId) {
        ProductDetailFragment fragment = new ProductDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PRODUCT_ID, productId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productId = getArguments().getString(ARG_PRODUCT_ID);
        }
        
        // Initialize formatters
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupClickListeners();
        initRepositories();
        loadProductDetail();
    }

    private void initViews(View view) {
        imageViewProduct = view.findViewById(R.id.imageViewProduct);
        textViewProductName = view.findViewById(R.id.textViewProductName);
        textViewBrand = view.findViewById(R.id.textViewBrand);
        textViewCategory = view.findViewById(R.id.textViewCategory);
        textViewCurrentPrice = view.findViewById(R.id.textViewCurrentPrice);
        textViewOriginalPrice = view.findViewById(R.id.textViewOriginalPrice);
        textViewDiscountPercentage = view.findViewById(R.id.textViewDiscountPercentage);
        textViewQuantity = view.findViewById(R.id.textViewQuantity);
        textViewStatus = view.findViewById(R.id.textViewStatus);
        textViewUnit = view.findViewById(R.id.textViewUnit);
        textViewRating = view.findViewById(R.id.textViewRating);
        textViewOrderCount = view.findViewById(R.id.textViewOrderCount);
        textViewCreatedDate = view.findViewById(R.id.textViewCreatedDate);
        textViewDescription = view.findViewById(R.id.textViewDescription);
        buttonAddToCart = view.findViewById(R.id.buttonAddToCart);
        buttonBuyNow = view.findViewById(R.id.buttonBuyNow);
        buttonBack = view.findViewById(R.id.buttonBack);
        progressBar = view.findViewById(R.id.progressBar);
        discountBadge = view.findViewById(R.id.discountBadge);
        contentView = view.findViewById(R.id.contentView);
    }

    private void setupClickListeners() {
        buttonBack.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        });

        buttonAddToCart.setOnClickListener(v -> addToCart());
        buttonBuyNow.setOnClickListener(v -> buyNow());
    }

    private void initRepositories() {
        productRepository = ProductRepository.getInstance();
        apiService = ApiClient.getApiService();
    }

    private void loadProductDetail() {
        if (productId == null) {
            showError("Product ID is missing");
            return;
        }

        showLoading();
        productRepository.getProduct(productId, new ProductRepository.ProductCallback() {
            @Override
            public void onSuccess(Product product) {
                currentProduct = product;
                displayProductInfo();
                hideLoading();
            }

            @Override
            public void onError(String errorMessage) {
                showError(errorMessage);
                hideLoading();
            }

            @Override
            public void onLoading() {
                showLoading();
            }
        });
    }

    private void displayProductInfo() {
        if (currentProduct == null) return;

        // Product name
        textViewProductName.setText(currentProduct.getName());
        
        // Brand and Category
        textViewBrand.setText(currentProduct.getBrand());
        textViewCategory.setText(currentProduct.getCategory());
        
        // Unit
        textViewUnit.setText(currentProduct.getUnit());

        // Load product image
        Glide.with(this)
                .load(currentProduct.getThumbnail())
                .placeholder(R.drawable.ic_milk_logo)
                .error(R.drawable.ic_milk_logo)
                .into(imageViewProduct);

        // Price handling
        if (currentProduct.isOnSale()) {
            // Show sale price
            textViewCurrentPrice.setText(currencyFormat.format(currentProduct.getSalePrice()));
            textViewOriginalPrice.setText(currencyFormat.format(currentProduct.getOriginalPrice()));
            textViewOriginalPrice.setPaintFlags(textViewOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            textViewOriginalPrice.setVisibility(View.VISIBLE);
            
            // Show discount percentage
            textViewDiscountPercentage.setText(String.format("-%d%%", (int) currentProduct.getDiscountPercentage()));
            discountBadge.setVisibility(View.VISIBLE);
        } else {
            // Show regular price
            textViewCurrentPrice.setText(currencyFormat.format(currentProduct.getOriginalPrice()));
            textViewOriginalPrice.setVisibility(View.GONE);
            discountBadge.setVisibility(View.GONE);
        }

        // Quantity and Status
        displayQuantityAndStatus();
        
        // Rating
        if (currentProduct.getRatingCount() > 0) {
            textViewRating.setText(String.format("%.1f ★ (%d đánh giá)", 
                    currentProduct.getAverageRating(), currentProduct.getRatingCount()));
        } else {
            textViewRating.setText("Chưa có đánh giá");
        }
        
        // Order count
        textViewOrderCount.setText(String.format("Đã bán %d", currentProduct.getOrderCount()));
        
        // Created date
        try {
            String createdAt = currentProduct.getCreatedAt();
            if (createdAt != null && !createdAt.startsWith("0001")) {
                // Parse ISO date format and display
                textViewCreatedDate.setText("Ngày tạo: " + createdAt.substring(0, 10));
            } else {
                textViewCreatedDate.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            textViewCreatedDate.setVisibility(View.GONE);
        }
        
        // Description
        if (currentProduct.getDescription() != null && !currentProduct.getDescription().trim().isEmpty()) {
            textViewDescription.setText(currentProduct.getDescription());
            textViewDescription.setVisibility(View.VISIBLE);
        } else {
            textViewDescription.setText("Không có mô tả sản phẩm");
        }
        
        // Cập nhật trạng thái button thêm vào giỏ hàng
        updateAddToCartButton();
    }

    private void displayQuantityAndStatus() {
        // Quantity
        if (currentProduct.getQuantity() > 0) {
            textViewQuantity.setText("Còn " + currentProduct.getQuantity() + " sản phẩm");
            textViewQuantity.setTextColor(getResources().getColor(R.color.green, null));
        } else {
            textViewQuantity.setText("Hết hàng");
            textViewQuantity.setTextColor(getResources().getColor(R.color.red, null));
        }

        // Status and button states
        switch (currentProduct.getStatus()) {
            case "SELLING":
                textViewStatus.setText("Đang bán");
                textViewStatus.setTextColor(getResources().getColor(R.color.green, null));
                buttonAddToCart.setEnabled(currentProduct.getQuantity() > 0);
                buttonBuyNow.setEnabled(currentProduct.getQuantity() > 0);
                break;
            case "PREORDER":
                textViewStatus.setText("Đặt trước");
                textViewStatus.setTextColor(getResources().getColor(R.color.orange, null));
                buttonAddToCart.setEnabled(true);
                buttonBuyNow.setEnabled(true);
                buttonAddToCart.setText("Đặt trước");
                buttonBuyNow.setText("Mua ngay (Đặt trước)");
                break;
            default:
                textViewStatus.setText("Không khả dụng");
                textViewStatus.setTextColor(getResources().getColor(R.color.red, null));
                buttonAddToCart.setEnabled(false);
                buttonBuyNow.setEnabled(false);
                break;
        }

        // Update button appearance
        buttonAddToCart.setAlpha(buttonAddToCart.isEnabled() ? 1.0f : 0.5f);
        buttonBuyNow.setAlpha(buttonBuyNow.isEnabled() ? 1.0f : 0.5f);
    }

    private void addToCart() {
        if (currentProduct != null) {
            String userId = AuthManager.getInstance().getUserId();
            ApiService apiService = ApiClient.getApiService();
            AddToCartRequest req = new AddToCartRequest(currentProduct.getId(), 1);
            apiService.addToCart(userId, req).enqueue(new retrofit2.Callback<BaseResponse<CartResponse>>() {
                @Override
                public void onResponse(retrofit2.Call<BaseResponse<CartResponse>> call, retrofit2.Response<BaseResponse<CartResponse>> response) {
                    if (response.isSuccessful()) {
                        String message = currentProduct.isPreOrder() ? "Đã thêm vào giỏ hàng (đặt trước)" : "Đã thêm vào giỏ hàng";
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        MainActivity.updateCartBadgeFromFragment(requireActivity());
                        // Optionally: cập nhật UI/badge
                    } else {
                        Toast.makeText(getContext(), "Lỗi khi thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(retrofit2.Call<BaseResponse<CartResponse>> call, Throwable t) {
                    Toast.makeText(getContext(), "Lỗi khi thêm vào giỏ hàng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateAddToCartButton() {
        if (currentProduct != null) {
            // For now, just show the default text since we don't have cart info
            // In a real implementation, you would fetch cart info from server
            buttonAddToCart.setText("Thêm vào giỏ hàng");
        }
    }

    private void buyNow() {
        if (currentProduct != null) {
            String userId = AuthManager.getInstance().getUserId();
            ApiService apiService = ApiClient.getApiService();
            AddToCartRequest req = new AddToCartRequest(currentProduct.getId(), 1);
            apiService.addToCart(userId, req).enqueue(new retrofit2.Callback<BaseResponse<CartResponse>>() {
                @Override
                public void onResponse(retrofit2.Call<BaseResponse<CartResponse>> call, retrofit2.Response<BaseResponse<CartResponse>> response) {
                    if (response.isSuccessful()) {
                        // Navigate to checkout screen
                        MainActivity.updateCartBadgeFromFragment(requireActivity());
                        CheckoutFragment checkoutFragment = new CheckoutFragment();
                        getParentFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, checkoutFragment)
                            .addToBackStack(null)
                            .commit();
                    } else {
                        Toast.makeText(getContext(), "Lỗi khi thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(retrofit2.Call<BaseResponse<CartResponse>> call, Throwable t) {
                    Toast.makeText(getContext(), "Lỗi khi thêm vào giỏ hàng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        contentView.setVisibility(View.GONE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        contentView.setVisibility(View.VISIBLE);
    }

    private void showError(String errorMessage) {
        Toast.makeText(getContext(), "Lỗi: " + errorMessage, Toast.LENGTH_LONG).show();
        // Optionally, you can show an error view instead of toast
    }
}
