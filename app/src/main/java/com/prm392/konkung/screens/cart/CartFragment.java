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
import java.util.ArrayList;
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

        try {
            initViews(view);
            initCartManager();
            setupRecyclerView();
            loadCartItems();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Lỗi khởi tạo giỏ hàng", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews(View view) {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initCartManager() {
        try {
            cartManager = CartManager.getInstance(requireContext());
        } catch (Exception e) {
            e.printStackTrace();
            cartManager = null;
        }
    }

    private void setupRecyclerView() {
        try {
            cartAdapter = new CartAdapter(this);
            recyclerViewCart.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerViewCart.setAdapter(cartAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCartItems() {
        try {
            if (cartManager != null) {
                cartItems = cartManager.getCartItems();
            } else {
                cartItems = new ArrayList<>();
            }
            updateUI();
        } catch (Exception e) {
            e.printStackTrace();
            cartItems = new ArrayList<>();
            updateUI();
        }
    }

    private void updateUI() {
        try {
            if (cartItems == null || cartItems.isEmpty()) {
                showEmptyCart();
            } else {
                showCartContent();
                if (cartAdapter != null) {
                    cartAdapter.updateCartItems(cartItems);
                }
                updateTotalPrice();
                updateCartCount();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showEmptyCart();
        }
    }

    private void showEmptyCart() {
        try {
            if (textViewEmptyCart != null) {
                textViewEmptyCart.setVisibility(View.VISIBLE);
            }
            if (contentView != null) {
                contentView.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showCartContent() {
        try {
            if (textViewEmptyCart != null) {
                textViewEmptyCart.setVisibility(View.GONE);
            }
            if (contentView != null) {
                contentView.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTotalPrice() {
        try {
            if (cartManager != null && textViewTotalPrice != null) {
                double totalPrice = cartManager.getTotalPrice();
                double totalSavings = cartManager.getTotalSavings();

                textViewTotalPrice.setText(currencyFormat.format(totalPrice));

                if (textViewTotalSavings != null) {
                    if (totalSavings > 0) {
                        textViewTotalSavings.setVisibility(View.VISIBLE);
                        textViewTotalSavings.setText("Tiết kiệm: " + currencyFormat.format(totalSavings));
                    } else {
                        textViewTotalSavings.setVisibility(View.GONE);
                    }
                }

                if (buttonCheckout != null) {
                    buttonCheckout.setEnabled(totalPrice > 0);
                }
            } else {
                if (textViewTotalPrice != null) {
                    textViewTotalPrice.setText(currencyFormat.format(0));
                }
                if (textViewTotalSavings != null) {
                    textViewTotalSavings.setVisibility(View.GONE);
                }
                if (buttonCheckout != null) {
                    buttonCheckout.setEnabled(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateCartCount() {
        try {
            if (textViewCartCount != null) {
                if (cartManager != null) {
                    int itemCount = cartManager.getCartItemCount();
                    textViewCartCount.setText(itemCount + " sản phẩm");
                } else {
                    textViewCartCount.setText("0 sản phẩm");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void proceedToCheckout() {
        try {
            if (cartItems == null || cartItems.isEmpty()) {
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
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Lỗi chuyển đến thanh toán", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onQuantityChanged(CartItem cartItem, int newQuantity) {
        try {
            if (cartManager != null) {
                cartManager.updateQuantity(cartItem.getProduct().getId(), newQuantity);
                loadCartItems();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Lỗi cập nhật số lượng", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRemoveItem(CartItem cartItem) {
        try {
            if (cartManager != null) {
                cartManager.removeFromCart(cartItem.getProduct().getId());
                loadCartItems();
                Toast.makeText(getContext(), "Đã xóa sản phẩm khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Lỗi xóa sản phẩm", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            loadCartItems();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}