package com.prm392.konkung.adapters;

import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prm392.konkung.R;
import com.prm392.konkung.models.Store;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.StoreViewHolder> {

    private List<Store> stores = new ArrayList<>();
    private Location userLocation;
    private OnStoreClickListener listener;

    public interface OnStoreClickListener {
        void onStoreClick(Store store);
    }

    public void setStores(List<Store> stores) {
        this.stores = stores;
        notifyDataSetChanged();
    }

    public void setUserLocation(Location userLocation) {
        this.userLocation = userLocation;
        notifyDataSetChanged();
    }

    public void setOnStoreClickListener(OnStoreClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_store, parent, false);
        return new StoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreViewHolder holder, int position) {
        Store store = stores.get(position);
        holder.bind(store);
    }

    @Override
    public int getItemCount() {
        return stores.size();
    }

    class StoreViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewStoreName;
        private TextView textViewStoreAddress;
        private TextView textViewStoreHours;
        private TextView textViewStoreDistance;

        public StoreViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewStoreName = itemView.findViewById(R.id.textViewStoreName);
            textViewStoreAddress = itemView.findViewById(R.id.textViewStoreAddress);
            textViewStoreHours = itemView.findViewById(R.id.textViewStoreHours);
            textViewStoreDistance = itemView.findViewById(R.id.textViewStoreDistance);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onStoreClick(stores.get(getAdapterPosition()));
                }
            });
        }

        public void bind(Store store) {
            textViewStoreName.setText(store.getName());
            textViewStoreAddress.setText(store.getAddress());
            textViewStoreHours.setText(store.getOpeningHours());

            // Calculate and display distance if user location is available
            if (userLocation != null) {
                float[] results = new float[1];
                Location.distanceBetween(
                        userLocation.getLatitude(), userLocation.getLongitude(),
                        store.getLatitude(), store.getLongitude(),
                        results
                );

                float distanceKm = results[0] / 1000;
                String distanceText;
                if (distanceKm < 1) {
                    distanceText = String.format(Locale.getDefault(), "• %.0f m", results[0]);
                } else {
                    distanceText = String.format(Locale.getDefault(), "• %.1f km", distanceKm);
                }
                textViewStoreDistance.setText(distanceText);
                textViewStoreDistance.setVisibility(View.VISIBLE);
            } else {
                textViewStoreDistance.setVisibility(View.GONE);
            }
        }
    }
}
