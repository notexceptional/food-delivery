package com.fooddelivery.services;

import com.fooddelivery.models.Coupon;
import com.fooddelivery.storage.DataStore;

/**
 * Validates and applies discount coupons.
 */
public class CouponService {

    private final DataStore store = DataStore.getInstance();

    /**
     * Validates a coupon code.
     * @param code        the coupon code entered by the customer
     * @param restaurantId the restaurant the order is being placed at
     * @return the Coupon if valid, null otherwise
     */
    public Coupon validateCoupon(String code, String restaurantId) {
        if (code == null || code.isBlank()) return null;
        Coupon coupon = store.findCouponByCode(code);
        if (coupon == null) return null;
        // Check restaurant scope: null means valid globally
        if (coupon.getRestaurantId() != null
                && !coupon.getRestaurantId().equals(restaurantId)) {
            return null;
        }
        return coupon;
    }

    /**
     * Applies a discount to a given total.
     * @param originalTotal the pre-discount total
     * @param coupon        the validated coupon (can be null = no discount)
     * @return discounted total
     */
    public double applyDiscount(double originalTotal, Coupon coupon) {
        if (coupon == null) return originalTotal;
        double discount = originalTotal * coupon.getDiscountPercent() / 100.0;
        return Math.max(0, originalTotal - discount);
    }

    /** Returns a formatted discount summary string. */
    public String getDiscountSummary(double originalTotal, Coupon coupon) {
        if (coupon == null) return "No coupon applied.";
        double discounted = applyDiscount(originalTotal, coupon);
        double saved = originalTotal - discounted;
        return String.format("Coupon '%s' applied: %.0f%% off — You save ৳%.2f",
                coupon.getCode(), (double) coupon.getDiscountPercent(), saved);
    }

    /** Add a new coupon to the global store (from restaurant dashboard). */
    public void createCoupon(String code, int discountPercent, String restaurantId) {
        if (store.findCouponByCode(code) != null) {
            throw new IllegalArgumentException("Coupon code '" + code + "' already exists.");
        }
        Coupon c = new Coupon(code, discountPercent, restaurantId);
        store.addCoupon(c);
    }
}
