package com.prm392.konkung.screens.checkout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.prm392.konkung.R;
import com.prm392.konkung.models.Order;
import com.prm392.konkung.repository.OrderRepository;

import java.text.NumberFormat;
import java.util.Locale;

public class OrderConfirmationFragment extends Fragment {
    
    private static final String ARG_ORDER_ID = "order_id";
    
    private TextView textViewOrderId;
    private TextView textViewOrderStatus;
    private TextView textViewCustomerName;
    private TextView textViewCustomerPhone;
    private TextView textViewDeliveryAddress;
    private TextView textViewPaymentMethod;
    private TextView textViewTotal;
    private Button buttonContinueShopping;
    private Button buttonViewOrders;
    
    private String orderId;
    private OrderRepository orderRepository;
    private NumberFormat currencyFormat;

    public static OrderConfirmationFragment newInstance(String orderId) {
        OrderConfirmationFragment fragment = new OrderConfirmationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ORDER_ID, orderId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            orderId = getArguments().getString(ARG_ORDER_ID);
        }
        
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_confirmation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupListeners();
        loadOrderDetails();
    }

    private void initViews(View view) {
        textViewOrderId = view.findViewById(R.id.textViewOrderId);
        textViewOrderStatus = view.findViewById(R.id.textViewOrderStatus);
        textViewCustomerName = view.findViewById(R.id.textViewCustomerName);
        textViewCustomerPhone = view.findViewById(R.id.textViewCustomerPhone);
        textViewDeliveryAddress = view.findViewById(R.id.textViewDeliveryAddress);
        textViewPaymentMethod = view.findViewById(R.id.textViewPaymentMethod);
        textViewTotal = view.findViewById(R.id.textViewTotal);
        buttonContinueShopping = view.findViewById(R.id.buttonContinueShopping);
        buttonViewOrders = view.findViewById(R.id.buttonViewOrders);
    }

    private void setupListeners() {
        buttonContinueShopping.setOnClickListener(v -> {
            // Navigate back to product list
            getParentFragmentManager().popBackStack();
        });
        
        buttonViewOrders.setOnClickListener(v -> {
            // TODO: Navigate to orders list
            // For now, just show a message
            android.widget.Toast.makeText(getContext(), "Tính năng xem đơn hàng sẽ có sớm!", android.widget.Toast.LENGTH_SHORT).show();
        });
    }

    private void loadOrderDetails() {
        if (orderId == null) {
            showError("Order ID is missing");
            return;
        }
        
        orderRepository = OrderRepository.getInstance();
        orderRepository.getOrder(orderId, new OrderRepository.OrderCallback() {
            @Override
            public void onSuccess(Order order) {
                displayOrderDetails(order);
            }

            @Override
            public void onError(String errorMessage) {
                showError(errorMessage);
            }
        });
    }

    private void displayOrderDetails(Order order) {
        textViewOrderId.setText("Đơn hàng #" + order.getId());
        textViewOrderStatus.setText(order.getStatusDisplayName());
        textViewCustomerName.setText(order.getCustomerName());
        textViewCustomerPhone.setText(order.getCustomerPhone());
        textViewDeliveryAddress.setText(order.getDeliveryAddress());
        textViewPaymentMethod.setText(order.getPaymentMethodDisplayName());
        textViewTotal.setText(currencyFormat.format(order.getTotal()));
        
        // Set status color
        switch (order.getStatus()) {
            case "PENDING":
                textViewOrderStatus.setTextColor(getResources().getColor(R.color.orange, null));
                break;
            case "CONFIRMED":
                textViewOrderStatus.setTextColor(getResources().getColor(R.color.blue, null));
                break;
            case "SHIPPING":
                textViewOrderStatus.setTextColor(getResources().getColor(R.color.primary, null));
                break;
            case "DELIVERED":
                textViewOrderStatus.setTextColor(getResources().getColor(R.color.green, null));
                break;
            case "CANCELLED":
                textViewOrderStatus.setTextColor(getResources().getColor(R.color.red, null));
                break;
        }
    }

    private void showError(String errorMessage) {
        android.widget.Toast.makeText(getContext(), "Lỗi: " + errorMessage, android.widget.Toast.LENGTH_LONG).show();
    }
} 