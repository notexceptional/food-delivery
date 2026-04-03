package com.fooddelivery.services;

import com.fooddelivery.models.Coupon;
import com.fooddelivery.storage.DataStore;




public class CouponService {

    private final DataStore store = DataStore.getInstance();

    





    public Coupon validateCoupon(String code, String restaurantId) {
        if (code == null || code.isBlank()) return null;
        Coupon coupon = store.findCouponByCode(code);
        if (coupon == null) return null;
        
        if (coupon.getRestaurantId() != null
                && !coupon.getRestaurantId().equals(restaurantId)) {
            return null;
        }
        return coupon;
    }

    





    public double applyDiscount(double originalTotal, Coupon coupon) {
        if (coupon == null) return originalTotal;
        double discount = originalTotal * coupon.getDiscountPercent() / 100.0;
        return Math.max(0, originalTotal - discount);
    }

    
    public String getDiscountSummary(double originalTotal, Coupon coupon) {
        if (coupon == null) return "No coupon applied.";
        double discounted = applyDiscount(originalTotal, coupon);
        double saved = originalTotal - discounted;
        return String.format("Coupon '%s' applied: %.0f%% off — You save ৳%.2f",
                coupon.getCode(), (double) coupon.getDiscountPercent(), saved);
    }

    
    public void createCoupon(String code, int discountPercent, String restaurantId) {
        if (store.findCouponByCode(code) != null) {
            throw new IllegalArgumentException("Coupon code '" + code + "' already exists.");
        }
        Coupon c = new Coupon(code, discountPercent, restaurantId);
        store.addCoupon(c);
    }
}
