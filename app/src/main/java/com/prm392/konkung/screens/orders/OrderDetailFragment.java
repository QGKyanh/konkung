package com.prm392.konkung.screens.orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prm392.konkung.R;
import com.prm392.konkung.models.OrderDetailResponse;
import com.prm392.konkung.models.OrderLog;
import com.prm392.konkung.repository.OrderDetailRepository;
import com.prm392.konkung.adapters.OrderDetailAdapter;

public class OrderDetailFragment extends Fragment {
    
    private static final String ARG_USER_ID = "userId";
    private static final String ARG_ORDER_ID = "orderId";
    
    // UI Elements
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private LinearLayout contentLayout;
    private TextView textOrderId;
    private TextView textOrderStatus;
    private TextView textOrderDate;
    private TextView textReceiverName;
    private TextView textEmail;
    private TextView textPhone;
    private TextView textAddress;
    private TextView textNote;
    private RecyclerView recyclerViewProducts;
    private TextView textTotalBeforeDiscount;
    private TextView textVoucherDiscount;
    private TextView textPointDiscount;
    private TextView textShippingFee;
    private TextView textTotalAmount;
    private TextView textPaymentMethod;
    private TextView textPaymentStatus;
    private TextView textReceivingPoints;
    private RecyclerView recyclerViewLogs;
    private LinearLayout layoutPaymentInfo;
    private LinearLayout layoutOrderLogs;
    
    // Data
    private OrderDetailRepository orderDetailRepository;
    private OrderDetailAdapter orderDetailAdapter;
    private String userId;
    private String orderId;

    public static OrderDetailFragment newInstance(String userId, String orderId) {
        OrderDetailFragment fragment = new OrderDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        args.putString(ARG_ORDER_ID, orderId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USER_ID);
            orderId = getArguments().getString(ARG_ORDER_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupToolbar();
        setupRecyclerView();
        initRepository();
        loadOrderDetail();
    }

    private void initViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        progressBar = view.findViewById(R.id.progressBar);
        contentLayout = view.findViewById(R.id.contentLayout);
        textOrderId = view.findViewById(R.id.textOrderId);
        textOrderStatus = view.findViewById(R.id.textOrderStatus);
        textOrderDate = view.findViewById(R.id.textOrderDate);
        textReceiverName = view.findViewById(R.id.textReceiverName);
        textEmail = view.findViewById(R.id.textEmail);
        textPhone = view.findViewById(R.id.textPhone);
        textAddress = view.findViewById(R.id.textAddress);
        textNote = view.findViewById(R.id.textNote);
        recyclerViewProducts = view.findViewById(R.id.recyclerViewProducts);
        textTotalBeforeDiscount = view.findViewById(R.id.textTotalBeforeDiscount);
        textVoucherDiscount = view.findViewById(R.id.textVoucherDiscount);
        textPointDiscount = view.findViewById(R.id.textPointDiscount);
        textShippingFee = view.findViewById(R.id.textShippingFee);
        textTotalAmount = view.findViewById(R.id.textTotalAmount);
        textPaymentMethod = view.findViewById(R.id.textPaymentMethod);
        textPaymentStatus = view.findViewById(R.id.textPaymentStatus);
        textReceivingPoints = view.findViewById(R.id.textReceivingPoints);
        recyclerViewLogs = view.findViewById(R.id.recyclerViewLogs);
        layoutPaymentInfo = view.findViewById(R.id.layoutPaymentInfo);
        layoutOrderLogs = view.findViewById(R.id.layoutOrderLogs);
    }

