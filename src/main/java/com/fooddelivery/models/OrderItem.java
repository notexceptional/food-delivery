package com.fooddelivery.models;

import java.util.List;

public class OrderItem {

    private String menuItemName;
    private double unitPrice;
    private int quantity;
    private List<String> chosenOptions;

    public OrderItem(String menuItemName, double unitPrice,
                     int quantity, List<String> chosenOptions) {
        this.menuItemName = menuItemName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.chosenOptions = chosenOptions;
    }

    public String getMenuItemName()               { return menuItemName; }
    public void   setMenuItemName(String name)    { this.menuItemName = name; }

    public double getUnitPrice()                  { return unitPrice; }
    public void   setUnitPrice(double unitPrice)  { this.unitPrice = unitPrice; }

    public int  getQuantity()                     { return quantity; }
    public void setQuantity(int quantity)         { this.quantity = quantity; }

    public List<String> getChosenOptions()                  { return chosenOptions; }
    public void         setChosenOptions(List<String> opts) { this.chosenOptions = opts; }

    
    public double getSubtotal() {
        return unitPrice * quantity;
    }

    @Override
    public String toString() {
        return menuItemName + " x" + quantity + " @ ৳" + unitPrice
                + " = ৳" + String.format("%.2f", getSubtotal());
    }
}
