package com.fooddelivery.models;

public class DeliveryRider {

    private String riderId;
    private String name;
    private String phone;
    private boolean isAvailable;
    private String currentOrderId; 

    public DeliveryRider(String name, String phone) {
        this.riderId = "RDR-" + java.util.UUID.randomUUID()
                .toString().substring(0, 6).toUpperCase();
        this.name = name;
        this.phone = phone;
        this.isAvailable = true;
        this.currentOrderId = null;
    }

    public String getRiderId()                  { return riderId; }
    public void   setRiderId(String riderId)    { this.riderId = riderId; }

    public String getName()                     { return name; }
    public void   setName(String name)          { this.name = name; }

    public String getPhone()                    { return phone; }
    public void   setPhone(String phone)        { this.phone = phone; }

    public boolean isAvailable()                { return isAvailable; }
    public void    setAvailable(boolean avail)  { this.isAvailable = avail; }

    public String getCurrentOrderId()           { return currentOrderId; }
    public void   setCurrentOrderId(String oid) { this.currentOrderId = oid; }

    @Override
    public String toString() {
        return name + " (" + phone + ")" + (isAvailable ? " [Available]" : " [On Delivery]");
    }
}
