package com.prm392.konkung.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.prm392.konkung.models.CartItem;
import com.prm392.konkung.models.Product;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static final String PREFS_NAME = "KonkungPrefs";
    private static final String CART_ITEMS_KEY = "cart_items";
    
    private static CartManager instance;
    private List<CartItem> cartItems;
    private SharedPreferences prefs;
    private Gson gson;

    private CartManager(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
        loadCartItems();
    }

    public static synchronized CartManager getInstance(Context context) {
        if (instance == null) {
            instance = new CartManager(context.getApplicationContext());
        }
        return instance;
    }

    public static CartManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("CartManager not initialized. Call getInstance(Context) first.");
        }
        return instance;
    }

    private void loadCartItems() {
        String cartJson = prefs.getString(CART_ITEMS_KEY, "[]");
        Type listType = new TypeToken<List<CartItem>>(){}.getType();
        cartItems = gson.fromJson(cartJson, listType);
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }
    }

    private void saveCartItems() {
        String cartJson = gson.toJson(cartItems);
        prefs.edit().putString(CART_ITEMS_KEY, cartJson).apply();
    }

    public void addToCart(Product product) {
        addToCart(product, 1);
    }

    public void addToCart(Product product, int quantity) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getId().equals(product.getId())) {
                item.setQuantity(item.getQuantity() + quantity);
                saveCartItems();
                return;
            }
        }
        cartItems.add(new CartItem(product, quantity));
        saveCartItems();
    }

    public void removeFromCart(String productId) {
        cartItems.removeIf(item -> item.getProduct().getId().equals(productId));
        saveCartItems();
    }

    public void updateQuantity(String productId, int quantity) {
        if (quantity <= 0) {
            removeFromCart(productId);
            return;
        }
        
        for (CartItem item : cartItems) {
            if (item.getProduct().getId().equals(productId)) {
                item.setQuantity(quantity);
                saveCartItems();
                return;
            }
        }
    }

    public void clearCart() {
        cartItems.clear();
        saveCartItems();
    }

    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    public int getCartItemCount() {
        int count = 0;
        for (CartItem item : cartItems) {
            count += item.getQuantity();
        }
        return count;
    }

    public double getTotalPrice() {
        double total = 0.0;
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }
        return total;
    }

    public double getTotalSavings() {
        double savings = 0.0;
        for (CartItem item : cartItems) {
            savings += item.getSavings();
        }
        return savings;
    }

    public boolean isInCart(String productId) {
        return cartItems.stream().anyMatch(item -> item.getProduct().getId().equals(productId));
    }

    public int getProductQuantityInCart(String productId) {
        return cartItems.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .mapToInt(CartItem::getQuantity)
                .findFirst()
                .orElse(0);
    }
}
