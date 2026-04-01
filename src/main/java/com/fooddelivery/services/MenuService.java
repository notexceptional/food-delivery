package com.fooddelivery.services;

import com.fooddelivery.models.Coupon;
import com.fooddelivery.models.MenuItem;
import com.fooddelivery.models.Restaurant;
import com.fooddelivery.storage.DataStore;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages menu items for a given restaurant.
 */
public class MenuService {

    private final DataStore store = DataStore.getInstance();

    /** Add a new menu item to the restaurant. */
    public void addItem(Restaurant restaurant, MenuItem item) {
        restaurant.getMenu().add(item);
        store.saveChanges();
    }

    /** Remove a menu item by name. */
    public boolean removeItem(Restaurant restaurant, String itemName) {
        boolean removed = restaurant.getMenu()
                .removeIf(i -> i.getName().equalsIgnoreCase(itemName));
        if (removed) store.saveChanges();
        return removed;
    }

    /** Toggle availability of a menu item. */
    public void toggleAvailability(Restaurant restaurant, String itemName) {
        restaurant.getMenu().stream()
                .filter(i -> i.getName().equalsIgnoreCase(itemName))
                .findFirst()
                .ifPresent(i -> {
                    i.setAvailable(!i.isAvailable());
                    store.saveChanges();
                });
    }

    /** Update stock quantity for a menu item. */
    public void setQuantity(Restaurant restaurant, String itemName, int quantity) {
        restaurant.getMenu().stream()
                .filter(i -> i.getName().equalsIgnoreCase(itemName))
                .findFirst()
                .ifPresent(i -> {
                    i.setQuantity(quantity);
                    store.saveChanges();
                });
    }

    /** Add an add-on option to a menu item. */
    public void addOption(Restaurant restaurant, String itemName, String option) {
        restaurant.getMenu().stream()
                .filter(i -> i.getName().equalsIgnoreCase(itemName))
                .findFirst()
                .ifPresent(i -> {
                    i.getOptions().add(option);
                    store.saveChanges();
                });
    }

    /** Get only available items from a restaurant's menu. */
    public List<MenuItem> getAvailableMenu(Restaurant restaurant) {
        return restaurant.getMenu().stream()
                .filter(MenuItem::isAvailable)
                .collect(Collectors.toList());
    }

    /** Add a coupon to the restaurant. */
    public void addCoupon(Restaurant restaurant, Coupon coupon) {
        restaurant.getCoupons().add(coupon);
        store.addCoupon(coupon); // also keep in global coupon list
        store.saveChanges();
    }
}
