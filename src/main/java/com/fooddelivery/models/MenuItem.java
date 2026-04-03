package com.fooddelivery.models;

import java.util.ArrayList;
import java.util.List;

public class MenuItem {
    private String name;
    private double price;
    private String category;
    private String description;
    private boolean isAvailable;
    private int quantity;
    private List<MenuItem> addOns;
    private List<String> options;

    public MenuItem(String name, double price) {
        this.name = name;
        this.price = price;
        this.category = "General";
        this.description = "";
        this.isAvailable = true;
        this.quantity = 0;
        this.addOns = new ArrayList<>();
        this.options = new ArrayList<>();
    }

    public MenuItem(String name, String description, String category, double price) {
        this(name, price);
        this.description = description;
        this.category = category;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public List<MenuItem> getAddOns() { return addOns; }
    public void setAddOns(List<MenuItem> addOns) { this.addOns = addOns; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }

}