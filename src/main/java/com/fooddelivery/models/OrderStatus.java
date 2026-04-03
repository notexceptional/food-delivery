package com.fooddelivery.models;

public enum OrderStatus {
    PENDING,
    PREPARING,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED;

    @Override
    public String toString() {
        return switch (this) {
            case PENDING          -> "Pending";
            case PREPARING        -> "Preparing";
            case OUT_FOR_DELIVERY -> "Out for Delivery";
            case DELIVERED        -> "Delivered";
            case CANCELLED        -> "Cancelled";
        };
    }
}
