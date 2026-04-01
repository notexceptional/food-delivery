package com.fooddelivery;

import com.fooddelivery.api.FoodDeliveryService;
import com.fooddelivery.storage.DataStore;
import com.fooddelivery.storage.JsonStorage;
import com.fooddelivery.ui.MainFrame;
import jakarta.xml.ws.Endpoint;

import javax.swing.*;

/**
 * Application entry point.
 *
 * 1. Loads persisted data from /data/*.json
 * 2. Publishes SOAP web service on port 8888
 *    → WSDL: http://localhost:8888/FoodDeliveryService?wsdl
 * 3. Launches the Swing UI
 */
public class Main {

    public static void main(String[] args) {

        // ── 1. Load persisted data ─────────────────────────────────────────
        System.out.println("[Main] Loading data from disk...");
        DataStore store = DataStore.getInstance();
        JsonStorage.loadAll(store);
        System.out.println("[Main] Loaded "
                + store.getCustomers().size()    + " customer(s), "
                + store.getRestaurants().size()  + " restaurant(s), "
                + store.getOrders().size()       + " order(s).");

        // ── 2. Start SOAP Web Service ──────────────────────────────────────
        String soapAddress = "http://localhost:8888/FoodDeliveryService";
        try {
            Endpoint.publish(soapAddress, new FoodDeliveryService());
            System.out.println("[Main] SOAP Service published.");
            System.out.println("[Main] WSDL → " + soapAddress + "?wsdl");
        } catch (Exception e) {
            System.err.println("[Main] Failed to start SOAP service: " + e.getMessage());
            System.err.println("[Main] Continuing without SOAP API...");
        }

        // ── 3. Launch Swing UI on the Event Dispatch Thread ────────────────
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
