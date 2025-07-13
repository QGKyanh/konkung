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
        try {
            this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            this.gson = new Gson();
            loadCartItems();
        } catch (Exception e) {
            e.printStackTrace();
            this.cartItems = new ArrayList<>();
        }
    }

    public static synchronized CartManager getInstance(Context context) {
        try {
            if (instance == null) {
                instance = new CartManager(context.getApplicationContext());
            }
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static CartManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("CartManager not initialized. Call getInstance(Context) first.");
        }
        return instance;
    }

    private void loadCartItems() {
        try {
            String cartJson = prefs.getString(CART_ITEMS_KEY, "[]");
            Type listType = new TypeToken<List<CartItem>>(){}.getType();
            cartItems = gson.fromJson(cartJson, listType);
            if (cartItems == null) {
                cartItems = new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
            cartItems = new ArrayList<>();
        }
    }

    private void saveCartItems() {
        try {
            String cartJson = gson.toJson(cartItems);
            prefs.edit().putString(CART_ITEMS_KEY, cartJson).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addToCart(Product product) {
        addToCart(product, 1);
    }

    public void addToCart(Product product, int quantity) {
        try {
            if (product == null) return;
            
            for (CartItem item : cartItems) {
                if (item.getProduct() != null && item.getProduct().getId() != null && 
                    item.getProduct().getId().equals(product.getId())) {
                    item.setQuantity(item.getQuantity() + quantity);
                    saveCartItems();
                    return;
                }
            }
            cartItems.add(new CartItem(product, quantity));
            saveCartItems();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeFromCart(String productId) {
        try {
            if (productId == null) return;
            
            List<CartItem> itemsToRemove = new ArrayList<>();
            for (CartItem item : cartItems) {
                if (item.getProduct() != null && item.getProduct().getId() != null && 
                    item.getProduct().getId().equals(productId)) {
                    itemsToRemove.add(item);
                }
            }
            cartItems.removeAll(itemsToRemove);
            saveCartItems();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateQuantity(String productId, int quantity) {
        try {
            if (productId == null) return;
            
            if (quantity <= 0) {
                removeFromCart(productId);
                return;
            }
            
            for (CartItem item : cartItems) {
                if (item.getProduct() != null && item.getProduct().getId() != null && 
                    item.getProduct().getId().equals(productId)) {
                    item.setQuantity(quantity);
                    saveCartItems();
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearCart() {
        try {
            cartItems.clear();
            saveCartItems();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<CartItem> getCartItems() {
        try {
            return new ArrayList<>(cartItems);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public int getCartItemCount() {
        try {
            int count = 0;
            for (CartItem item : cartItems) {
                count += item.getQuantity();
            }
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public double getTotalPrice() {
        try {
            double total = 0.0;
            for (CartItem item : cartItems) {
                total += item.getTotalPrice();
            }
            return total;
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    public double getTotalSavings() {
        try {
            double savings = 0.0;
            for (CartItem item : cartItems) {
                savings += item.getSavings();
            }
            return savings;
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    public boolean isInCart(String productId) {
        try {
            if (productId == null) return false;
            
            for (CartItem item : cartItems) {
                if (item.getProduct() != null && item.getProduct().getId() != null && 
                    item.getProduct().getId().equals(productId)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getProductQuantityInCart(String productId) {
        try {
            if (productId == null) return 0;
            
            for (CartItem item : cartItems) {
                if (item.getProduct() != null && item.getProduct().getId() != null && 
                    item.getProduct().getId().equals(productId)) {
                    return item.getQuantity();
                }
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
