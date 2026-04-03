package com.fooddelivery.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CouponTest {

    @Test
    void constructorSetsDefaultsAndValues() {
        Coupon coupon = new Coupon("SAVE20", 20, "REST-1");

        assertEquals("SAVE20", coupon.getCode());
        assertEquals(20, coupon.getDiscountPercent());
        assertEquals("REST-1", coupon.getRestaurantId());
        assertTrue(coupon.isActive());
    }

    @Test
    void settersUpdateFields() {
        Coupon coupon = new Coupon("A", 10, null);

        coupon.setCode("B");
        coupon.setDiscountPercent(35);
        coupon.setRestaurantId("REST-2");
        coupon.setActive(false);

        assertEquals("B", coupon.getCode());
        assertEquals(35, coupon.getDiscountPercent());
        assertEquals("REST-2", coupon.getRestaurantId());
        assertFalse(coupon.isActive());
    }

    @Test
    void toStringIncludesInactiveMarkerWhenDisabled() {
        Coupon coupon = new Coupon("SPRING", 15, null);
        coupon.setActive(false);

        assertTrue(coupon.toString().contains("[Inactive]"));
    }
}
