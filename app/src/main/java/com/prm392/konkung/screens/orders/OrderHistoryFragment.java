package com.prm392.konkung.screens.orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.prm392.konkung.R;
import com.prm392.konkung.adapters.OrderHistoryAdapter;
import com.prm392.konkung.models.OrderHistory;
import com.prm392.konkung.repository.OrderHistoryRepository;
import com.prm392.konkung.screens.orders.OrderDetailFragment;

import java.util.List;

public class OrderHistoryFragment extends Fragment {
    
    private static final int PAGE_SIZE = 10;
    private static final String DEMO_USER_ID = "7db4909f-77ad-4bc0-a67b-1fb4cfd8d467"; // Demo user ID
    
    // UI Elements
    private RecyclerView recyclerViewOrders;
    private OrderHistoryAdapter orderHistoryAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private View emptyView;
    
    // Data
    private OrderHistoryRepository orderHistoryRepository;
    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean hasNextPage = true;
    private String userId;

    public static OrderHistoryFragment newInstance() {
        return new OrderHistoryFragment();
    }

    public static OrderHistoryFragment newInstance(String userId) {
        OrderHistoryFragment fragment = new OrderHistoryFragment();
        Bundle args = new Bundle();
        args.putString("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (getArguments() != null) {
            userId = getArguments().getString("userId");
        }
        
        if (userId == null || userId.isEmpty()) {
            userId = DEMO_USER_ID; // Use demo user ID for testing
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupRecyclerView();
        setupSwipeRefresh();
        initRepository();
        loadOrderHistory();
    }

    private void initViews(View view) {
        recyclerViewOrders = view.findViewById(R.id.recyclerViewOrders);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        progressBar = view.findViewById(R.id.progressBar);
        emptyView = view.findViewById(R.id.emptyView);
        
        // Setup start shopping button
        view.findViewById(R.id.btnStartShopping).setOnClickListener(v -> {
            // Navigate to Shop tab
            if (getActivity() instanceof com.prm392.konkung.screens.main.MainActivity) {
                com.prm392.konkung.screens.main.MainActivity mainActivity = 
                    (com.prm392.konkung.screens.main.MainActivity) getActivity();
                android.view.View bottomNav = mainActivity.findViewById(R.id.bottom_navigation);
                if (bottomNav instanceof com.google.android.material.bottomnavigation.BottomNavigationView) {
                    ((com.google.android.material.bottomnavigation.BottomNavigationView) bottomNav)
                        .setSelectedItemId(R.id.nav_shop);
                }
            }
        });
    }

    private void setupRecyclerView() {
        orderHistoryAdapter = new OrderHistoryAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerViewOrders.setLayoutManager(layoutManager);
        recyclerViewOrders.setAdapter(orderHistoryAdapter);

        // Set click listener
        orderHistoryAdapter.setOnOrderClickListener(new OrderHistoryAdapter.OnOrderClickListener() {
            @Override
            public void onOrderClick(OrderHistory order) {
                // Navigate to order detail
                OrderDetailFragment orderDetailFragment = OrderDetailFragment.newInstance(userId, order.getId());
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, orderDetailFragment)
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onReorderClick(OrderHistory order) {
                // Handle reorder
                Toast.makeText(getContext(), "Đặt lại đơn hàng: " + order.getId(), Toast.LENGTH_SHORT).show();
            }
        });

        // Pagination scroll listener
        recyclerViewOrders.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                
                if (dy > 0) { // Scrolling down
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager != null) {
                        int visibleItemCount = layoutManager.getChildCount();
                        int totalItemCount = layoutManager.getItemCount();
                        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                        if (!isLoading && hasNextPage) {
                            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 3) {
                                loadMoreOrders();
                            }
                        }
                    }
                }
            }
        });
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(this::refreshOrders);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.blue,
                R.color.green,
                R.color.orange
        );
    }

    private void initRepository() {
        orderHistoryRepository = OrderHistoryRepository.getInstance();
    }

    private void loadOrderHistory() {
        currentPage = 1;
        hasNextPage = true;
        
        orderHistoryRepository.getUserOrderHistory(userId, currentPage, PAGE_SIZE, 
                new OrderHistoryRepository.OrderHistoryListCallback() {
            @Override
            public void onSuccess(List<OrderHistory> orders, int page, boolean hasNextPage) {
                OrderHistoryFragment.this.hasNextPage = hasNextPage;
                isLoading = false;
                
                if (isAdded()) {
                    hideLoading();
                    orderHistoryAdapter.setOrders(orders);
                    
                    if (orders.isEmpty()) {
                        showEmptyView();
                    } else {
                        hideEmptyView();
                    }
                }
            }

            @Override
            public void onError(String errorMessage) {
                isLoading = false;
                if (isAdded()) {
                    hideLoading();
                    showError(errorMessage);
                    
                    if (orderHistoryAdapter.getItemCount() == 0) {
                        showEmptyView();
                    }
                }
            }

            @Override
            public void onLoading() {
                if (currentPage == 1) {
                    showLoading();
                }
                isLoading = true;
            }
        });
    }

    private void loadMoreOrders() {
        if (isLoading || !hasNextPage) return;
        
        currentPage++;
        
        orderHistoryRepository.getUserOrderHistory(userId, currentPage, PAGE_SIZE, 
                new OrderHistoryRepository.OrderHistoryListCallback() {
            @Override
            public void onSuccess(List<OrderHistory> orders, int page, boolean hasNextPage) {
                OrderHistoryFragment.this.hasNextPage = hasNextPage;
                isLoading = false;
                
                if (isAdded()) {
                    orderHistoryAdapter.addOrders(orders);
                }
            }

            @Override
            public void onError(String errorMessage) {
                isLoading = false;
                currentPage--; // Reset page number on error
                if (isAdded()) {
                    showError("Không thể tải thêm đơn hàng: " + errorMessage);
                }
            }

            @Override
            public void onLoading() {
                isLoading = true;
            }
        });
    }

    private void refreshOrders() {
        orderHistoryAdapter.clearOrders();
        loadOrderHistory();
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerViewOrders.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        recyclerViewOrders.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void showEmptyView() {
        emptyView.setVisibility(View.VISIBLE);
        recyclerViewOrders.setVisibility(View.GONE);
    }

    private void hideEmptyView() {
        emptyView.setVisibility(View.GONE);
        recyclerViewOrders.setVisibility(View.VISIBLE);
    }

    private void showError(String errorMessage) {
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
    }
}
