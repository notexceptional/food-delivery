package com.fooddelivery.services;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fooddelivery.models.DeliveryRider;
import com.fooddelivery.models.Order;
import com.fooddelivery.models.OrderItem;
import com.fooddelivery.models.OrderStatus;
import com.fooddelivery.storage.DataStore;

class DeliveryServiceEdgeCaseTest {

    private final DataStore store = DataStore.getInstance();
    private final DeliveryService deliveryService = new DeliveryService();

    @BeforeEach
    void setUp() {
        store.setCustomers(new ArrayList<>());
        store.setRestaurants(new ArrayList<>());
        store.setOrders(new ArrayList<>());
        store.setCoupons(new ArrayList<>());
        store.setRiders(new ArrayList<>());
    }

    @Test
    void getRiderForOrderReturnsNullWhenNoRiderIdPresent() {
        Order order = sampleOrder();

        assertNull(deliveryService.getRiderForOrder(order));
    }

    @Test
    void getTrackingMessageShowsNoRiderWhenOrderHasNoAssignment() {
        Order order = sampleOrder();

        String message = deliveryService.getTrackingMessage(order);

        assertTrue(message.contains("Status: PENDING"));
        assertTrue(message.contains("No rider assigned yet"));
    }

    @Test
    void getTrackingMessageShowsRiderDetailsOnlyForOutForDeliveryStatus() {
        DeliveryRider rider = new DeliveryRider("Rafi", "0170000000");
        store.setRiders(new ArrayList<>(List.of(rider)));

        Order order = sampleOrder();
        order.setRiderId(rider.getRiderId());

        String pendingMessage = deliveryService.getTrackingMessage(order);
        assertTrue(pendingMessage.contains("Rider: Rafi"));

        order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
        String outForDeliveryMessage = deliveryService.getTrackingMessage(order);

        assertTrue(outForDeliveryMessage.contains("Rider: Rafi"));
        assertTrue(outForDeliveryMessage.contains("0170000000"));
    }

    @Test
    void releaseRiderWithNullIdDoesNothing() {
        deliveryService.releaseRider(null);

        assertEquals(0, store.getRiders().size());
    }

    @Test
    void getRiderForOrderReturnsRiderWhenIdMatchesStore() {
        DeliveryRider rider = new DeliveryRider("Nadim", "0180000000");
        store.setRiders(new ArrayList<>(List.of(rider)));

        Order order = sampleOrder();
        order.setRiderId(rider.getRiderId());

        DeliveryRider assigned = deliveryService.getRiderForOrder(order);

        assertNotNull(assigned);
        assertEquals("Nadim", assigned.getName());
    }

    private static Order sampleOrder() {
        return new Order(
                "customer-1",
                "restaurant-1",
                "Test Restaurant",
                List.of(new OrderItem("Burger", 200.0, 1, List.of())),
                200.0,
                null
        );
    }
}
