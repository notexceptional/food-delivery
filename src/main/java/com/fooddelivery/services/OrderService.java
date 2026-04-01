package com.fooddelivery.services;

import com.fooddelivery.models.*;
import com.fooddelivery.storage.DataStore;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles placing orders, updating status, and retrieving order history.
 */
public class OrderService {

    private final DataStore store          = DataStore.getInstance();
    private final DeliveryService delivery = new DeliveryService();

    /**
     * Places an order for a customer from a restaurant.
     * Clears the customer's cart after placing.
     */
    public Order placeOrder(Customer customer, Restaurant restaurant,
                            List<OrderItem> items, double totalPrice,
                            String couponCode) {
        if (items == null || items.isEmpty()) {
            throw new IllegalStateException("Cannot place an empty order.");
        }
        if (!restaurant.isOpen()) {
            throw new IllegalStateException("Restaurant is currently closed.");
        }
        Order order = new Order(customer.getUserName(), restaurant.getRestaurantId(),
                restaurant.getName(), items, totalPrice, couponCode);
        store.addOrder(order);

        // Record order ID in customer history
        customer.addToOrderHistory(order.getOrderId());
        store.saveChanges();

        // Auto-assign a free rider
        delivery.assignRider(order);

        return order;
    }

    /** Update the status of an order (e.g., restaurant marks it PREPARING). */
    public void updateStatus(String orderId, OrderStatus newStatus) {
        Order order = store.findOrderById(orderId);
        if (order == null) throw new IllegalArgumentException("Order not found: " + orderId);
        order.setStatus(newStatus);

        // If delivered, free up the rider
        if (newStatus == OrderStatus.DELIVERED || newStatus == OrderStatus.CANCELLED) {
            delivery.releaseRider(order.getRiderId());
        }
        store.saveChanges();
    }

    /** Get all orders placed by a specific customer. */
    public List<Order> getOrdersByCustomer(String username) {
        return store.getOrders().stream()
                .filter(o -> o.getCustomerId().equalsIgnoreCase(username))
                .collect(Collectors.toList());
    }

    /** Get all orders received by a specific restaurant. */
    public List<Order> getOrdersByRestaurant(String restaurantId) {
        return store.getOrders().stream()
                .filter(o -> o.getRestaurantId().equals(restaurantId))
                .collect(Collectors.toList());
    }

    /** Get a single order by ID. */
    public Order getOrderById(String orderId) {
        return store.findOrderById(orderId);
    }

    /** Get active (non-delivered, non-cancelled) orders for a restaurant. */
    public List<Order> getActiveOrdersForRestaurant(String restaurantId) {
        return getOrdersByRestaurant(restaurantId).stream()
                .filter(o -> o.getStatus() != OrderStatus.DELIVERED
                        && o.getStatus() != OrderStatus.CANCELLED)
                .collect(Collectors.toList());
    }
}
