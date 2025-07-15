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
import android.widget.ImageView;
import android.content.Intent;
import android.net.Uri;
import android.app.ProgressDialog;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import com.prm392.konkung.R;
import com.prm392.konkung.models.CartItem;
import com.prm392.konkung.models.CartItems;
import com.prm392.konkung.models.Order;
import com.prm392.konkung.repository.OrderRepository;
import com.prm392.konkung.network.ApiClient;
import com.prm392.konkung.network.ApiService;
import com.prm392.konkung.network.responses.BaseResponse;
import com.prm392.konkung.models.CartResponse;
import com.prm392.konkung.utils.AuthManager;
// CartManager import removed - using server APIs instead
import com.prm392.konkung.models.CheckoutRequest;
import com.prm392.konkung.models.CheckoutResponse;
import androidx.appcompat.app.AlertDialog;
import com.prm392.konkung.models.Address;

import java.text.NumberFormat;
import java.util.ArrayList;
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
    private Button buttonPlaceOrder;
    private RecyclerView recyclerViewCheckoutProducts;
    private CheckoutProductAdapter checkoutProductAdapter;
    private WebView webViewPayment;
    private Button buttonBackWebView;

    // Data
    private OrderRepository orderRepository;
    private NumberFormat currencyFormat;
    private List<CartItem> cartItems;
    private double shippingFee = 15000; // 15,000 VND shipping fee
    private ApiService apiService;
    private List<Address> addressList = new ArrayList<>();
    private int selectedAddressId = 1; // default hardcode
    private ProgressDialog progressDialog;

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
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Đang xử lý thanh toán...");
        progressDialog.setCancelable(false);
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
        buttonPlaceOrder = view.findViewById(R.id.buttonPlaceOrder);
        recyclerViewCheckoutProducts = view.findViewById(R.id.recyclerViewCheckoutProducts);
        checkoutProductAdapter = new CheckoutProductAdapter();
        recyclerViewCheckoutProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewCheckoutProducts.setAdapter(checkoutProductAdapter);
        webViewPayment = view.findViewById(R.id.webViewPayment);
        webViewPayment.getSettings().setJavaScriptEnabled(true);
        webViewPayment.setWebViewClient(new WebViewClient());
        buttonBackWebView = view.findViewById(R.id.buttonBackWebView);
        buttonBackWebView.setOnClickListener(v -> handleBackWebView());

        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        // Cho phép nhập địa chỉ tự do, không setFocusable/Clickable/OnClickListener nữa
    }

    private void initData() {
        orderRepository = OrderRepository.getInstance();
        apiService = ApiClient.getApiService();
        loadCartFromServer();
        loadAddresses();
    }

    private void setupListeners() {
        buttonPlaceOrder.setOnClickListener(v -> placeOrder());
    }

    private void loadCartFromServer() {
        String userId = AuthManager.getInstance().getUserId();
        if (userId == null || apiService == null) {
            Toast.makeText(getContext(), "Lỗi: Không thể tải giỏ hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.getCart(userId).enqueue(new retrofit2.Callback<BaseResponse<CartResponse>>() {
            @Override
            public void onResponse(retrofit2.Call<BaseResponse<CartResponse>> call, retrofit2.Response<BaseResponse<CartResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    CartItems cartItemsData = response.body().getData().getCartItems();
                    if (cartItemsData != null) {
                        cartItems = cartItemsData.getItems();
                    } else {
                        cartItems = new ArrayList<>();
                    }
                    updateOrderSummary();
                } else {
                    Toast.makeText(getContext(), "Lỗi tải giỏ hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<BaseResponse<CartResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAddresses() {
        String userId = AuthManager.getInstance().getUserId();
        apiService.getUserAddresses(userId).enqueue(new retrofit2.Callback<BaseResponse<List<Address>>>() {
            @Override
            public void onResponse(retrofit2.Call<BaseResponse<List<Address>>> call, retrofit2.Response<BaseResponse<List<Address>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null && !response.body().getData().isEmpty()) {
                    addressList = response.body().getData();
                    Address defaultAddress = addressList.get(0); // hoặc tìm address có isDefault = true nếu muốn
                    selectedAddressId = defaultAddress.getId();
                    String addressStr = defaultAddress.getAddress() + ", " +
                        defaultAddress.getWardName() + ", " +
                        defaultAddress.getDistrictName() + ", " +
                        defaultAddress.getProvinceName();
                    editTextAddress.setText(addressStr);
                    showAddressSelectionDialog();
                } else {
                    selectedAddressId = 1;
                    editTextAddress.setText("");
                }
            }
            @Override
            public void onFailure(retrofit2.Call<BaseResponse<List<Address>>> call, Throwable t) {
                selectedAddressId = 1;
                editTextAddress.setText("");
            }
        });
    }

    private void showAddressSelectionDialog() {
        if (addressList == null || addressList.isEmpty()) return;
        String[] addressStrings = new String[addressList.size()];
        for (int i = 0; i < addressList.size(); i++) {
            Address addr = addressList.get(i);
            addressStrings[i] = addr.getReceiverName() + " - " + addr.getAddress() + ", " + addr.getWardName() + ", " + addr.getDistrictName() + ", " + addr.getProvinceName();
        }
        new AlertDialog.Builder(requireContext())
            .setTitle("Chọn địa chỉ giao hàng")
            .setSingleChoiceItems(addressStrings, 0, (dialog, which) -> {
                selectedAddressId = addressList.get(which).getId();
                Address chosen = addressList.get(which);
                String addressStr = chosen.getAddress() + ", " +
                        chosen.getWardName() + ", " +
                        chosen.getDistrictName() + ", " +
                        chosen.getProvinceName();
                editTextAddress.setText(addressStr);
            })
            .setPositiveButton("OK", null)
            .show();
    }

    private void updateOrderSummary() {
        if (cartItems == null || cartItems.isEmpty()) {
            checkoutProductAdapter.updateProducts(new ArrayList<>());
            textViewSubtotal.setText(currencyFormat.format(0));
            textViewShippingFee.setText(currencyFormat.format(shippingFee));
            textViewTotal.setText(currencyFormat.format(shippingFee));
            textViewSavings.setVisibility(View.GONE);
            return;
        }
        checkoutProductAdapter.updateProducts(cartItems);

        double subtotal = 0;
        double savings = 0;

        for (CartItem item : cartItems) {
            subtotal += item.getTotalPrice();
            // Calculate savings (original price - sale price) * quantity
            if (item.getSalePrice() > 0 && item.getSalePrice() < item.getOriginalPrice()) {
                savings += (item.getOriginalPrice() - item.getSalePrice()) * item.getQuantity();
            }
        }

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

    private void clearCartFromServer() {
        String userId = AuthManager.getInstance().getUserId();
        if (userId == null || apiService == null) return;

        apiService.deleteAllCart(userId).enqueue(new retrofit2.Callback<BaseResponse<Void>>() {
            @Override
            public void onResponse(retrofit2.Call<BaseResponse<Void>> call, retrofit2.Response<BaseResponse<Void>> response) {
                // Cart cleared successfully
            }

            @Override
            public void onFailure(retrofit2.Call<BaseResponse<Void>> call, Throwable t) {
                // Handle error silently
            }
        });
    }

    private void showPaymentWebView(String url) {
        webViewPayment.setVisibility(View.VISIBLE);
        buttonBackWebView.setVisibility(View.VISIBLE);
        // Ẩn nút đặt hàng, giữ nguyên các layout khác
        buttonPlaceOrder.setVisibility(View.GONE);
        // Chuyển các trường thông tin sang chỉ đọc
        editTextFullName.setEnabled(false);
        editTextPhone.setEnabled(false);
        editTextAddress.setEnabled(false);
        editTextNote.setEnabled(false);
        recyclerViewCheckoutProducts.setEnabled(false);
        webViewPayment.loadUrl(url);
    }

    private void hidePaymentWebView() {
        webViewPayment.setVisibility(View.GONE);
        buttonBackWebView.setVisibility(View.GONE);
        buttonPlaceOrder.setVisibility(View.VISIBLE);
        // Cho phép chỉnh sửa lại các trường thông tin
        editTextFullName.setEnabled(true);
        editTextPhone.setEnabled(true);
        editTextAddress.setEnabled(true);
        editTextNote.setEnabled(true);
        recyclerViewCheckoutProducts.setEnabled(true);
    }

    private void handleBackWebView() {
        // Khi bấm Quay lại, chuyển về Home và cập nhật badge cart
        // 1. Đóng WebView
        hidePaymentWebView();
        // 2. Chuyển về Home bằng cách chọn tab nav_home trên BottomNavigationView
        if (getActivity() instanceof com.prm392.konkung.screens.main.MainActivity) {
            com.prm392.konkung.screens.main.MainActivity mainActivity = (com.prm392.konkung.screens.main.MainActivity) getActivity();
            android.view.View bottomNav = mainActivity.findViewById(R.id.bottom_navigation);
            if (bottomNav instanceof com.google.android.material.bottomnavigation.BottomNavigationView) {
                ((com.google.android.material.bottomnavigation.BottomNavigationView) bottomNav).setSelectedItemId(R.id.nav_home);
            }
            // 3. Gọi lại cập nhật badge cart
            com.prm392.konkung.screens.main.MainActivity.updateCartBadgeFromFragment(getActivity());
        }
    }

    private void placeOrder() {
        if (!validateInput()) {
            return;
        }
        String address = editTextAddress.getText().toString().trim();
        String note = editTextNote.getText().toString().trim();
        String paymentMethod = "PAYOS";

        CheckoutRequest request = new CheckoutRequest(address, note, paymentMethod);

        buttonPlaceOrder.setEnabled(false);
        buttonPlaceOrder.setText("Đang xử lý...");
        progressDialog.show();

        apiService.checkout(request).enqueue(new retrofit2.Callback<BaseResponse<CheckoutResponse>>() {
            @Override
            public void onResponse(retrofit2.Call<BaseResponse<CheckoutResponse>> call, retrofit2.Response<BaseResponse<CheckoutResponse>> response) {
                buttonPlaceOrder.setEnabled(true);
                buttonPlaceOrder.setText("Đặt hàng");
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    String checkoutUrl = response.body().getData().getCheckoutUrl();
                    if (checkoutUrl != null && !checkoutUrl.isEmpty()) {
                        showPaymentWebView(checkoutUrl);
                    } else {
                        Toast.makeText(getContext(), "Không nhận được link thanh toán", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Lỗi khi tạo đơn hàng", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(retrofit2.Call<BaseResponse<CheckoutResponse>> call, Throwable t) {
                buttonPlaceOrder.setEnabled(true);
                buttonPlaceOrder.setText("Đặt hàng");
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
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

// Adapter cho danh sách sản phẩm trong checkout
class CheckoutProductAdapter extends RecyclerView.Adapter<CheckoutProductAdapter.ViewHolder> {
    private List<CartItem> products = new ArrayList<>();
    public void updateProducts(List<CartItem> newProducts) {
        this.products = newProducts != null ? newProducts : new ArrayList<>();
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checkout_product, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = products.get(position);
        holder.textProductName.setText(item.getProductName());
        holder.textProductQuantity.setText("x" + item.getQuantity());
        holder.textProductPrice.setText(NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(item.getSalePrice() > 0 ? item.getSalePrice() : item.getOriginalPrice()));
        Glide.with(holder.imageProduct.getContext())
                .load(item.getThumbnail())
                .placeholder(R.drawable.ic_milk_logo)
                .error(R.drawable.ic_milk_logo)
                .into(holder.imageProduct);
    }
    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProduct;
        TextView textProductName, textProductQuantity, textProductPrice;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProduct = itemView.findViewById(R.id.imageProduct);
            textProductName = itemView.findViewById(R.id.textProductName);
            textProductQuantity = itemView.findViewById(R.id.textProductQuantity);
            textProductPrice = itemView.findViewById(R.id.textProductPrice);
        }
    }
}