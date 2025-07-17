package com.prm392.konkung.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.prm392.konkung.R;
import com.prm392.konkung.models.OrderHistory;
import com.prm392.konkung.models.OrderProduct;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {
    
    private List<OrderHistory> orders;
    private OnOrderClickListener onOrderClickListener;
    private NumberFormat currencyFormat;

    public interface OnOrderClickListener {
        void onOrderClick(OrderHistory order);
        void onReorderClick(OrderHistory order);
    }

    public OrderHistoryAdapter() {
        this.orders = new ArrayList<>();
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    }

    public void setOnOrderClickListener(OnOrderClickListener listener) {
        this.onOrderClickListener = listener;
    }

    public void setOrders(List<OrderHistory> orders) {
        this.orders = orders != null ? orders : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addOrders(List<OrderHistory> newOrders) {
        if (newOrders != null && !newOrders.isEmpty()) {
            int startPosition = this.orders.size();
            this.orders.addAll(newOrders);
            notifyItemRangeInserted(startPosition, newOrders.size());
        }
    }

    public void clearOrders() {
        this.orders.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_history, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderHistory order = orders.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewOrderId;
        private TextView textViewOrderDate;
        private TextView textViewOrderStatus;
        private TextView textViewTotalAmount;
        private TextView textViewPaymentMethod;
        private TextView textViewProductCount;
        private TextView textViewPreorderBadge;
        private LinearLayout productImagesContainer;
        private View statusIndicator;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            
            textViewOrderId = itemView.findViewById(R.id.textViewOrderId);
            textViewOrderDate = itemView.findViewById(R.id.textViewOrderDate);
            textViewOrderStatus = itemView.findViewById(R.id.textViewOrderStatus);
            textViewTotalAmount = itemView.findViewById(R.id.textViewTotalAmount);
            textViewPaymentMethod = itemView.findViewById(R.id.textViewPaymentMethod);
            textViewProductCount = itemView.findViewById(R.id.textViewProductCount);
            textViewPreorderBadge = itemView.findViewById(R.id.textViewPreorderBadge);
            productImagesContainer = itemView.findViewById(R.id.productImagesContainer);
            statusIndicator = itemView.findViewById(R.id.statusIndicator);

            itemView.setOnClickListener(v -> {
                if (onOrderClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onOrderClickListener.onOrderClick(orders.get(position));
                    }
                }
            });
        }

        public void bind(OrderHistory order) {
            // Order ID (show last 8 characters)
            String orderId = order.getId();
            if (orderId.length() > 8) {
                orderId = "..." + orderId.substring(orderId.length() - 8);
            }
            textViewOrderId.setText("Đơn hàng " + orderId);
            
            // Order date
            textViewOrderDate.setText(order.getFormattedCreatedDate());
            
            // Order status
            textViewOrderStatus.setText(order.getStatusDisplayText());
            textViewOrderStatus.setTextColor(itemView.getContext().getResources().getColor(order.getStatusColor(), null));
            statusIndicator.setBackgroundColor(itemView.getContext().getResources().getColor(order.getStatusColor(), null));
            
            // Total amount
            textViewTotalAmount.setText(currencyFormat.format(order.getTotalAmount()));
            
            // Payment method
            textViewPaymentMethod.setText(order.getPaymentMethodDisplayText());
            
            // Product count
            int productCount = order.getProductCount();
            textViewProductCount.setText(productCount + " sản phẩm");
            
            // Preorder badge
            if (order.isPreorder()) {
                textViewPreorderBadge.setVisibility(View.VISIBLE);
            } else {
                textViewPreorderBadge.setVisibility(View.GONE);
            }
            
            // Product images
            displayProductImages(order.getProductList());
        }

        private void displayProductImages(List<OrderProduct> productList) {
            productImagesContainer.removeAllViews();
            
            if (productList == null || productList.isEmpty()) {
                return;
            }

            // Show maximum 3 product images
            int maxImages = Math.min(3, productList.size());
            
            for (int i = 0; i < maxImages; i++) {
                OrderProduct product = productList.get(i);
                
                ImageView imageView = new ImageView(itemView.getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(80, 80);
                params.setMargins(0, 0, 8, 0);
                imageView.setLayoutParams(params);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setBackground(itemView.getContext().getDrawable(R.drawable.item_background));
                
                Glide.with(itemView.getContext())
                        .load(product.getThumbnail())
                        .placeholder(R.drawable.ic_milk_logo)
                        .error(R.drawable.ic_milk_logo)
                        .centerCrop()
                        .into(imageView);
                
                productImagesContainer.addView(imageView);
            }
            
            // Show more indicator if there are more products
            if (productList.size() > 3) {
                TextView moreIndicator = new TextView(itemView.getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(80, 80);
                moreIndicator.setLayoutParams(params);
                moreIndicator.setText("+" + (productList.size() - 3));
                moreIndicator.setTextSize(12);
                moreIndicator.setTextColor(itemView.getContext().getResources().getColor(R.color.text_secondary, null));
                moreIndicator.setBackground(itemView.getContext().getDrawable(R.drawable.more_indicator_background));
                moreIndicator.setGravity(android.view.Gravity.CENTER);
                
                productImagesContainer.addView(moreIndicator);
            }
        }
    }
}
