package com.fooddelivery.services;

import java.util.List;
import java.util.stream.Collectors;

import com.fooddelivery.models.Customer;
import com.fooddelivery.models.Order;
import com.fooddelivery.models.OrderItem;
import com.fooddelivery.models.OrderStatus;
import com.fooddelivery.models.Restaurant;
import com.fooddelivery.storage.DataStore;

public class OrderService {

    private final DataStore store = DataStore.getInstance();
    private final DeliveryService delivery = new DeliveryService();

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

        customer.addToOrderHistory(order.getOrderId());
        store.saveChanges();

        delivery.assignRider(order);

        return order;
    }

    public void updateStatus(String orderId, OrderStatus newStatus) {
        Order order = store.findOrderById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found: " + orderId);
        }
        order.setStatus(newStatus);

        if (newStatus == OrderStatus.DELIVERED || newStatus == OrderStatus.CANCELLED) {
            delivery.releaseRider(order.getRiderId());
        }
        store.saveChanges();
    }

    public List<Order> getOrdersByCustomer(String username) {
        return store.getOrders().stream()
                .filter(o -> o.getCustomerId().equalsIgnoreCase(username))
                .collect(Collectors.toList());
    }

    public List<Order> getOrdersByRestaurant(String restaurantId) {
        return store.getOrders().stream()
                .filter(o -> o.getRestaurantId().equals(restaurantId))
                .collect(Collectors.toList());
    }

    public Order getOrderById(String orderId) {
        return store.findOrderById(orderId);
    }

    public List<Order> getActiveOrdersForRestaurant(String restaurantId) {
        return getOrdersByRestaurant(restaurantId).stream()
                .filter(o -> o.getStatus() != OrderStatus.DELIVERED
                && o.getStatus() != OrderStatus.CANCELLED)
                .collect(Collectors.toList());
    }
}
