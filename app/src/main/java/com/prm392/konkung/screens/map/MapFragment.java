package com.prm392.konkung.screens.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prm392.konkung.R;
import com.prm392.konkung.adapters.StoreAdapter;
import com.prm392.konkung.models.Store;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapFragment extends Fragment implements LocationListener, StoreAdapter.OnStoreClickListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    
    private MapView mapView;
    private IMapController mapController;
    private MyLocationNewOverlay myLocationOverlay;
    private LocationManager locationManager;
    private List<Store> storeLocations;
    
    // UI Components
    private RecyclerView recyclerViewStores;
    private StoreAdapter storeAdapter;
    private TextView textViewStoreCount;
    private ImageView buttonMyLocation;
    private Location currentUserLocation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Initialize OSMDroid configuration
        Context ctx = requireContext().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupRecyclerView();
        setupMap();
        setupStoreLocations();
        addStoreMarkers();
        enableMyLocation();
        updateStoreList();
    }

    private void initViews(View view) {
        mapView = view.findViewById(R.id.osmMap);
        recyclerViewStores = view.findViewById(R.id.recyclerViewStores);
        textViewStoreCount = view.findViewById(R.id.textViewStoreCount);
        buttonMyLocation = view.findViewById(R.id.buttonMyLocation);
        
        // Initialize location services
        locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        
        // Set up my location button click
        buttonMyLocation.setOnClickListener(v -> {
            if (currentUserLocation != null) {
                GeoPoint userLocation = new GeoPoint(currentUserLocation.getLatitude(), currentUserLocation.getLongitude());
                mapController.animateTo(userLocation);
                mapController.setZoom(15.0);
            } else {
                Toast.makeText(getContext(), "Location not available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView() {
        storeAdapter = new StoreAdapter();
        storeAdapter.setOnStoreClickListener(this);
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerViewStores.setLayoutManager(layoutManager);
        recyclerViewStores.setAdapter(storeAdapter);
    }

    private void setupMap() {
        if (mapView != null) {
            mapView.setTileSource(TileSourceFactory.MAPNIK);
            mapView.setMultiTouchControls(true);
            mapView.setBuiltInZoomControls(true);
            
            mapController = mapView.getController();
            mapController.setZoom(12.0);
            
            // Set default location to Ho Chi Minh City
            GeoPoint startPoint = new GeoPoint(10.7769, 106.7009);
            mapController.setCenter(startPoint);
        }
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) 
                == PackageManager.PERMISSION_GRANTED) {
            
            // Add location overlay
            myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(requireContext()), mapView);
            myLocationOverlay.enableMyLocation();
            mapView.getOverlays().add(myLocationOverlay);
            
            // Request location updates
            requestLocationUpdates();
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) 
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, this);
    }

    private void setupStoreLocations() {
        storeLocations = new ArrayList<>();
        
        // Sample store locations (replace with your actual store data)
        // Ho Chi Minh City stores
        storeLocations.add(new Store("1", "Konkung Milk Store - District 1", 
                "123 Nguyen Hue Street, District 1, Ho Chi Minh City", 
                10.7769, 106.7009, "028-1234-5678", "7:00 - 22:00", 
                "Main store with full product range"));
        
        storeLocations.add(new Store("2", "Konkung Milk Store - District 3", 
                "456 Vo Van Tan Street, District 3, Ho Chi Minh City", 
                10.7860, 106.6917, "028-2345-6789", "7:00 - 22:00",
                "Convenient location near university"));
        
        storeLocations.add(new Store("3", "Konkung Milk Store - Binh Thanh", 
                "789 Xo Viet Nghe Tinh Street, Binh Thanh District, Ho Chi Minh City", 
                10.8014, 106.7108, "028-3456-7890", "6:30 - 23:00",
                "24/7 service available"));
        
        // Hanoi stores
        storeLocations.add(new Store("4", "Konkung Milk Store - Hoan Kiem", 
                "100 Hoan Kiem Lake Street, Hoan Kiem District, Hanoi", 
                21.0285, 105.8542, "024-1234-5678", "7:00 - 22:00",
                "Historic district location"));
        
        storeLocations.add(new Store("5", "Konkung Milk Store - Ba Dinh", 
                "200 Doi Can Street, Ba Dinh District, Hanoi", 
                21.0348, 105.8236, "024-2345-6789", "7:00 - 22:00",
                "Near government offices"));
        
        // Da Nang store
        storeLocations.add(new Store("6", "Konkung Milk Store - Hai Chau", 
                "300 Bach Dang Street, Hai Chau District, Da Nang", 
                16.0678, 108.2208, "0236-123-4567", "7:00 - 22:00",
                "Coastal city branch"));
        
        // Can Tho store
        storeLocations.add(new Store("7", "Konkung Milk Store - Ninh Kieu", 
                "400 Hai Ba Trung Street, Ninh Kieu District, Can Tho", 
                10.0452, 105.7469, "0292-123-4567", "7:00 - 22:00",
                "Mekong Delta region"));
    }

    private void addStoreMarkers() {
        if (mapView == null || storeLocations == null) return;
        
        for (Store store : storeLocations) {
            if (store.isActive()) {
                Marker marker = new Marker(mapView);
                GeoPoint storeLocation = new GeoPoint(store.getLatitude(), store.getLongitude());
                
                marker.setPosition(storeLocation);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                marker.setTitle(store.getName());
                marker.setSubDescription(store.getAddress() + "\n" + 
                                       "Giờ mở cửa: " + store.getOpeningHours() + "\n" +
                                       "SĐT: " + store.getPhone());
                
                // Set marker click listener
                marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker, MapView mapView) {
                        // Show store information
                        String info = store.getName() + "\n" +
                                     store.getAddress() + "\n" +
                                     "Giờ mở cửa: " + store.getOpeningHours() + "\n" +
                                     "SĐT: " + store.getPhone();
                        
                        Toast.makeText(getContext(), info, Toast.LENGTH_LONG).show();
                        return true;
                    }
                });
                
                mapView.getOverlays().add(marker);
            }
        }
        mapView.invalidate();
    }

    private void updateStoreList() {
        if (storeAdapter != null && storeLocations != null) {
            storeAdapter.setStores(storeLocations);
            
            // Update store count
            if (textViewStoreCount != null) {
                String countText = storeLocations.size() + " stores";
                textViewStoreCount.setText(countText);
            }
            
            // Update user location in adapter for distance calculation
            if (currentUserLocation != null) {
                storeAdapter.setUserLocation(currentUserLocation);
            }
        }
    }

    // StoreAdapter.OnStoreClickListener implementation
    @Override
    public void onStoreClick(Store store) {
        // Navigate to the store on the map
        GeoPoint storeLocation = new GeoPoint(store.getLatitude(), store.getLongitude());
        mapController.animateTo(storeLocation);
        mapController.setZoom(16.0);
        
        // Show store info toast
        String info = store.getName() + "\n" + store.getAddress();
        Toast.makeText(getContext(), "Navigating to: " + info, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        currentUserLocation = location;
        
        if (mapController != null) {
            // Only animate to user location on first location update
            if (myLocationOverlay != null && myLocationOverlay.getMyLocation() == null) {
                GeoPoint currentLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                mapController.animateTo(currentLocation);
                mapController.setZoom(13.0);
            }
        }
        
        // Update store list with user location for distance calculation
        if (storeAdapter != null) {
            storeAdapter.setUserLocation(location);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            } else {
                Toast.makeText(getContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mapView != null) {
            mapView.onDetach();
        }
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }
}
