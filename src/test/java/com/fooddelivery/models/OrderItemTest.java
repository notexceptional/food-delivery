package com.fooddelivery.models;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    @Test
    void getSubtotalMultipliesPriceByQuantity() {
        OrderItem item = new OrderItem("Burger", 250.0, 3, List.of("Extra Cheese"));

        assertEquals(750.0, item.getSubtotal(), 0.0001);
    }

    @Test
    void settersUpdateValues() {
        OrderItem item = new OrderItem("Fries", 100.0, 1, List.of());

        item.setMenuItemName("Large Fries");
        item.setUnitPrice(120.0);
        item.setQuantity(2);
        item.setChosenOptions(List.of("No Salt"));

        assertEquals("Large Fries", item.getMenuItemName());
        assertEquals(120.0, item.getUnitPrice(), 0.0001);
        assertEquals(2, item.getQuantity());
        assertEquals(1, item.getChosenOptions().size());
    }

    @Test
    void toStringContainsSubtotal() {
        OrderItem item = new OrderItem("Wrap", 180.0, 2, List.of());

        String text = item.toString();
        assertTrue(text.contains("Wrap"));
        assertTrue(text.contains("360.00"));
    }
}
