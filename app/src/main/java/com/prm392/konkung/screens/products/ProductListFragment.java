package com.prm392.konkung.screens.products;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.prm392.konkung.R;
import com.prm392.konkung.adapters.ProductAdapter;
import com.prm392.konkung.models.Product;
import com.prm392.konkung.repository.ProductRepository;
// CartManager import removed - using server APIs instead
import com.prm392.konkung.network.ApiClient;
import com.prm392.konkung.network.ApiService;
import com.prm392.konkung.network.responses.BaseResponse;
import com.prm392.konkung.models.AddToCartRequest;
import com.prm392.konkung.models.CartResponse;
import com.prm392.konkung.utils.AuthManager;
import com.prm392.konkung.screens.main.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ProductListFragment extends Fragment implements ProductAdapter.OnProductClickListener {
    
    private RecyclerView recyclerViewProducts;
    private ProductAdapter productAdapter;
    private EditText editTextSearch;
    private ProgressBar progressBar;
    private TextView textViewEmpty;
    private SwipeRefreshLayout swipeRefreshLayout;
    
    private ProductRepository productRepository;
    private ApiService apiService;
    
    private List<Product> allProducts = new ArrayList<>();
    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean hasNextPage = true;
    private String currentSearchQuery = "";
    private Timer searchTimer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupRecyclerView();
        setupSearch();
        setupSwipeRefresh();
        initRepositories();
        loadProducts();
    }

    private void initViews(View view) {
        recyclerViewProducts = view.findViewById(R.id.recyclerViewProducts);
        editTextSearch = view.findViewById(R.id.editTextSearch);
        progressBar = view.findViewById(R.id.progressBar);
        textViewEmpty = view.findViewById(R.id.textViewEmpty);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(allProducts, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerViewProducts.setLayoutManager(gridLayoutManager);
        recyclerViewProducts.setAdapter(productAdapter);

        // Setup pagination
        recyclerViewProducts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                
                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && hasNextPage && !isLoading) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        loadMoreProducts();
                    }
                }
            }
        });
    }

    private void setupSearch() {
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchTimer != null) {
                    searchTimer.cancel();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchTimer = new Timer();
                searchTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                String query = s.toString().trim();
                                if (!query.equals(currentSearchQuery)) {
                                    currentSearchQuery = query;
                                    resetPagination();
                                    if (query.isEmpty()) {
                                        loadProducts();
                                    } else {
                                        searchProducts(query);
                                    }
                                }
                            });
                        }
                    }
                }, 500); // 500ms delay for search
            }
        });
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            resetPagination();
            loadProducts();
        });
    }

    private void initRepositories() {
        try {
            productRepository = ProductRepository.getInstance();
            apiService = ApiClient.getApiService();
        } catch (Exception e) {
            e.printStackTrace();
            if (getContext() != null) {
                Toast.makeText(getContext(), "Lỗi khởi tạo API service", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadProducts() {
        System.out.println("Loading products...");
        productRepository.getAllProducts(currentPage, 10, new ProductRepository.ProductListCallback() {
            @Override
            public void onSuccess(List<Product> products, boolean hasNextPage, int totalCount) {
                handleProductsLoaded(products, hasNextPage);
            }

            @Override
            public void onError(String errorMessage) {
                handleError(errorMessage);
            }

            @Override
            public void onLoading() {
                handleLoading();
            }
        });
    }

    private void searchProducts(String query) {
        productRepository.searchProducts(query, currentPage, 10, new ProductRepository.ProductListCallback() {
            @Override
            public void onSuccess(List<Product> products, boolean hasNextPage, int totalCount) {
                handleProductsLoaded(products, hasNextPage);
            }

            @Override
            public void onError(String errorMessage) {
                handleError(errorMessage);
            }

            @Override
            public void onLoading() {
                handleLoading();
            }
        });
    }

    private void loadMoreProducts() {
        if (!hasNextPage || isLoading) return;
        
        currentPage++;
        if (currentSearchQuery.isEmpty()) {
            loadProducts();
        } else {
            searchProducts(currentSearchQuery);
        }
    }

    private void handleProductsLoaded(List<Product> products, boolean hasNextPage) {
        System.out.println("Products loaded: " + (products != null ? products.size() : 0));
        isLoading = false;
        swipeRefreshLayout.setRefreshing(false);
        progressBar.setVisibility(View.GONE);
        this.hasNextPage = hasNextPage;

        if (currentPage == 1) {
            // First page or refresh
            allProducts.clear();
            if (products != null) {
                allProducts.addAll(products);
                System.out.println("Added " + products.size() + " products to list");
            }
            productAdapter.updateProducts(allProducts);
        } else {
            // Load more
            if (products != null) {
                allProducts.addAll(products);
                productAdapter.addProducts(products);
            }
        }

        updateEmptyState();
    }

    private void handleError(String errorMessage) {
        isLoading = false;
        swipeRefreshLayout.setRefreshing(false);
        progressBar.setVisibility(View.GONE);
        
        Toast.makeText(getContext(), "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
        updateEmptyState();
    }

    private void handleLoading() {
        if (currentPage == 1 && !swipeRefreshLayout.isRefreshing()) {
            progressBar.setVisibility(View.VISIBLE);
        }
        isLoading = true;
    }

    private void updateEmptyState() {
        if (allProducts.isEmpty()) {
            textViewEmpty.setVisibility(View.VISIBLE);
            recyclerViewProducts.setVisibility(View.GONE);
            if (currentSearchQuery.isEmpty()) {
                textViewEmpty.setText("Không có sản phẩm nào");
            } else {
                textViewEmpty.setText("Không tìm thấy sản phẩm cho từ khóa: " + currentSearchQuery);
            }
        } else {
            textViewEmpty.setVisibility(View.GONE);
            recyclerViewProducts.setVisibility(View.VISIBLE);
        }
    }

    private void resetPagination() {
        currentPage = 1;
        hasNextPage = true;
        isLoading = false;
    }

    @Override
    public void onProductClick(Product product) {
        // Navigate to product detail
        ProductDetailFragment detailFragment = ProductDetailFragment.newInstance(product.getId());
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onAddToCartClick(Product product) {
        try {
            if (product.isAvailable() || product.isPreOrder()) {
                String userId = AuthManager.getInstance().getUserId();
                ApiService apiService = ApiClient.getApiService();
                AddToCartRequest req = new AddToCartRequest(product.getId(), 1);
                apiService.addToCart(userId, req).enqueue(new retrofit2.Callback<BaseResponse<CartResponse>>() {
                    @Override
                    public void onResponse(retrofit2.Call<BaseResponse<CartResponse>> call, retrofit2.Response<BaseResponse<CartResponse>> response) {
                        if (response.isSuccessful()) {
                            String message = product.isPreOrder() ? "Đã thêm vào giỏ hàng (đặt trước)" : "Đã thêm vào giỏ hàng";
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            MainActivity.updateCartBadgeFromFragment(requireActivity());
                            // Optionally: reload cart badge/main activity
                        } else {
                            Toast.makeText(getContext(), "Lỗi khi thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(retrofit2.Call<BaseResponse<CartResponse>> call, Throwable t) {
                        Toast.makeText(getContext(), "Lỗi khi thêm vào giỏ hàng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getContext(), "Sản phẩm không khả dụng", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Lỗi khi thêm vào giỏ hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (searchTimer != null) {
            searchTimer.cancel();
        }
    }
}
