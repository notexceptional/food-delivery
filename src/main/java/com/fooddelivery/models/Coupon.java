package com.fooddelivery.models;

public class Coupon {

    private String code;
    private int discountPercent; 
    private boolean isActive;
    private String restaurantId; 

    public Coupon(String code, int discountPercent, String restaurantId) {
        this.code = code;
        this.discountPercent = discountPercent;
        this.isActive = true;
        this.restaurantId = restaurantId;
    }

    public String getCode()                  { return code; }
    public void   setCode(String code)       { this.code = code; }

    public int  getDiscountPercent()                     { return discountPercent; }
    public void setDiscountPercent(int discountPercent)  { this.discountPercent = discountPercent; }

    public boolean isActive()                { return isActive; }
    public void    setActive(boolean active) { this.isActive = active; }

    public String getRestaurantId()          { return restaurantId; }
    public void   setRestaurantId(String id) { this.restaurantId = id; }

    @Override
    public String toString() {
        return code + " — " + discountPercent + "% off"
                + (isActive ? "" : " [Inactive]");
    }
}
