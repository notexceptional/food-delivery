package com.fooddelivery.api;

import java.util.List;

import com.fooddelivery.models.MenuItem;
import com.fooddelivery.models.Order;
import com.fooddelivery.models.Restaurant;
import com.fooddelivery.services.OrderService;
import com.fooddelivery.services.SearchService;
import com.fooddelivery.storage.DataStore;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

@WebService(name = "FoodDeliveryService",
        serviceName = "FoodDeliveryService",
        targetNamespace = "http://api.fooddelivery.com/")
public class FoodDeliveryService {

    private final SearchService searchService = new SearchService();
    private final OrderService orderService = new OrderService();
    private final DataStore store = DataStore.getInstance();

    @WebMethod(operationName = "getRestaurantsByArea")
    public String getRestaurantsByArea(
            @WebParam(name = "area") String area) {

        List<Restaurant> results = searchService.filterByArea(area, false);
        if (results.isEmpty()) {
            return "No restaurants found in area: " + area;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Restaurants in ").append(area).append(":\n");
        sb.append("─".repeat(50)).append("\n");
        for (Restaurant r : results) {
                sb.append(String.format("%-25s | %-15s | %.1f | %s%n",
                    r.getName(), r.getCuisine(), r.getRating(),
                    r.isOpen() ? "OPEN" : "CLOSED"));
        }
        return sb.toString();
    }

    @WebMethod(operationName = "searchRestaurants")
    public String searchRestaurants(
            @WebParam(name = "query") String query) {

        List<Restaurant> results = searchService.searchRestaurants(query);
        if (results.isEmpty()) {
            return "No restaurants match: " + query;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Search results for \"").append(query).append("\":\n");
        sb.append("─".repeat(50)).append("\n");
        for (Restaurant r : results) {
                sb.append(String.format("%-25s | %s | %.1f | %s%n",
                    r.getName(), r.getArea(), r.getRating(),
                    r.isOpen() ? "OPEN" : "CLOSED"));
        }
        return sb.toString();
    }

    @WebMethod(operationName = "getMenuByRestaurantId")
    public String getMenuByRestaurantId(
            @WebParam(name = "restaurantId") String restaurantId) {

        Restaurant r = store.findRestaurantById(restaurantId);
        if (r == null) {
            return "Restaurant not found: " + restaurantId;
        }
        List<MenuItem> menu = r.getMenu();
        if (menu.isEmpty()) {
            return r.getName() + " has no menu items yet.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Menu for ").append(r.getName())
                .append(" (").append(r.getCuisine()).append(")\n");
        sb.append("─".repeat(50)).append("\n");
        String currentCategory = "";
        for (MenuItem item : menu) {
            if (!item.getCategory().equals(currentCategory)) {
                currentCategory = item.getCategory();
                sb.append("\n[").append(currentCategory).append("]\n");
            }
            sb.append(String.format("  %-25s ৳%-8.2f %s%n",
                    item.getName(), item.getPrice(),
                    item.isAvailable() ? "" : "[Unavailable]"));
            if (!item.getDescription().isBlank()) {
                sb.append("    └ ").append(item.getDescription()).append("\n");
            }
        }
        return sb.toString();
    }

    @WebMethod(operationName = "getOrderStatus")
    public String getOrderStatus(
            @WebParam(name = "orderId") String orderId) {

        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            return "Order not found: " + orderId;
        }
        return String.format(
                "Order ID  : %s%n"
                + "Restaurant: %s%n"
                + "Status    : %s%n"
                + "Total     : ৳%.2f%n"
                + "Placed    : %s",
                order.getOrderId(), order.getRestaurantName(),
                order.getStatus(), order.getTotalPrice(), order.getTimestamp());
    }
}
