package com.prm392.konkung.screens.cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prm392.konkung.R;
import com.prm392.konkung.adapters.CartAdapter;
import com.prm392.konkung.models.CartItem;
import com.prm392.konkung.utils.CartManager;
import com.prm392.konkung.screens.checkout.CheckoutFragment;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartFragment extends Fragment implements CartAdapter.OnCartItemClickListener {

    private RecyclerView recyclerViewCart;
    private CartAdapter cartAdapter;
    private TextView textViewTotalPrice;
    private TextView textViewTotalSavings;
    private TextView textViewEmptyCart;
    private TextView textViewCartCount;
    private Button buttonCheckout;
    private View contentView;

    private CartManager cartManager;
    private NumberFormat currencyFormat;
    private List<CartItem> cartItems;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        initCartManager();
        setupRecyclerView();
        loadCartItems();
    }

    private void initViews(View view) {
        recyclerViewCart = view.findViewById(R.id.recyclerViewCart);
        textViewTotalPrice = view.findViewById(R.id.textViewTotalPrice);
        textViewTotalSavings = view.findViewById(R.id.textViewTotalSavings);
        textViewEmptyCart = view.findViewById(R.id.textViewEmptyCart);
        textViewCartCount = view.findViewById(R.id.textViewCartCount);
        buttonCheckout = view.findViewById(R.id.buttonCheckout);
        contentView = view.findViewById(R.id.contentView);

        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        buttonCheckout.setOnClickListener(v -> proceedToCheckout());

        // Setup back button
        view.findViewById(R.id.buttonBack).setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        });
    }

    private void initCartManager() {
        cartManager = CartManager.getInstance(requireContext());
    }

    private void setupRecyclerView() {
        cartAdapter = new CartAdapter(this);
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewCart.setAdapter(cartAdapter);
    }

    private void loadCartItems() {
        cartItems = cartManager.getCartItems();
        updateUI();
    }

    private void updateUI() {
        if (cartItems.isEmpty()) {
            showEmptyCart();
        } else {
            showCartContent();
            cartAdapter.updateCartItems(cartItems);
            updateTotalPrice();
            updateCartCount();
        }
    }

    private void showEmptyCart() {
        textViewEmptyCart.setVisibility(View.VISIBLE);
        contentView.setVisibility(View.GONE);
    }

    private void showCartContent() {
        textViewEmptyCart.setVisibility(View.GONE);
        contentView.setVisibility(View.VISIBLE);
    }

    private void updateTotalPrice() {
        double totalPrice = cartManager.getTotalPrice();
        double totalSavings = cartManager.getTotalSavings();

        textViewTotalPrice.setText(currencyFormat.format(totalPrice));

        if (totalSavings > 0) {
            textViewTotalSavings.setVisibility(View.VISIBLE);
            textViewTotalSavings.setText("Tiết kiệm: " + currencyFormat.format(totalSavings));
        } else {
            textViewTotalSavings.setVisibility(View.GONE);
        }

        buttonCheckout.setEnabled(totalPrice > 0);
    }

    private void updateCartCount() {
        int itemCount = cartManager.getCartItemCount();
        textViewCartCount.setText(itemCount + " sản phẩm");
    }

    private void proceedToCheckout() {
        if (cartItems.isEmpty()) {
            Toast.makeText(getContext(), "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
            return;
        }

        // Navigate to checkout screen
        CheckoutFragment checkoutFragment = new CheckoutFragment();
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, checkoutFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onQuantityChanged(CartItem cartItem, int newQuantity) {
        cartManager.updateQuantity(cartItem.getProduct().getId(), newQuantity);
        loadCartItems();
    }

    @Override
    public void onRemoveItem(CartItem cartItem) {
        cartManager.removeFromCart(cartItem.getProduct().getId());
        loadCartItems();
        Toast.makeText(getContext(), "Đã xóa sản phẩm khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCartItems();
    }
}