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
import com.prm392.konkung.R;
import com.prm392.konkung.models.Product;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    
    private List<Product> products;
    private OnProductClickListener listener;
    private NumberFormat currencyFormat;

    public interface OnProductClickListener {
        void onProductClick(Product product);
        void onAddToCartClick(Product product);
    }

    public ProductAdapter(List<Product> products, OnProductClickListener listener) {
        this.products = products;
        this.listener = listener;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }

    public void updateProducts(List<Product> newProducts) {
        this.products = newProducts;
        notifyDataSetChanged();
    }

    public void addProducts(List<Product> newProducts) {
        if (this.products == null) {
            this.products = newProducts;
        } else {
            this.products.addAll(newProducts);
        }
        notifyDataSetChanged();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageProduct;
        private TextView textProductName;
        private TextView textProductBrand;
        private TextView textCurrentPrice;
        private TextView textOriginalPrice;
        private TextView textDiscountPercentage;
        private TextView textQuantity;
        private TextView textStatus;
        private View buttonAddToCart;
        private View discountBadge;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProduct = itemView.findViewById(R.id.imageProduct);
            textProductName = itemView.findViewById(R.id.textProductName);
            textProductBrand = itemView.findViewById(R.id.textProductBrand);
            textCurrentPrice = itemView.findViewById(R.id.textCurrentPrice);
            textOriginalPrice = itemView.findViewById(R.id.textOriginalPrice);
            textDiscountPercentage = itemView.findViewById(R.id.textDiscountPercentage);
            textQuantity = itemView.findViewById(R.id.textQuantity);
            textStatus = itemView.findViewById(R.id.textStatus);
            buttonAddToCart = itemView.findViewById(R.id.buttonAddToCart);
            discountBadge = itemView.findViewById(R.id.discountBadge);

            // Set click listeners
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductClick(products.get(getAdapterPosition()));
                }
            });

            buttonAddToCart.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddToCartClick(products.get(getAdapterPosition()));
                }
            });
        }

        public void bind(Product product) {
            // Product name
            textProductName.setText(product.getName());
            
            // Brand
            textProductBrand.setText(product.getBrand());

            // Load product image
            Glide.with(itemView.getContext())
                    .load(product.getThumbnail())
                    .transform(new RoundedCorners(16))
                    .placeholder(R.drawable.ic_milk_logo)
                    .error(R.drawable.ic_milk_logo)
                    .into(imageProduct);

            // Price handling
            if (product.isOnSale()) {
                // Show sale price
                textCurrentPrice.setText(currencyFormat.format(product.getSalePrice()));
                textOriginalPrice.setText(currencyFormat.format(product.getOriginalPrice()));
                textOriginalPrice.setPaintFlags(textOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                textOriginalPrice.setVisibility(View.VISIBLE);
                
                // Show discount percentage
                textDiscountPercentage.setText(String.format("-%d%%", (int) product.getDiscountPercentage()));
                discountBadge.setVisibility(View.VISIBLE);
            } else {
                // Show regular price
                textCurrentPrice.setText(currencyFormat.format(product.getOriginalPrice()));
                textOriginalPrice.setVisibility(View.GONE);
                discountBadge.setVisibility(View.GONE);
            }

            // Quantity
            if (product.getQuantity() > 0) {
                textQuantity.setText("Còn " + product.getQuantity() + " sản phẩm");
                textQuantity.setTextColor(itemView.getContext().getResources().getColor(R.color.green, null));
            } else {
                textQuantity.setText("Hết hàng");
                textQuantity.setTextColor(itemView.getContext().getResources().getColor(R.color.red, null));
            }

            // Status
            switch (product.getStatus()) {
                case "SELLING":
                    textStatus.setText("Đang bán");
                    textStatus.setTextColor(itemView.getContext().getResources().getColor(R.color.green, null));
                    buttonAddToCart.setEnabled(product.getQuantity() > 0);
                    break;
                case "PREORDER":
                    textStatus.setText("Đặt trước");
                    textStatus.setTextColor(itemView.getContext().getResources().getColor(R.color.orange, null));
                    buttonAddToCart.setEnabled(true);
                    break;
                default:
                    textStatus.setText("Không khả dụng");
                    textStatus.setTextColor(itemView.getContext().getResources().getColor(R.color.red, null));
                    buttonAddToCart.setEnabled(false);
                    break;
            }

            // Update add to cart button appearance
            buttonAddToCart.setAlpha(buttonAddToCart.isEnabled() ? 1.0f : 0.5f);
        }
    }
}
