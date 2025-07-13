package com.prm392.konkung.adapters;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.material.button.MaterialButton;
import com.prm392.konkung.R;
import com.prm392.konkung.models.CartItem;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems;
    private OnCartItemClickListener listener;
    private NumberFormat currencyFormat;

    public interface OnCartItemClickListener {
        void onQuantityChanged(CartItem cartItem, int newQuantity);
        void onRemoveItem(CartItem cartItem);
    }

    public CartAdapter(OnCartItemClickListener listener) {
        this.cartItems = new ArrayList<>();
        this.listener = listener;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.bind(cartItem);
    }

    @Override
    public int getItemCount() {
        return cartItems != null ? cartItems.size() : 0;
    }

    public void updateCartItems(List<CartItem> newCartItems) {
        this.cartItems = new ArrayList<>(newCartItems);
        notifyDataSetChanged();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageProduct;
        private TextView textProductName;
        private TextView textCurrentPrice;
        private TextView textOriginalPrice;
        private TextView textQuantity;
        private TextView textTotalPrice;
        private TextView textDiscountPercentage;
        private MaterialButton buttonMinus;
        private MaterialButton buttonPlus;
        private ImageView buttonRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProduct = itemView.findViewById(R.id.imageProduct);
            textProductName = itemView.findViewById(R.id.textProductName);
            textCurrentPrice = itemView.findViewById(R.id.textCurrentPrice);
            textOriginalPrice = itemView.findViewById(R.id.textOriginalPrice);
            textQuantity = itemView.findViewById(R.id.textQuantity);
            textTotalPrice = itemView.findViewById(R.id.textTotalPrice);
            textDiscountPercentage = itemView.findViewById(R.id.textDiscountPercentage);
            buttonMinus = itemView.findViewById(R.id.buttonMinus);
            buttonPlus = itemView.findViewById(R.id.buttonPlus);
            buttonRemove = itemView.findViewById(R.id.buttonRemove);
        }

        public void bind(CartItem cartItem) {
            try {
                // Product image
                if (imageProduct != null) {
                    Glide.with(itemView.getContext())
                            .load(cartItem.getThumbnail())
                            .transform(new RoundedCorners(12))
                            .placeholder(R.drawable.ic_milk_logo)
                            .error(R.drawable.ic_milk_logo)
                            .into(imageProduct);
                }

                // Product info
                if (textProductName != null) {
                    textProductName.setText(cartItem.getProductName());
                }

                // Price handling
                boolean onSale = cartItem.getSalePrice() > 0 && cartItem.getSalePrice() < cartItem.getOriginalPrice();
                if (onSale) {
                    if (textCurrentPrice != null) {
                        textCurrentPrice.setText(currencyFormat.format(cartItem.getSalePrice()));
                    }
                    if (textOriginalPrice != null) {
                        textOriginalPrice.setText(currencyFormat.format(cartItem.getOriginalPrice()));
                        textOriginalPrice.setPaintFlags(textOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        textOriginalPrice.setVisibility(View.VISIBLE);
                    }
                    if (textDiscountPercentage != null) {
                        int percent = (int) ((cartItem.getOriginalPrice() - cartItem.getSalePrice()) / cartItem.getOriginalPrice() * 100);
                        textDiscountPercentage.setText("-" + percent + "%");
                        textDiscountPercentage.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (textCurrentPrice != null) {
                        textCurrentPrice.setText(currencyFormat.format(cartItem.getOriginalPrice()));
                    }
                    if (textOriginalPrice != null) {
                        textOriginalPrice.setVisibility(View.GONE);
                    }
                    if (textDiscountPercentage != null) {
                        textDiscountPercentage.setVisibility(View.GONE);
                    }
                }

                // Quantity
                if (textQuantity != null) {
                    textQuantity.setText(String.valueOf(cartItem.getQuantity()));
                }

                // Total price for this item
                if (textTotalPrice != null) {
                    textTotalPrice.setText(currencyFormat.format(cartItem.getTotalPrice()));
                }

                // Quantity buttons
                if (buttonMinus != null) {
                    buttonMinus.setOnClickListener(v -> {
                        int newQuantity = cartItem.getQuantity() - 1;
                        if (newQuantity >= 1) {
                            if (listener != null) {
                                listener.onQuantityChanged(cartItem, newQuantity);
                            }
                        }
                    });
                    buttonMinus.setEnabled(cartItem.getQuantity() > 1);
                }

                if (buttonPlus != null) {
                    buttonPlus.setOnClickListener(v -> {
                        int newQuantity = cartItem.getQuantity() + 1;
                        if (listener != null) {
                            listener.onQuantityChanged(cartItem, newQuantity);
                        }
                    });
                }

                // Remove button
                if (buttonRemove != null) {
                    buttonRemove.setOnClickListener(v -> {
                        if (listener != null) {
                            listener.onRemoveItem(cartItem);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}