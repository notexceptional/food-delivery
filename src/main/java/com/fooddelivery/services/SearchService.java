package com.fooddelivery.services;

import com.fooddelivery.models.MenuItem;
import com.fooddelivery.models.Restaurant;
import com.fooddelivery.storage.DataStore;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides advanced search and sort features for restaurants and menus.
 */
public class SearchService {

    private final DataStore store = DataStore.getInstance();

    /**
     * Search restaurants by name, area, or cuisine.
     * Results are sorted by rating descending.
     */
    public List<Restaurant> searchRestaurants(String query) {
        if (query == null || query.isBlank()) return store.getRestaurants();
        String q = query.toLowerCase().trim();
        return store.getRestaurants().stream()
                .filter(r -> r.getName().toLowerCase().contains(q)
                        || r.getArea().toLowerCase().contains(q)
                        || r.getCuisine().toLowerCase().contains(q))
                .sorted(Comparator.comparingDouble(Restaurant::getRating).reversed())
                .collect(Collectors.toList());
    }

    /** Filter restaurants by cuisine type. */
    public List<Restaurant> filterByCuisine(String cuisine) {
        return store.getRestaurants().stream()
                .filter(r -> r.getCuisine().equalsIgnoreCase(cuisine))
                .collect(Collectors.toList());
    }

    /** Filter by area and optionally by open status. */
    public List<Restaurant> filterByArea(String area, boolean openOnly) {
        return store.getRestaurants().stream()
                .filter(r -> r.getArea().equalsIgnoreCase(area))
                .filter(r -> !openOnly || r.isOpen())
                .sorted(Comparator.comparingDouble(Restaurant::getRating).reversed())
                .collect(Collectors.toList());
    }

    /** Search within a restaurant's menu for items matching name or category. */
    public List<MenuItem> searchMenu(Restaurant restaurant, String query) {
        if (query == null || query.isBlank()) return restaurant.getMenu();
        String q = query.toLowerCase().trim();
        return restaurant.getMenu().stream()
                .filter(i -> i.getName().toLowerCase().contains(q)
                        || i.getCategory().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    /** Get menu items sorted by price ascending. */
    public List<MenuItem> menuSortedByPrice(Restaurant restaurant) {
        return restaurant.getMenu().stream()
                .sorted(Comparator.comparingDouble(MenuItem::getPrice))
                .collect(Collectors.toList());
    }

    /** Get all distinct cuisine types available. */
    public List<String> getAllCuisines() {
        return store.getRestaurants().stream()
                .map(Restaurant::getCuisine)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}
