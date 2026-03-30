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
    private List<MenuItem>menu;

    public Restaurant(String name, String location, String contactInfo, String email, String openingHours) {
        this.name = name;
        this.restaurantId = name;
        this.location = location;
        this.contactInfo = contactInfo;
        this.email = email;
        this.openingHours = openingHours;
        this.menu = new ArrayList<>();
    }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRestaurantId() { return restaurantId; }
    public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getOpeningHours() { return openingHours; }
    public void setOpeningHours(String openingHours) { this.openingHours = openingHours; }
    public List<MenuItem> getMenu() { return menu; }
    public void setMenu(List<MenuItem> menu) { this.menu = menu; }

}
