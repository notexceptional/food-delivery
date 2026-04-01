package com.fooddelivery.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class Order {

    private String orderId;
    private String customerId;    // customer username
    private String restaurantId;  // restaurant ID
    private String restaurantName;
    private List<OrderItem> items;
    private OrderStatus status;
    private double totalPrice;
    private String couponCode;
    private String timestamp;
    private String riderId;       // rider assigned (can be null)

    public Order(String customerId, String restaurantId, String restaurantName,
                 List<OrderItem> items, double totalPrice, String couponCode) {
        this.orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.items = items;
        this.totalPrice = totalPrice;
        this.couponCode = couponCode;
        this.status = OrderStatus.PENDING;
        this.timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.riderId = null;
    }

    public String getOrderId()                { return orderId; }
    public void   setOrderId(String id)       { this.orderId = id; }

    public String getCustomerId()             { return customerId; }
    public void   setCustomerId(String cid)   { this.customerId = cid; }

    public String getRestaurantId()           { return restaurantId; }
    public void   setRestaurantId(String rid) { this.restaurantId = rid; }

    public String getRestaurantName()         { return restaurantName; }
    public void   setRestaurantName(String n) { this.restaurantName = n; }

    public List<OrderItem> getItems()         { return items; }
    public void setItems(List<OrderItem> i)   { this.items = i; }

    public OrderStatus getStatus()            { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public double getTotalPrice()             { return totalPrice; }
    public void   setTotalPrice(double t)     { this.totalPrice = t; }

    public String getCouponCode()             { return couponCode; }
    public void   setCouponCode(String c)     { this.couponCode = c; }

    public String getTimestamp()              { return timestamp; }
    public void   setTimestamp(String t)      { this.timestamp = t; }

    public String getRiderId()                { return riderId; }
    public void   setRiderId(String riderId)  { this.riderId = riderId; }

    @Override
    public String toString() {
        return orderId + " | " + restaurantName + " | ৳" +
                String.format("%.2f", totalPrice) + " | " + status;
    }
}
