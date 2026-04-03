package com.fooddelivery.services;

import java.util.ArrayList;
import java.util.List;

import com.fooddelivery.models.Customer;
import com.fooddelivery.models.MenuItem;
import com.fooddelivery.models.OrderItem;
import com.fooddelivery.storage.DataStore;

public class CartService {

    private final DataStore store = DataStore.getInstance();

    public void addToCart(Customer customer, MenuItem item, int quantity, List<String> chosenOptions) {
        if (!item.isAvailable()) {
            throw new IllegalStateException(item.getName() + " is not available.");
        }

        for (OrderItem existing : customer.getCart()) {
            if (existing.getMenuItemName().equals(item.getName())) {
                existing.setQuantity(existing.getQuantity() + quantity);
                store.saveChanges();
                return;
            }
        }
        OrderItem cartItem = new OrderItem(item.getName(), item.getPrice(),
                quantity, chosenOptions != null ? chosenOptions : new ArrayList<>());
        customer.getCart().add(cartItem);
        store.saveChanges();
    }

    public boolean removeFromCart(Customer customer, String itemName) {
        boolean removed = customer.getCart()
                .removeIf(i -> i.getMenuItemName().equalsIgnoreCase(itemName));
        if (removed) {
            store.saveChanges();
        }
        return removed;
    }

    public void clearCart(Customer customer) {
        customer.getCart().clear();
        store.saveChanges();
    }

    public double getCartTotal(Customer customer) {
        return customer.getCart().stream()
                .mapToDouble(OrderItem::getSubtotal)
                .sum();
    }

    public List<OrderItem> getCartItems(Customer customer) {
        return customer.getCart();
    }

    public boolean isCartEmpty(Customer customer) {
        return customer.getCart().isEmpty();
    }
}
