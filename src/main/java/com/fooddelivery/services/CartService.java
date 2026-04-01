package com.fooddelivery.services;

import com.fooddelivery.models.Customer;
import com.fooddelivery.models.MenuItem;
import com.fooddelivery.models.OrderItem;
import com.fooddelivery.storage.DataStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the customer's shopping cart.
 */
public class CartService {

    private final DataStore store = DataStore.getInstance();

    /** Add a menu item to the customer's cart (merges qty if already present). */
    public void addToCart(Customer customer, MenuItem item, int quantity, List<String> chosenOptions) {
        if (!item.isAvailable()) {
            throw new IllegalStateException(item.getName() + " is not available.");
        }
        // Check if item already in cart — if so, just bump quantity
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

    /** Remove an item from the cart by name. */
    public boolean removeFromCart(Customer customer, String itemName) {
        boolean removed = customer.getCart()
                .removeIf(i -> i.getMenuItemName().equalsIgnoreCase(itemName));
        if (removed) store.saveChanges();
        return removed;
    }

    /** Clear the entire cart. */
    public void clearCart(Customer customer) {
        customer.getCart().clear();
        store.saveChanges();
    }

    /** Calculate raw total before any discount. */
    public double getCartTotal(Customer customer) {
        return customer.getCart().stream()
                .mapToDouble(OrderItem::getSubtotal)
                .sum();
    }

    /** Returns a copy of the current cart items. */
    public List<OrderItem> getCartItems(Customer customer) {
        return customer.getCart();
    }

    public boolean isCartEmpty(Customer customer) {
        return customer.getCart().isEmpty();
    }
}
