package com.fooddelivery;

import javax.swing.SwingUtilities;

import com.fooddelivery.api.FoodDeliveryService;
import com.fooddelivery.storage.DataStore;
import com.fooddelivery.storage.JsonStorage;
import com.fooddelivery.ui.MainFrame;

import jakarta.xml.ws.Endpoint;
public class Main {

    public static void main(String[] args) {
        System.out.println("[Main] Loading data from disk...");
        DataStore store = DataStore.getInstance();
        JsonStorage.loadAll(store);
        System.out.println("[Main] Loaded "
                + store.getCustomers().size()    + " customer(s), "
                + store.getRestaurants().size()  + " restaurant(s), "
                + store.getOrders().size()       + " order(s).");
        String soapAddress = "http://localhost:8888/FoodDeliveryService";
        try {
            Endpoint.publish(soapAddress, new FoodDeliveryService());
            System.out.println("[Main] SOAP Service published.");
            System.out.println("[Main] WSDL → " + soapAddress + "?wsdl");
        } catch (Exception e) {
            System.err.println("[Main] Failed to start SOAP service: " + e.getMessage());
            System.err.println("[Main] Continuing without SOAP API...");
        }
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
