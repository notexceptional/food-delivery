package com.fooddelivery.services;

import java.util.List;
import java.util.stream.Collectors;

import com.fooddelivery.models.Coupon;
import com.fooddelivery.models.MenuItem;
import com.fooddelivery.models.Restaurant;
import com.fooddelivery.storage.DataStore;

public class MenuService {

    private final DataStore store = DataStore.getInstance();

    public void addItem(Restaurant restaurant, MenuItem item) {
        restaurant.getMenu().add(item);
        store.saveChanges();
    }

    public boolean removeItem(Restaurant restaurant, String itemName) {
        boolean removed = restaurant.getMenu()
                .removeIf(i -> i.getName().equalsIgnoreCase(itemName));
        if (removed) {
            store.saveChanges();
        }
        return removed;
    }

    public void toggleAvailability(Restaurant restaurant, String itemName) {
        restaurant.getMenu().stream()
                .filter(i -> i.getName().equalsIgnoreCase(itemName))
                .findFirst()
                .ifPresent(i -> {
                    i.setAvailable(!i.isAvailable());
                    store.saveChanges();
                });
    }

    public void setQuantity(Restaurant restaurant, String itemName, int quantity) {
        restaurant.getMenu().stream()
                .filter(i -> i.getName().equalsIgnoreCase(itemName))
                .findFirst()
                .ifPresent(i -> {
                    i.setQuantity(quantity);
                    store.saveChanges();
                });
    }

    public void addOption(Restaurant restaurant, String itemName, String option) {
        restaurant.getMenu().stream()
                .filter(i -> i.getName().equalsIgnoreCase(itemName))
                .findFirst()
                .ifPresent(i -> {
                    i.getOptions().add(option);
                    store.saveChanges();
                });
    }

    public List<MenuItem> getAvailableMenu(Restaurant restaurant) {
        return restaurant.getMenu().stream()
                .filter(MenuItem::isAvailable)
                .collect(Collectors.toList());
    }

    public void addCoupon(Restaurant restaurant, Coupon coupon) {
        restaurant.getCoupons().add(coupon);
        store.addCoupon(coupon);
        store.saveChanges();
    }
}
