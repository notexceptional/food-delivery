package com.fooddelivery.storage;

import com.fooddelivery.models.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;




public class JsonStorage {

    private static final String DATA_DIR = "data";
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    
    public static void saveAll(DataStore store) {
        saveToFile("customers.json",    store.getCustomers());
        saveToFile("restaurants.json",  store.getRestaurants());
        saveToFile("orders.json",       store.getOrders());
        saveToFile("coupons.json",      store.getCoupons());
        saveToFile("riders.json",       store.getRiders());
    }

    
    public static void loadAll(DataStore store) {
        store.setCustomers(   loadFromFile("customers.json",    new TypeToken<List<Customer>>(){}.getType()));
        store.setRestaurants( loadFromFile("restaurants.json",  new TypeToken<List<Restaurant>>(){}.getType()));
        store.setOrders(      loadFromFile("orders.json",       new TypeToken<List<Order>>(){}.getType()));
        store.setCoupons(     loadFromFile("coupons.json",      new TypeToken<List<Coupon>>(){}.getType()));
        store.setRiders(      loadFromFile("riders.json",       new TypeToken<List<DeliveryRider>>(){}.getType()));
    }

    
    private static <T> void saveToFile(String filename, List<T> data) {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdirs();

        try (Writer writer = new FileWriter(new File(dir, filename))) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            System.err.println("[JsonStorage] Failed to save " + filename + ": " + e.getMessage());
        }
    }

    private static <T> List<T> loadFromFile(String filename, Type type) {
        File file = new File(DATA_DIR, filename);
        if (!file.exists()) return new ArrayList<>();

        try (Reader reader = new FileReader(file)) {
            List<T> result = GSON.fromJson(reader, type);
            return (result != null) ? result : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("[JsonStorage] Failed to load " + filename + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
