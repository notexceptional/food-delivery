package com.fooddelivery.models;

import java.util.ArrayList;
import java.util.List;

public class Customer extends User {

    private String address;
    private List<OrderItem> cart;
    private List<String> orderHistory;

    public Customer() {
        super("", "", "", "", "");
        this.address = "";
        this.cart = new ArrayList<>();
        this.orderHistory = new ArrayList<>();
    }

    public Customer(String userId, String userName, String password,
            String email, String phone, String address) {
        super(userId, userName, password, email, phone);
        this.address = address;
        this.cart = new ArrayList<>();
        this.orderHistory = new ArrayList<>();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<OrderItem> getCart() {
        return cart;
    }

    public void setCart(List<OrderItem> cart) {
        this.cart = cart;
    }

    public List<String> getOrderHistory() {
        return orderHistory;
    }

    public void setOrderHistory(List<String> orderHistory) {
        this.orderHistory = orderHistory;
    }

    public void addToOrderHistory(String orderId) {
        this.orderHistory.add(orderId);
    }

}
