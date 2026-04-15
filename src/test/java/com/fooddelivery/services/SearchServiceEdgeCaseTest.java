package com.fooddelivery.services;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fooddelivery.models.MenuItem;
import com.fooddelivery.models.Restaurant;
import com.fooddelivery.storage.DataStore;

class SearchServiceEdgeCaseTest {

    private final DataStore store = DataStore.getInstance();
    private final SearchService searchService = new SearchService();

    private Restaurant alpha;
    private Restaurant bravo;
    private Restaurant curry;

    @BeforeEach
    void setUp() {
        store.setCustomers(new ArrayList<>());
        store.setOrders(new ArrayList<>());
        store.setCoupons(new ArrayList<>());
        store.setRiders(new ArrayList<>());

        alpha = new Restaurant("Alpha Bites", "Loc1", "AreaX", "111", "a@x.com", "9-9", "FastFood", "o1", "p1");
        bravo = new Restaurant("Bravo Bowl", "Loc2", "AreaX", "222", "b@x.com", "9-9", "Thai", "o2", "p2");
        curry = new Restaurant("Curry House", "Loc3", "AreaY", "333", "c@x.com", "9-9", "Indian", "o3", "p3");

        alpha.setRating(3.9);
        bravo.setRating(4.8);
        curry.setRating(4.2);
        alpha.setOpen(false);

        alpha.setMenu(new ArrayList<>(List.of(
            new MenuItem("Fries", "Crispy", "Sides", 120.0),
            new MenuItem("Burger", "Beef", "Main", 250.0)
        )));

        store.setRestaurants(new ArrayList<>(List.of(alpha, bravo, curry)));
    }

    @Test
    void searchRestaurantsReturnsAllWhenQueryIsBlank() {
        List<Restaurant> all = searchService.searchRestaurants("   ");

        assertEquals(3, all.size());
    }

    @Test
    void searchRestaurantsMatchesAndSortsByRatingDescending() {
        List<Restaurant> results = searchService.searchRestaurants("areax");

        assertEquals(2, results.size());
        assertEquals("Bravo Bowl", results.get(0).getName());
        assertEquals("Alpha Bites", results.get(1).getName());
    }

    @Test
    void filterByAreaOpenOnlyExcludesClosedRestaurants() {
        List<Restaurant> openInAreaX = searchService.filterByArea("AreaX", true);

        assertEquals(1, openInAreaX.size());
        assertEquals("Bravo Bowl", openInAreaX.get(0).getName());
    }

    @Test
    void searchMenuReturnsWholeMenuWhenQueryIsBlank() {
        List<MenuItem> menu = searchService.searchMenu(alpha, "");

        assertEquals(2, menu.size());
    }

    @Test
    void menuSortedByPriceReturnsAscendingOrder() {
        List<MenuItem> sorted = searchService.menuSortedByPrice(alpha);

        assertIterableEquals(List.of("Fries", "Burger"),
                sorted.stream().map(MenuItem::getName).toList());
    }
}
