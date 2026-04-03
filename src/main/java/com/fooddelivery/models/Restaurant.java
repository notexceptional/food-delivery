package com.fooddelivery.models;

import java.util.ArrayList;
import java.util.List;

public class Restaurant {

    private String name;
    private String restaurantId;
    private String location;
    private String contactInfo;
    private String email;
    private String openingHours;
    private String area;
    private String cuisine;
    private double rating;
    private boolean open;
    private String ownerUsername;
    private String ownerPassword;
    private List<MenuItem> menu;
    private List<Coupon> coupons;

    public Restaurant(String name, String location, String contactInfo, String email, String openingHours) {
        this(name, location, location, contactInfo, email, openingHours, "General", "", "");
    }

    public Restaurant(String name, String location, String area, String contactInfo, String email,
            String openingHours, String cuisine, String ownerUsername, String ownerPassword) {
        this.name = name;
        this.restaurantId = name;
        this.location = location;
        this.area = area;
        this.contactInfo = contactInfo;
        this.email = email;
        this.openingHours = openingHours;
        this.cuisine = cuisine;
        this.rating = 0.0;
        this.open = true;
        this.ownerUsername = ownerUsername;
        this.ownerPassword = ownerPassword;
        this.menu = new ArrayList<>();
        this.coupons = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public List<MenuItem> getMenu() {
        return menu;
    }

    public void setMenu(List<MenuItem> menu) {
        this.menu = menu;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public String getOwnerPassword() {
        return ownerPassword;
    }

    public void setOwnerPassword(String ownerPassword) {
        this.ownerPassword = ownerPassword;
    }

    public List<Coupon> getCoupons() {
        return coupons;
    }

    public void setCoupons(List<Coupon> coupons) {
        this.coupons = coupons;
    }

}
