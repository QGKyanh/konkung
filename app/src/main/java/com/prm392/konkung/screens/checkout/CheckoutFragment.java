package com.prm392.konkung.screens.checkout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.prm392.konkung.R;
import com.prm392.konkung.models.CartItem;
import com.prm392.konkung.models.Order;
import com.prm392.konkung.repository.OrderRepository;
import com.prm392.konkung.utils.CartManager;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CheckoutFragment extends Fragment {

    // UI Elements
    private TextView textViewSubtotal;
    private TextView textViewShippingFee;
    private TextView textViewTotal;
    private TextView textViewSavings;
    private EditText editTextFullName;
    private EditText editTextPhone;
    private EditText editTextAddress;
    private EditText editTextNote;
    private RadioGroup radioGroupPaymentMethod;
    private RadioButton radioButtonCash;
    private RadioButton radioButtonBank;
    private Button buttonPlaceOrder;

    // Data
    private CartManager cartManager;
    private OrderRepository orderRepository;
    private NumberFormat currencyFormat;
    private List<CartItem> cartItems;
    private double shippingFee = 15000; // 15,000 VND shipping fee

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_checkout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        initData();
        setupListeners();
        updateOrderSummary();
    }

    private void initViews(View view) {
        textViewSubtotal = view.findViewById(R.id.textViewSubtotal);
        textViewShippingFee = view.findViewById(R.id.textViewShippingFee);
        textViewTotal = view.findViewById(R.id.textViewTotal);
        textViewSavings = view.findViewById(R.id.textViewSavings);
        editTextFullName = view.findViewById(R.id.editTextFullName);
        editTextPhone = view.findViewById(R.id.editTextPhone);
        editTextAddress = view.findViewById(R.id.editTextAddress);
        editTextNote = view.findViewById(R.id.editTextNote);
        radioGroupPaymentMethod = view.findViewById(R.id.radioGroupPaymentMethod);
        radioButtonCash = view.findViewById(R.id.radioButtonCash);
        radioButtonBank = view.findViewById(R.id.radioButtonBank);
        buttonPlaceOrder = view.findViewById(R.id.buttonPlaceOrder);

        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    }

    private void initData() {
        cartManager = CartManager.getInstance(requireContext());
        orderRepository = OrderRepository.getInstance();
        cartItems = cartManager.getCartItems();
    }

    private void setupListeners() {
        buttonPlaceOrder.setOnClickListener(v -> placeOrder());

        // Set default payment method
        radioButtonCash.setChecked(true);
    }

    private void updateOrderSummary() {
        double subtotal = cartManager.getTotalPrice();
        double savings = cartManager.getTotalSavings();
        double total = subtotal + shippingFee;

        textViewSubtotal.setText(currencyFormat.format(subtotal));
        textViewShippingFee.setText(currencyFormat.format(shippingFee));
        textViewTotal.setText(currencyFormat.format(total));

        if (savings > 0) {
            textViewSavings.setVisibility(View.VISIBLE);
            textViewSavings.setText("Tiết kiệm: " + currencyFormat.format(savings));
        } else {
            textViewSavings.setVisibility(View.GONE);
        }
    }

    private void placeOrder() {
        // Validate input
        if (!validateInput()) {
            return;
        }

        // Get form data
        String fullName = editTextFullName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String note = editTextNote.getText().toString().trim();
        String paymentMethod = radioButtonCash.isChecked() ? "CASH" : "BANK_TRANSFER";

        // Create order
        Order order = new Order();
        order.setCustomerName(fullName);
        order.setCustomerPhone(phone);
        order.setDeliveryAddress(address);
        order.setNote(note);
        order.setPaymentMethod(paymentMethod);
        order.setSubtotal(cartManager.getTotalPrice());
        order.setShippingFee(shippingFee);
        order.setTotal(order.getSubtotal() + order.getShippingFee());
        order.setSavings(cartManager.getTotalSavings());
        order.setItems(cartItems);

        // Show loading
        buttonPlaceOrder.setEnabled(false);
        buttonPlaceOrder.setText("Đang xử lý...");

        // Submit order
        orderRepository.createOrder(order, new OrderRepository.OrderCallback() {
            @Override
            public void onSuccess(Order createdOrder) {
                // Clear cart after successful order
                cartManager.clearCart();

                // Show success message
                Toast.makeText(getContext(), "Đặt hàng thành công!", Toast.LENGTH_LONG).show();

                // Navigate to order confirmation
                OrderConfirmationFragment confirmationFragment = OrderConfirmationFragment
                        .newInstance(createdOrder.getId());
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, confirmationFragment)
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onError(String errorMessage) {
                // Re-enable button
                buttonPlaceOrder.setEnabled(true);
                buttonPlaceOrder.setText("Đặt hàng");

                // Show error message
                Toast.makeText(getContext(), "Lỗi: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validateInput() {
        String fullName = editTextFullName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();

        if (fullName.isEmpty()) {
            editTextFullName.setError("Vui lòng nhập họ tên");
            return false;
        }

        if (phone.isEmpty()) {
            editTextPhone.setError("Vui lòng nhập số điện thoại");
            return false;
        }

        if (phone.length() < 10) {
            editTextPhone.setError("Số điện thoại không hợp lệ");
            return false;
        }

        if (address.isEmpty()) {
            editTextAddress.setError("Vui lòng nhập địa chỉ giao hàng");
            return false;
        }

        if (cartItems.isEmpty()) {
            Toast.makeText(getContext(), "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}