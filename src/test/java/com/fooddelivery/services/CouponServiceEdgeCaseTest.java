package com.fooddelivery.services;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fooddelivery.models.Coupon;
import com.fooddelivery.storage.DataStore;

class CouponServiceEdgeCaseTest {

    private final DataStore store = DataStore.getInstance();
    private final CouponService couponService = new CouponService();

    @BeforeEach
    void setUp() {
        store.setCustomers(new ArrayList<>());
        store.setRestaurants(new ArrayList<>());
        store.setOrders(new ArrayList<>());

        Coupon global = new Coupon("GLOBAL10", 10, null);
        Coupon local = new Coupon("LOCAL20", 20, "REST-1");
        Coupon inactive = new Coupon("OFF", 50, "REST-1");
        inactive.setActive(false);

        store.setCoupons(new ArrayList<>(List.of(global, local, inactive)));
        store.setRiders(new ArrayList<>());
    }

    @Test
    void validateCouponReturnsNullForNullOrBlankCode() {
        assertNull(couponService.validateCoupon(null, "REST-1"));
        assertNull(couponService.validateCoupon("", "REST-1"));
        assertNull(couponService.validateCoupon("   ", "REST-1"));
    }

    @Test
    void validateCouponRejectsUnknownInactiveAndMismatchedCoupons() {
        assertNull(couponService.validateCoupon("UNKNOWN", "REST-1"));
        assertNull(couponService.validateCoupon("OFF", "REST-1"));
        assertNull(couponService.validateCoupon("LOCAL20", "REST-2"));
    }

    @Test
    void validateCouponAcceptsGlobalAndMatchingRestaurantCoupons() {
        Coupon global = couponService.validateCoupon("GLOBAL10", "ANY-REST");
        Coupon local = couponService.validateCoupon("LOCAL20", "REST-1");

        assertNotNull(global);
        assertNotNull(local);
        assertEquals("GLOBAL10", global.getCode());
        assertEquals("LOCAL20", local.getCode());
    }

    @Test
    void applyDiscountClampsToZeroWhenDiscountExceedsTotal() {
        Coupon huge = new Coupon("HUGE", 300, null);

        double discounted = couponService.applyDiscount(100.0, huge);

        assertEquals(0.0, discounted, 0.0001);
    }

    @Test
    void getDiscountSummaryShowsNoCouponMessageWhenCouponIsNull() {
        String summary = couponService.getDiscountSummary(250.0, null);

        assertTrue(summary.contains("No coupon applied"));
    }
}