    private void setupToolbar() {
        toolbar.setTitle("Chi tiết đơn hàng");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void setupRecyclerView() {
        orderDetailAdapter = new OrderDetailAdapter();
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewProducts.setAdapter(orderDetailAdapter);
        recyclerViewProducts.setNestedScrollingEnabled(false);
    }

    private void initRepository() {
        orderDetailRepository = OrderDetailRepository.getInstance();
    }

    private void loadOrderDetail() {
        orderDetailRepository.getOrderDetail(userId, orderId, new OrderDetailRepository.OrderDetailCallback() {
            @Override
            public void onSuccess(OrderDetailResponse orderDetail) {
                if (isAdded()) {
                    hideLoading();
                    displayOrderDetail(orderDetail);
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (isAdded()) {
                    hideLoading();
                    showError(errorMessage);
                }
            }

            @Override
            public void onLoading() {
                if (isAdded()) {
                    showLoading();
                }
            }
        });
    }

    private void displayOrderDetail(OrderDetailResponse orderDetail) {
        // Order basic info
        textOrderId.setText("Mã đơn hàng: " + orderDetail.getId());
        textOrderStatus.setText(orderDetail.getOrderStatusDisplay());
        textOrderDate.setText("Ngày đặt: " + orderDetail.getFormattedCreatedAt());
        
        // Receiver info
        textReceiverName.setText(orderDetail.getReceiverName() != null && !orderDetail.getReceiverName().isEmpty() 
                ? orderDetail.getReceiverName() : "Không có tên người nhận");
        textEmail.setText(orderDetail.getEmail() != null ? orderDetail.getEmail() : "Không có email");
        textPhone.setText(orderDetail.getPhoneNumber() != null && !orderDetail.getPhoneNumber().isEmpty() 
                ? orderDetail.getPhoneNumber() : "Không có số điện thoại");
        textAddress.setText(orderDetail.getAddress() != null ? orderDetail.getAddress() : "Không có địa chỉ");
        
        // Note
        if (orderDetail.getNote() != null && !orderDetail.getNote().isEmpty()) {
            textNote.setText(orderDetail.getNote());
            textNote.setVisibility(View.VISIBLE);
        } else {
            textNote.setVisibility(View.GONE);
        }
        
        // Products
        if (orderDetail.getOrderDetail() != null) {
            orderDetailAdapter.setOrderDetails(orderDetail.getOrderDetail());
        }
        
        // Price breakdown
        textTotalBeforeDiscount.setText(orderDetail.getFormattedTotalPriceBeforeDiscount());
        textVoucherDiscount.setText("-" + orderDetail.getFormattedVoucherDiscount());
        textPointDiscount.setText("-" + orderDetail.getFormattedPointDiscount());
        textShippingFee.setText(orderDetail.getFormattedShippingFee());
        textTotalAmount.setText(orderDetail.getFormattedTotalAmount());
        
        // Payment info
        textPaymentMethod.setText(orderDetail.getPaymentMethodDisplay());
        if (orderDetail.getPaymentData() != null) {
            textPaymentStatus.setText(orderDetail.getPaymentData().getPaymentStatusDisplay());
            layoutPaymentInfo.setVisibility(View.VISIBLE);
        } else {
            layoutPaymentInfo.setVisibility(View.GONE);
        }
        
        // Receiving points
        if (orderDetail.getReceivingPoint() > 0) {
            textReceivingPoints.setText("+" + orderDetail.getFormattedReceivingPoint());
            textReceivingPoints.setVisibility(View.VISIBLE);
        } else {
            textReceivingPoints.setVisibility(View.GONE);
        }
        
        // Order logs
        if (orderDetail.getLogs() != null && !orderDetail.getLogs().isEmpty()) {
            setupOrderLogs(orderDetail.getLogs());
            layoutOrderLogs.setVisibility(View.VISIBLE);
        } else {
            layoutOrderLogs.setVisibility(View.GONE);
        }
    }

    private void setupOrderLogs(java.util.List<OrderLog> logs) {
        // Clear existing views
        recyclerViewLogs.removeAllViews();
        
        // Create a simple vertical layout for logs
        LinearLayout logsContainer = new LinearLayout(getContext());
        logsContainer.setOrientation(LinearLayout.VERTICAL);
        
        for (int i = 0; i < logs.size(); i++) {
            OrderLog log = logs.get(i);
            View logView = LayoutInflater.from(getContext()).inflate(R.layout.item_order_log, logsContainer, false);
            
            TextView textLogStatus = logView.findViewById(R.id.textLogStatus);
            TextView textLogDate = logView.findViewById(R.id.textLogDate);
            View divider = logView.findViewById(R.id.divider);
            
            textLogStatus.setText(log.getStatusDisplay());
            textLogDate.setText(log.getFormattedDateTime());
            
            // Hide divider for last item
            if (i == logs.size() - 1) {
                divider.setVisibility(View.GONE);
            }
            
            logsContainer.addView(logView);
        }
        
        // Add the container to recyclerView (or replace with proper RecyclerView adapter if needed)
        recyclerViewLogs.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewLogs.setNestedScrollingEnabled(false);
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        contentLayout.setVisibility(View.GONE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        contentLayout.setVisibility(View.VISIBLE);
    }

    private void showError(String errorMessage) {
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
    }
}
