package com.fooddelivery.storage;

import com.fooddelivery.models.*;

import java.util.ArrayList;
import java.util.List;





public class DataStore {

    private static DataStore instance;

    private List<Customer>      customers    = new ArrayList<>();
    private List<Restaurant>    restaurants  = new ArrayList<>();
    private List<Order>         orders       = new ArrayList<>();
    private List<Coupon>        coupons      = new ArrayList<>();
    private List<DeliveryRider> riders       = new ArrayList<>();

    private DataStore() {}

    public static DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    
    public List<Customer>   getCustomers()                         { return customers; }
    public void             setCustomers(List<Customer> customers) { this.customers = customers; }

    public void addCustomer(Customer c) {
        customers.add(c);
        JsonStorage.saveAll(this);
    }

    public Customer findCustomerByUsername(String username) {
        return customers.stream()
                .filter(c -> c.getUserName().equalsIgnoreCase(username))
                .findFirst().orElse(null);
    }

    
    public List<Restaurant> getRestaurants()                           { return restaurants; }
    public void             setRestaurants(List<Restaurant> list)      { this.restaurants = list; }

    public void addRestaurant(Restaurant r) {
        restaurants.add(r);
        JsonStorage.saveAll(this);
    }

    public Restaurant findRestaurantById(String id) {
        return restaurants.stream()
                .filter(r -> r.getRestaurantId().equals(id))
                .findFirst().orElse(null);
    }

    public Restaurant findRestaurantByOwner(String username) {
        return restaurants.stream()
                .filter(r -> r.getOwnerUsername().equalsIgnoreCase(username))
                .findFirst().orElse(null);
    }

    
    public List<Order>  getOrders()                { return orders; }
    public void         setOrders(List<Order> o)   { this.orders = o; }

    public void addOrder(Order o) {
        orders.add(o);
        JsonStorage.saveAll(this);
    }

    public Order findOrderById(String orderId) {
        return orders.stream()
                .filter(o -> o.getOrderId().equals(orderId))
                .findFirst().orElse(null);
    }

    public void saveChanges() {
        JsonStorage.saveAll(this);
    }

    
    public List<Coupon> getCoupons()               { return coupons; }
    public void         setCoupons(List<Coupon> c) { this.coupons = c; }

    public void addCoupon(Coupon c) {
        coupons.add(c);
        JsonStorage.saveAll(this);
    }

    public Coupon findCouponByCode(String code) {
        return coupons.stream()
                .filter(c -> c.getCode().equalsIgnoreCase(code) && c.isActive())
                .findFirst().orElse(null);
    }

    
    public List<DeliveryRider> getRiders()                   { return riders; }
    public void                setRiders(List<DeliveryRider> r) { this.riders = r; }

    public void addRider(DeliveryRider r) {
        riders.add(r);
        JsonStorage.saveAll(this);
    }

    public DeliveryRider findAvailableRider() {
        return riders.stream()
                .filter(DeliveryRider::isAvailable)
                .findFirst().orElse(null);
    }
}
