package com.prm392.konkung.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.prm392.konkung.R;
import com.prm392.konkung.models.OrderDetail;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder> {
    
    private List<OrderDetail> orderDetails = new ArrayList<>();

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails.clear();
        if (orderDetails != null) {
            this.orderDetails.addAll(orderDetails);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_detail_product, parent, false);
        return new OrderDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailViewHolder holder, int position) {
        OrderDetail orderDetail = orderDetails.get(position);
        holder.bind(orderDetail);
    }

    @Override
    public int getItemCount() {
        return orderDetails.size();
    }

    static class OrderDetailViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageProductThumbnail;
        private final TextView textProductName;
        private final TextView textQuantity;
        private final TextView textUnitPrice;
        private final TextView textItemPrice;

        public OrderDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            
            imageProductThumbnail = itemView.findViewById(R.id.imageProductThumbnail);
            textProductName = itemView.findViewById(R.id.textProductName);
            textQuantity = itemView.findViewById(R.id.textQuantity);
            textUnitPrice = itemView.findViewById(R.id.textUnitPrice);
            textItemPrice = itemView.findViewById(R.id.textItemPrice);
        }

        public void bind(OrderDetail orderDetail) {
            // Product name
            textProductName.setText(orderDetail.getProductName());
            
            // Quantity
            textQuantity.setText("x" + orderDetail.getQuantity());
            
            // Unit price
            textUnitPrice.setText(orderDetail.getFormattedUnitPrice());
            
            // Item price (total for this product)
            textItemPrice.setText(orderDetail.getFormattedItemPrice());
            
            // Product thumbnail
            if (orderDetail.getThumbnail() != null && !orderDetail.getThumbnail().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(orderDetail.getThumbnail())
                        .placeholder(R.drawable.ic_product_placeholder)
                        .error(R.drawable.ic_product_placeholder)
                        .centerCrop()
                        .into(imageProductThumbnail);
            } else {
                imageProductThumbnail.setImageResource(R.drawable.ic_product_placeholder);
            }
        }
    }
}
