package com.fooddelivery.api;

import com.fooddelivery.models.MenuItem;
import com.fooddelivery.models.Order;
import com.fooddelivery.models.Restaurant;
import com.fooddelivery.services.OrderService;
import com.fooddelivery.services.SearchService;
import com.fooddelivery.storage.DataStore;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

import java.util.List;

/**
 * SOAP Web Service for Food Delivery.
 *
 * WSDL available at: http://localhost:8888/FoodDeliveryService?wsdl
 *
 * Exposed operations:
 *  1. getRestaurantsByArea   - search restaurants by delivery area
 *  2. searchRestaurants      - search by name / cuisine / area keyword
 *  3. getMenuByRestaurantId  - get the full menu of a restaurant
 *  4. getOrderStatus         - check the current status of an order
 */
@WebService(name = "FoodDeliveryService",
            serviceName = "FoodDeliveryService",
            targetNamespace = "http://api.fooddelivery.com/")
public class FoodDeliveryService {

    private final SearchService searchService = new SearchService();
    private final OrderService  orderService  = new OrderService();
    private final DataStore     store         = DataStore.getInstance();

    /**
     * Get all restaurants available in a given area.
     *
     * @param area  e.g. "Gulshan", "Dhanmondi"
     * @return      formatted list of restaurants (name, cuisine, rating, open/closed)
     */
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
            sb.append(String.format("%-25s | %-15s | ⭐%.1f | %s%n",
                    r.getName(), r.getCuisine(), r.getRating(),
                    r.isOpen() ? "OPEN" : "CLOSED"));
        }
        return sb.toString();
    }

    /**
     * Search all restaurants by keyword (name, cuisine, or area).
     *
     * @param query  search keyword
     * @return       formatted list of matching restaurants
     */
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
            sb.append(String.format("%-25s | %s | ⭐%.1f | %s%n",
                    r.getName(), r.getArea(), r.getRating(),
                    r.isOpen() ? "OPEN" : "CLOSED"));
        }
        return sb.toString();
    }

    /**
     * Get the full menu of a restaurant by its ID.
     *
     * @param restaurantId  the restaurant ID (e.g. REST-A1B2C3D4)
     * @return              formatted menu listing
     */
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

    /**
     * Get the current delivery status of an order.
     *
     * @param orderId  the order ID (e.g. ORD-A1B2C3D4)
     * @return         status message with rider info if available
     */
    @WebMethod(operationName = "getOrderStatus")
    public String getOrderStatus(
            @WebParam(name = "orderId") String orderId) {

        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            return "Order not found: " + orderId;
        }
        return String.format(
                "Order ID  : %s%n" +
                "Restaurant: %s%n" +
                "Status    : %s%n" +
                "Total     : ৳%.2f%n" +
                "Placed    : %s",
                order.getOrderId(), order.getRestaurantName(),
                order.getStatus(), order.getTotalPrice(), order.getTimestamp());
    }
}
