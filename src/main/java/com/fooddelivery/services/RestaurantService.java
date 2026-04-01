package com.fooddelivery.services;

import com.fooddelivery.models.Restaurant;
import com.fooddelivery.storage.DataStore;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages restaurant listing, search, open/close status, and ratings.
 */
public class RestaurantService {

    private final DataStore store = DataStore.getInstance();

    public List<Restaurant> getAllRestaurants() {
        return store.getRestaurants();
    }

    /** Returns restaurants in a given area (case-insensitive). */
    public List<Restaurant> getByArea(String area) {
        return store.getRestaurants().stream()
                .filter(r -> r.getArea().equalsIgnoreCase(area))
                .collect(Collectors.toList());
    }

    /** Returns only open restaurants in a given area. */
    public List<Restaurant> getOpenByArea(String area) {
        return getByArea(area).stream()
                .filter(Restaurant::isOpen)
                .collect(Collectors.toList());
    }

    /** Search by restaurant name or cuisine (partial, case-insensitive). */
    public List<Restaurant> search(String query) {
        String q = query.toLowerCase();
        return store.getRestaurants().stream()
                .filter(r -> r.getName().toLowerCase().contains(q)
                        || r.getCuisine().toLowerCase().contains(q)
                        || r.getArea().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    /** Sort all restaurants by rating descending. */
    public List<Restaurant> sortByRating() {
        return store.getRestaurants().stream()
                .sorted((a, b) -> Double.compare(b.getRating(), a.getRating()))
                .collect(Collectors.toList());
    }

    /** Sort all restaurants by name ascending. */
    public List<Restaurant> sortByName() {
        return store.getRestaurants().stream()
                .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
                .collect(Collectors.toList());
    }

    public void setOpen(Restaurant r, boolean open) {
        r.setOpen(open);
        store.saveChanges();
    }

    public void updateRating(Restaurant r, double rating) {
        r.setRating(Math.max(0, Math.min(5, rating)));
        store.saveChanges();
    }

    /** Returns all distinct areas across registered restaurants. */
    public List<String> getAllAreas() {
        return store.getRestaurants().stream()
                .map(Restaurant::getArea)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}
