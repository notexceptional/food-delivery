package com.fooddelivery.services;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fooddelivery.models.Order;
import com.fooddelivery.models.OrderItem;
import com.fooddelivery.models.OrderStatus;
import com.fooddelivery.storage.DataStore;

class OrderServiceEdgeCaseTest {

    private final DataStore store = DataStore.getInstance();
    private final OrderService orderService = new OrderService();

    @BeforeEach
    void setUp() {
        store.setCustomers(new ArrayList<>());
        store.setRestaurants(new ArrayList<>());
        store.setCoupons(new ArrayList<>());
        store.setRiders(new ArrayList<>());

        Order pending = sampleOrder("alice", "REST-A", "A Bistro", OrderStatus.PENDING);
        Order delivered = sampleOrder("alice", "REST-A", "A Bistro", OrderStatus.DELIVERED);
        Order cancelled = sampleOrder("bob", "REST-A", "A Bistro", OrderStatus.CANCELLED);
        Order otherRestaurant = sampleOrder("alice", "REST-B", "B Bistro", OrderStatus.PREPARING);

        store.setOrders(new ArrayList<>(List.of(pending, delivered, cancelled, otherRestaurant)));
    }

    @Test
    void getActiveOrdersForRestaurantExcludesDeliveredAndCancelled() {
        List<Order> active = orderService.getActiveOrdersForRestaurant("REST-A");

        assertEquals(1, active.size());
        assertEquals(OrderStatus.PENDING, active.get(0).getStatus());
    }

    @Test
    void getOrdersByCustomerUsesCaseInsensitiveMatch() {
        List<Order> aliceOrders = orderService.getOrdersByCustomer("ALICE");

        assertEquals(3, aliceOrders.size());
    }

    @Test
    void updateStatusThrowsWhenOrderDoesNotExist() {
        assertThrows(IllegalArgumentException.class,
                () -> orderService.updateStatus("MISSING-ID", OrderStatus.DELIVERED));
    }

    private static Order sampleOrder(String customerId, String restaurantId,
            String restaurantName, OrderStatus status) {
        Order order = new Order(
                customerId,
                restaurantId,
                restaurantName,
                List.of(new OrderItem("Item", 100.0, 1, List.of())),
                100.0,
                null
        );
        order.setStatus(status);
        return order;
    }
}
