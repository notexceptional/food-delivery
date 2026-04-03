package com.fooddelivery.models;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantTest {

    @Test
    void fiveArgConstructorAppliesCompatibilityDefaults() {
        Restaurant restaurant = new Restaurant("DineHouse", "Banani", "017", "a@b.com", "9-11");

        assertEquals("DineHouse", restaurant.getName());
        assertEquals("Banani", restaurant.getLocation());
        assertEquals("Banani", restaurant.getArea());
        assertEquals("General", restaurant.getCuisine());
        assertTrue(restaurant.isOpen());
        assertEquals(0.0, restaurant.getRating(), 0.0001);
        assertNotNull(restaurant.getMenu());
        assertNotNull(restaurant.getCoupons());
    }

    @Test
    void fullConstructorSetsAllFields() {
        Restaurant restaurant = new Restaurant(
                "Spice Hub", "Gulshan", "Gulshan-1", "018", "spice@hub.com",
                "10-10", "Indian", "owner1", "pw1");

        assertEquals("Spice Hub", restaurant.getName());
        assertEquals("Gulshan", restaurant.getLocation());
        assertEquals("Gulshan-1", restaurant.getArea());
        assertEquals("Indian", restaurant.getCuisine());
        assertEquals("owner1", restaurant.getOwnerUsername());
        assertEquals("pw1", restaurant.getOwnerPassword());
    }

    @Test
    void settersUpdateMutableFields() {
        Restaurant restaurant = new Restaurant("A", "B", "C", "D", "E");

        restaurant.setOpen(false);
        restaurant.setRating(4.5);
        restaurant.setMenu(List.of(new MenuItem("Soup", 90.0)));
        restaurant.setCoupons(List.of(new Coupon("OFF10", 10, null)));

        assertFalse(restaurant.isOpen());
        assertEquals(4.5, restaurant.getRating(), 0.0001);
        assertEquals(1, restaurant.getMenu().size());
        assertEquals(1, restaurant.getCoupons().size());
    }
}
