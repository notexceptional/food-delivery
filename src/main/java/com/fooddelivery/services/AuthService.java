package com.fooddelivery.services;

import com.fooddelivery.models.Customer;
import com.fooddelivery.models.Restaurant;
import com.fooddelivery.storage.DataStore;

import java.util.UUID;

/**
 * Handles customer and restaurant owner registration and login.
 */
public class AuthService {

    private final DataStore store = DataStore.getInstance();

    // ── Customer Auth ──────────────────────────────────────────────────────

    public Customer registerCustomer(String username, String password,
                                     String email, String phone, String address) {
        if (store.findCustomerByUsername(username) != null) {
            throw new IllegalArgumentException("Username '" + username + "' is already taken.");
        }
        String userId = "USR-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Customer c = new Customer(userId, username, password, email, phone, address);
        store.addCustomer(c);
        return c;
    }

    /** Returns the Customer on success, or null if credentials are wrong. */
    public Customer loginCustomer(String username, String password) {
        Customer c = store.findCustomerByUsername(username);
        if (c != null && c.getPassword().equals(password)) {
            return c;
        }
        return null;
    }

    // ── Restaurant Owner Auth ──────────────────────────────────────────────

    public Restaurant registerRestaurant(String name, String location, String area,
                                         String contactInfo, String email,
                                         String openingHours, String cuisine,
                                         String ownerUsername, String ownerPassword) {
        if (store.findRestaurantByOwner(ownerUsername) != null) {
            throw new IllegalArgumentException("Owner '" + ownerUsername + "' already registered.");
        }
        Restaurant r = new Restaurant(name, location, area, contactInfo, email,
                openingHours, cuisine, ownerUsername, ownerPassword);
        store.addRestaurant(r);
        return r;
    }

    /** Returns the Restaurant on success, or null if credentials are wrong. */
    public Restaurant loginRestaurant(String ownerUsername, String ownerPassword) {
        Restaurant r = store.findRestaurantByOwner(ownerUsername);
        if (r != null && r.getOwnerPassword().equals(ownerPassword)) {
            return r;
        }
        return null;
    }
}
