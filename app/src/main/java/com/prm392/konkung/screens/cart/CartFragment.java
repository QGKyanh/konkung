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
import com.prm392.konkung.models.CartResponse;
import com.prm392.konkung.models.UpdateCartItemRequest;
import com.prm392.konkung.network.ApiClient;
import com.prm392.konkung.network.ApiService;
import com.prm392.konkung.network.responses.BaseResponse;
import com.prm392.konkung.screens.checkout.CheckoutFragment;
import com.prm392.konkung.utils.AuthManager;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment implements CartAdapter.OnCartItemClickListener {

    private RecyclerView recyclerViewCart;
    private CartAdapter cartAdapter;
    private TextView textViewTotalPrice;
    private TextView textViewTotalSavings;
    private View textViewEmptyCart;
    private TextView textViewCartCount;
    private Button buttonCheckout;
    private View contentView;

    private ApiService apiService;
    private NumberFormat currencyFormat;
    private List<CartItem> cartItems;
    private String userId;
    private double totalPrice;
    private double totalSavings;
    private int totalQuantity;

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
            setupRecyclerView();
            apiService = ApiClient.getApiService();
            userId = AuthManager.getInstance().getUserId();
            loadCartFromServer();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Lỗi khởi tạo giỏ hàng", Toast.LENGTH_SHORT).show();
        }
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
        view.findViewById(R.id.buttonBack).setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        });
    }

    private void setupRecyclerView() {
        cartAdapter = new CartAdapter(this);
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewCart.setAdapter(cartAdapter);
    }

    private void loadCartFromServer() {
        if (userId == null) return;
        apiService.getCart(userId).enqueue(new Callback<BaseResponse<CartResponse>>() {
            @Override
            public void onResponse(Call<BaseResponse<CartResponse>> call, Response<BaseResponse<CartResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    CartResponse cart = response.body().getData();
                    cartItems = cart.getCartItems() != null ? cart.getCartItems().getItems() : new ArrayList<>();
                    totalPrice = cart.getTotalPriceAfterDiscount();
                    totalSavings = cart.getVoucherDiscount() + cart.getPointDiscount();
                    totalQuantity = cart.getTotalQuantity();
                    updateUI();
                } else {
                    showEmptyCart();
                }
            }
            @Override
            public void onFailure(Call<BaseResponse<CartResponse>> call, Throwable t) {
                showEmptyCart();
            }
        });
    }

    private void updateUI() {
        if (cartItems == null || cartItems.isEmpty()) {
            showEmptyCart();
        } else {
            showCartContent();
            cartAdapter.updateCartItems(cartItems);
            updateTotalPrice();
            updateCartCount();
        }
    }

    private void showEmptyCart() {
        if (textViewEmptyCart != null) textViewEmptyCart.setVisibility(View.VISIBLE);
        if (contentView != null) contentView.setVisibility(View.GONE);
        if (textViewCartCount != null) textViewCartCount.setText("0 sản phẩm");
        if (textViewTotalPrice != null) textViewTotalPrice.setText(currencyFormat.format(0));
        if (buttonCheckout != null) buttonCheckout.setEnabled(false);
    }

    private void showCartContent() {
        if (textViewEmptyCart != null) textViewEmptyCart.setVisibility(View.GONE);
        if (contentView != null) contentView.setVisibility(View.VISIBLE);
    }

    private void updateTotalPrice() {
        if (textViewTotalPrice != null) textViewTotalPrice.setText(currencyFormat.format(totalPrice));
        if (textViewTotalSavings != null) {
            if (totalSavings > 0) {
                textViewTotalSavings.setVisibility(View.VISIBLE);
                textViewTotalSavings.setText("Tiết kiệm: " + currencyFormat.format(totalSavings));
            } else {
                textViewTotalSavings.setVisibility(View.GONE);
            }
        }
        if (buttonCheckout != null) buttonCheckout.setEnabled(totalPrice > 0);
    }

    private void updateCartCount() {
        if (textViewCartCount != null) {
            textViewCartCount.setText(totalQuantity + " sản phẩm");
        }
    }

    private void proceedToCheckout() {
        if (cartItems == null || cartItems.isEmpty()) {
            Toast.makeText(getContext(), "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
            return;
        }
        CheckoutFragment checkoutFragment = new CheckoutFragment();
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, checkoutFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onQuantityChanged(CartItem cartItem, int newQuantity) {
        if (userId == null) return;
        apiService.updateCartItem(userId, cartItem.getProductId(), new UpdateCartItemRequest(newQuantity))
                .enqueue(new Callback<BaseResponse<CartResponse>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<CartResponse>> call, Response<BaseResponse<CartResponse>> response) {
                        loadCartFromServer();
                    }
                    @Override
                    public void onFailure(Call<BaseResponse<CartResponse>> call, Throwable t) {
                        Toast.makeText(getContext(), "Lỗi cập nhật số lượng", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onRemoveItem(CartItem cartItem) {
        if (userId == null) return;
        apiService.deleteCartItem(userId, cartItem.getProductId())
                .enqueue(new Callback<BaseResponse<CartResponse>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<CartResponse>> call, Response<BaseResponse<CartResponse>> response) {
                        loadCartFromServer();
                    }
                    @Override
                    public void onFailure(Call<BaseResponse<CartResponse>> call, Throwable t) {
                        Toast.makeText(getContext(), "Lỗi xóa sản phẩm", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCartFromServer();
    }
}