package com.fooddelivery.services;

import com.fooddelivery.models.DeliveryRider;
import com.fooddelivery.models.Order;
import com.fooddelivery.models.OrderStatus;
import com.fooddelivery.storage.DataStore;

/**
 * Assigns riders to orders and manages rider availability.
 */
public class DeliveryService {

    private final DataStore store = DataStore.getInstance();

    /** Assign an available rider to the order. */
    public void assignRider(Order order) {
        DeliveryRider rider = store.findAvailableRider();
        if (rider != null) {
            rider.setAvailable(false);
            rider.setCurrentOrderId(order.getOrderId());
            order.setRiderId(rider.getRiderId());
            store.saveChanges();
            System.out.println("[DeliveryService] Rider " + rider.getName()
                    + " assigned to " + order.getOrderId());
        } else {
            System.out.println("[DeliveryService] No riders available for " + order.getOrderId());
        }
    }

    /** Free a rider after delivery or cancellation. */
    public void releaseRider(String riderId) {
        if (riderId == null) return;
        store.getRiders().stream()
                .filter(r -> r.getRiderId().equals(riderId))
                .findFirst()
                .ifPresent(r -> {
                    r.setAvailable(true);
                    r.setCurrentOrderId(null);
                    store.saveChanges();
                });
    }

    /** Add a new delivery rider to the system. */
    public DeliveryRider addRider(String name, String phone) {
        DeliveryRider rider = new DeliveryRider(name, phone);
        store.addRider(rider);
        return rider;
    }

    /** Get rider info for a given order. */
    public DeliveryRider getRiderForOrder(Order order) {
        if (order.getRiderId() == null) return null;
        return store.getRiders().stream()
                .filter(r -> r.getRiderId().equals(order.getRiderId()))
                .findFirst().orElse(null);
    }

    /** Returns a readable tracking status message. */
    public String getTrackingMessage(Order order) {
        DeliveryRider rider = getRiderForOrder(order);
        String statusLine = "Status: " + order.getStatus();
        if (rider != null && order.getStatus() == OrderStatus.OUT_FOR_DELIVERY) {
            return statusLine + "\nRider: " + rider.getName() + " | " + rider.getPhone();
        }
        return statusLine + (rider != null ? "\nRider: " + rider.getName() : "\nNo rider assigned yet");
    }
}
