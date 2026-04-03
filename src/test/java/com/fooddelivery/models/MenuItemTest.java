package com.fooddelivery.models;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MenuItemTest {

    @Test
    void twoArgConstructorSetsDefaults() {
        MenuItem item = new MenuItem("Pizza", 500.0);

        assertEquals("Pizza", item.getName());
        assertEquals(500.0, item.getPrice(), 0.0001);
        assertEquals("General", item.getCategory());
        assertEquals("", item.getDescription());
        assertTrue(item.isAvailable());
        assertEquals(0, item.getQuantity());
        assertNotNull(item.getAddOns());
        assertNotNull(item.getOptions());
    }

    @Test
    void fourArgConstructorSetsCategoryAndDescription() {
        MenuItem item = new MenuItem("Pasta", "Creamy white sauce", "Main", 420.0);

        assertEquals("Pasta", item.getName());
        assertEquals("Creamy white sauce", item.getDescription());
        assertEquals("Main", item.getCategory());
        assertEquals(420.0, item.getPrice(), 0.0001);
    }

    @Test
    void settersUpdateCollectionsAndFlags() {
        MenuItem item = new MenuItem("Sandwich", 200.0);

        item.setAvailable(false);
        item.setQuantity(7);
        item.setOptions(List.of("No Mayo"));

        assertFalse(item.isAvailable());
        assertEquals(7, item.getQuantity());
        assertEquals(1, item.getOptions().size());
    }
}
