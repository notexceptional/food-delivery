package com.fooddelivery.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import com.fooddelivery.models.Coupon;
import com.fooddelivery.models.Customer;
import com.fooddelivery.models.Order;
import com.fooddelivery.models.OrderItem;
import com.fooddelivery.models.OrderStatus;
import com.fooddelivery.models.Restaurant;
import com.fooddelivery.services.AuthService;
import com.fooddelivery.services.CartService;
import com.fooddelivery.services.CouponService;
import com.fooddelivery.services.DeliveryService;
import com.fooddelivery.services.OrderService;
import com.fooddelivery.services.PaymentService;
import com.fooddelivery.services.RestaurantService;
import com.fooddelivery.services.SearchService;





public class CustomerPanel extends JPanel {

    
    private final AuthService      authService    = new AuthService();
    private final RestaurantService restService   = new RestaurantService();
    private final SearchService    searchService  = new SearchService();
    private final CartService      cartService    = new CartService();
    private final CouponService    couponService  = new CouponService();
    private final PaymentService   paymentService = new PaymentService();
    private final OrderService     orderService   = new OrderService();

    
    private Customer        loggedInCustomer;
    private Restaurant      selectedRestaurant;
    private Coupon          appliedCoupon;

    
    private final CardLayout cardLayout = new CardLayout();

    
    private DefaultTableModel restaurantTableModel;
    private DefaultTableModel menuTableModel;
    private DefaultTableModel cartTableModel;
    private DefaultTableModel orderTableModel;

    
    private JLabel cartTotalLabel;
    private JLabel discountLabel;
    private JLabel trackStatusLabel;
    private JLabel trackRiderLabel;
    private JLabel dashboardWelcomeLabel;
    private JLabel dashboardRoleLabel;
    private JLabel dashboardMessageLabel;
    private JLabel dashboardOrderHistoryLabel;
    private JLabel dashboardCurrentOrderLabel;
    private JTextField couponField;
    private JComboBox<String> paymentMethodBox;
    private JTextField orderSearchField;
    private String currentTrackOrderId;

    public CustomerPanel() {
        setLayout(cardLayout);
        add(buildAuthCard(),   "AUTH");
        add(buildBrowseCard(), "BROWSE");
        add(buildMenuCard(),   "MENU");
        add(buildCartCard(),   "CART");
        add(buildTrackCard(),  "TRACK");
        cardLayout.show(this, "AUTH");
    }

    
    
    
    private JPanel buildAuthCard() {
        JPanel root = darkPanel(new BorderLayout());

        JLabel title = styledLabel("Customer Login / Register", Font.BOLD, 20);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        root.add(title, BorderLayout.NORTH);

        JPanel form = darkPanel(new GridBagLayout());
        GridBagConstraints gbc = gbc();

        JTextField userField  = styledField(18);
        JPasswordField passField = new JPasswordField(18);
        styleField(passField);
        JTextField emailField = styledField(18);
        JTextField phoneField = styledField(18);
        JTextField addrField  = styledField(18);

        addRow(form, gbc, 0, "Username:",  userField);
        addRow(form, gbc, 1, "Password:",  passField);
        addRow(form, gbc, 2, "Email (register only):",  emailField);
        addRow(form, gbc, 3, "Phone (register only):", phoneField);
        addRow(form, gbc, 4, "Address (register only):", addrField);

        JLabel msg = styledLabel("", Font.PLAIN, 12);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        form.add(msg, gbc);

        root.add(form, BorderLayout.CENTER);

        JPanel btnPanel = darkPanel(new FlowLayout(FlowLayout.CENTER, 16, 12));
        JButton loginBtn    = accentButton("Login");
        JButton registerBtn = accentButton("Register");
        btnPanel.add(loginBtn);
        btnPanel.add(registerBtn);
        root.add(btnPanel, BorderLayout.SOUTH);

        
        loginBtn.addActionListener(e -> {
            String user = userField.getText().trim();
            String pass = new String(passField.getPassword());
            if (user.isEmpty() || pass.isEmpty()) { msg.setText("Please fill username and password."); return; }
            Customer c = authService.loginCustomer(user, pass);
            if (c == null) { msg.setText("Invalid username or password."); return; }
            loggedInCustomer = c;
            msg.setText("");
            refreshCustomerDashboard();
            refreshRestaurantTable("");
            cardLayout.show(this, "BROWSE");
        });

        
        registerBtn.addActionListener(e -> {
            String user  = userField.getText().trim();
            String pass  = new String(passField.getPassword());
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String addr  = addrField.getText().trim();
            if (user.isEmpty() || pass.isEmpty() || email.isEmpty() || phone.isEmpty() || addr.isEmpty()) {
                msg.setText("Please fill all fields for registration."); return;
            }
            try {
                Customer c = authService.registerCustomer(user, pass, email, phone, addr);
                loggedInCustomer = c;
                msg.setText(""); clearFields(userField, passField, emailField, phoneField, addrField);
                refreshCustomerDashboard();
                refreshRestaurantTable("");
                cardLayout.show(this, "BROWSE");
            } catch (IllegalArgumentException ex) {
                msg.setText(ex.getMessage());
            }
        });
        return root;
    }

    
    
    
    private JPanel buildBrowseCard() {
        JPanel root = darkPanel(new BorderLayout(0, 8));
        root.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JPanel topSection = darkPanel(new BorderLayout(0, 6));
        topSection.add(buildCustomerDashboardHeader(), BorderLayout.NORTH);

        
        JPanel toolbar = darkPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        orderSearchField = styledField(22);
        JComboBox<String> searchByBox = new JComboBox<>(new String[]{"Restaurant Name", "Location"});
        styleCombo(searchByBox);
        JButton searchBtn  = accentButton("Search");
        JButton sortRating = smallButton("Sort by Rating ↓");
        JButton sortName   = smallButton("Sort by Name ↑");
        JButton viewCart   = accentButton("Cart");
        JButton trackBtn   = smallButton("My Orders");
        JButton logoutBtn  = smallButton("Logout");
        toolbar.add(styledLabel("Search:", Font.PLAIN, 13));
        toolbar.add(orderSearchField);
        toolbar.add(styledLabel("By:", Font.PLAIN, 13));
        toolbar.add(searchByBox);
        toolbar.add(searchBtn);
        toolbar.add(sortRating);
        toolbar.add(sortName);
        toolbar.add(new JSeparator(JSeparator.VERTICAL));
        toolbar.add(viewCart);
        toolbar.add(trackBtn);
        toolbar.add(logoutBtn);
        topSection.add(toolbar, BorderLayout.SOUTH);
        root.add(topSection, BorderLayout.NORTH);

        
        String[] cols = {"Restaurant", "Cuisine", "Area", "Rating", "Hours", "Status"};
        restaurantTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(restaurantTableModel);
        table.setRowHeight(28);
        table.getColumnModel().getColumn(0).setPreferredWidth(180);
        table.getColumnModel().getColumn(3).setPreferredWidth(60);
        table.getColumnModel().getColumn(5).setPreferredWidth(70);

        JScrollPane scroll = new JScrollPane(table);
        styleScrollPane(scroll);
        root.add(scroll, BorderLayout.CENTER);

        JLabel hint = styledLabel("Double-click a restaurant to view its menu.", Font.ITALIC, 11);
        root.add(hint, BorderLayout.SOUTH);

        
        searchBtn.addActionListener(e ->
            refreshRestaurantTable(orderSearchField.getText(), (String) searchByBox.getSelectedItem()));
        orderSearchField.addActionListener(e ->
            refreshRestaurantTable(orderSearchField.getText(), (String) searchByBox.getSelectedItem()));
        sortRating.addActionListener(e -> populateRestaurantTable(restService.sortByRating()));
        sortName.addActionListener(e -> populateRestaurantTable(restService.sortByName()));
        viewCart.addActionListener(e -> { refreshCartTable(); cardLayout.show(this, "CART"); });
        trackBtn.addActionListener(e -> { refreshOrderTable(); cardLayout.show(this, "TRACK"); });
        logoutBtn.addActionListener(e -> {
            loggedInCustomer = null;
            dashboardWelcomeLabel.setText("Welcome");
            dashboardMessageLabel.setText("Please login to view your dashboard.");
            dashboardOrderHistoryLabel.setText("Order History: 0");
            dashboardCurrentOrderLabel.setText("Current Order: None");
            cardLayout.show(this, "AUTH");
        });

        
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row < 0) return;
                    String name = (String) restaurantTableModel.getValueAt(row, 0);
                    selectedRestaurant = restService.getAllRestaurants().stream()
                            .filter(r -> r.getName().equals(name))
                            .findFirst().orElse(null);
                    if (selectedRestaurant != null) {
                        refreshMenuTable();
                        cardLayout.show(CustomerPanel.this, "MENU");
                    }
                }
            }
        });
        return root;
    }

    
    
    
    private JPanel buildMenuCard() {
        JPanel root = darkPanel(new BorderLayout(0, 8));
        root.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        
        JLabel restNameLabel = styledLabel("", Font.BOLD, 18);
        restNameLabel.setName("restNameLabel");
        root.add(restNameLabel, BorderLayout.NORTH);

        
        String[] cols = {"Item", "Category", "Price (৳)", "Available", "Options"};
        menuTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(menuTableModel);
        table.setRowHeight(26);
        JScrollPane scroll = new JScrollPane(table);
        styleScrollPane(scroll);
        root.add(scroll, BorderLayout.CENTER);

        
        JPanel bottom = darkPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        qtySpinner.setPreferredSize(new Dimension(55, 28));
        JButton addToCart = accentButton("Add to Cart");
        JButton backBtn   = smallButton("Back");
        JButton viewCart  = accentButton("View Cart");
        bottom.add(styledLabel("Qty:", Font.PLAIN, 13));
        bottom.add(qtySpinner);
        bottom.add(addToCart);
        bottom.add(viewCart);
        bottom.add(backBtn);
        root.add(bottom, BorderLayout.SOUTH);

        
        backBtn.addActionListener(e -> cardLayout.show(this, "BROWSE"));
        viewCart.addActionListener(e -> { refreshCartTable(); cardLayout.show(this, "CART"); });

        addToCart.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Please select a menu item."); return; }
            String itemName = (String) menuTableModel.getValueAt(row, 0);
            com.fooddelivery.models.MenuItem item = selectedRestaurant.getMenu().stream()
                    .filter(i -> i.getName().equals(itemName))
                    .findFirst().orElse(null);
            if (item == null || !item.isAvailable()) {
                JOptionPane.showMessageDialog(this, "Item is not available."); return;
            }
            int qty = (int) qtySpinner.getValue();

            
            List<String> chosen = new ArrayList<>();
            if (!item.getOptions().isEmpty()) {
                String optStr = String.join(", ", item.getOptions());
                String input = JOptionPane.showInputDialog(this,
                        "Options: " + optStr + "\nType any options (comma-separated) or leave blank:");
                if (input != null && !input.isBlank()) {
                    for (String o : input.split(",")) chosen.add(o.trim());
                }
            }
            cartService.addToCart(loggedInCustomer, item, qty, chosen);
            JOptionPane.showMessageDialog(this,
                    qty + "x " + item.getName() + " added to cart.",
                    "Cart", JOptionPane.INFORMATION_MESSAGE);
        });

        
        root.setName("menuCard");
        return root;
    }

    
    
    
    private JPanel buildCartCard() {
        JPanel root = darkPanel(new BorderLayout(0, 8));
        root.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JLabel title = styledLabel("Your Cart", Font.BOLD, 18);
        root.add(title, BorderLayout.NORTH);

        
        String[] cols = {"Item", "Qty", "Unit Price (৳)", "Subtotal (৳)", "Options"};
        cartTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(cartTableModel);
        table.setRowHeight(26);
        JScrollPane scroll = new JScrollPane(table);
        styleScrollPane(scroll);
        root.add(scroll, BorderLayout.CENTER);

        
        JPanel southPanel = darkPanel(new BorderLayout(0, 6));
        southPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        
        JPanel totalsRow = darkPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        cartTotalLabel   = styledLabel("Total: ৳0.00", Font.BOLD, 14);
        discountLabel    = styledLabel("", Font.PLAIN, 12);
        couponField = styledField(14);
        JButton applyCoupon = smallButton("Apply Coupon");
        paymentMethodBox = new JComboBox<>(new String[]{"Cash on Delivery", "Card", "Mobile Banking"});
        styleCombo(paymentMethodBox);
        totalsRow.add(cartTotalLabel);
        totalsRow.add(styledLabel("|", Font.PLAIN, 13));
        totalsRow.add(styledLabel("Coupon:", Font.PLAIN, 13));
        totalsRow.add(couponField);
        totalsRow.add(applyCoupon);
        totalsRow.add(styledLabel("  Payment:", Font.PLAIN, 13));
        totalsRow.add(paymentMethodBox);
        southPanel.add(totalsRow, BorderLayout.NORTH);
        southPanel.add(discountLabel, BorderLayout.CENTER);

        
        JPanel btns = darkPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        JButton removeBtn   = smallButton("Remove Selected");
        JButton clearBtn    = smallButton("Clear Cart");
        JButton checkoutBtn = accentButton("Place Order");
        JButton backBtn     = smallButton("Back to Menu");
        btns.add(removeBtn); btns.add(clearBtn); btns.add(checkoutBtn); btns.add(backBtn);
        southPanel.add(btns, BorderLayout.SOUTH);

        root.add(southPanel, BorderLayout.SOUTH);

        
        backBtn.addActionListener(e -> cardLayout.show(this, "MENU"));

        removeBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select an item to remove."); return; }
            String name = (String) cartTableModel.getValueAt(row, 0);
            cartService.removeFromCart(loggedInCustomer, name);
            appliedCoupon = null; discountLabel.setText("");
            refreshCartTable();
        });

        clearBtn.addActionListener(e -> {
            cartService.clearCart(loggedInCustomer);
            appliedCoupon = null; discountLabel.setText("");
            refreshCartTable();
        });

        applyCoupon.addActionListener(e -> {
            if (selectedRestaurant == null) return;
            String code = couponField.getText().trim();
            Coupon c = couponService.validateCoupon(code, selectedRestaurant.getRestaurantId());
            if (c == null) {
                discountLabel.setText("  Invalid or expired coupon.");
            } else {
                appliedCoupon = c;
                double raw = cartService.getCartTotal(loggedInCustomer);
                discountLabel.setText("  " + couponService.getDiscountSummary(raw, c));
                refreshCartTable();
            }
        });

        checkoutBtn.addActionListener(e -> {
            if (loggedInCustomer == null) { cardLayout.show(this, "AUTH"); return; }
            if (cartService.isCartEmpty(loggedInCustomer)) {
                JOptionPane.showMessageDialog(this, "Your cart is empty!"); return;
            }
            if (selectedRestaurant == null) {
                JOptionPane.showMessageDialog(this, "Please select a restaurant first."); return;
            }
            double raw = cartService.getCartTotal(loggedInCustomer);
            double total = couponService.applyDiscount(raw, appliedCoupon);
            String couponCode = appliedCoupon != null ? appliedCoupon.getCode() : "";

            int confirm = JOptionPane.showConfirmDialog(this,
                    String.format("Confirm order from %s?%nTotal: ৳%.2f",
                            selectedRestaurant.getName(), total),
                    "Place Order", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            
            PaymentService.PaymentMethod method = switch (paymentMethodBox.getSelectedIndex()) {
                case 1 -> PaymentService.PaymentMethod.CARD;
                case 2 -> PaymentService.PaymentMethod.MOBILE_BANKING;
                default -> PaymentService.PaymentMethod.CASH_ON_DELIVERY;
            };
            String txnId = paymentService.processPayment(total, method, loggedInCustomer.getUserName());

            
            List<OrderItem> items = new ArrayList<>(loggedInCustomer.getCart());
            Order order = orderService.placeOrder(loggedInCustomer, selectedRestaurant,
                    items, total, couponCode);
            cartService.clearCart(loggedInCustomer);
            appliedCoupon = null;

            currentTrackOrderId = order.getOrderId();
            refreshCustomerDashboard();
            refreshCartTable();
            refreshOrderTable();

            JOptionPane.showMessageDialog(this,
                    "Order placed!\nOrder ID: " + order.getOrderId()
                            + "\nTxn: " + txnId
                            + "\n\nTracking your order...",
                    "Order Confirmed", JOptionPane.INFORMATION_MESSAGE);
            updateTrackPanel(order);
            cardLayout.show(this, "TRACK");
        });

        return root;
    }

    
    
    
    private JPanel buildTrackCard() {
        JPanel root = darkPanel(new BorderLayout(0, 8));
        root.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JLabel title = styledLabel("My Orders", Font.BOLD, 18);
        root.add(title, BorderLayout.NORTH);

        
        String[] cols = {"Order ID", "Restaurant", "Total (৳)", "Status", "Date"};
        orderTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(orderTableModel);
        table.setRowHeight(26);
        table.getColumnModel().getColumn(0).setPreferredWidth(130);
        table.getColumnModel().getColumn(3).setPreferredWidth(130);
        JScrollPane scroll = new JScrollPane(table);
        styleScrollPane(scroll);
        root.add(scroll, BorderLayout.CENTER);

        
        JPanel statusBox = darkPanel(new BorderLayout(0, 4));
        statusBox.setBorder(titledBorder("Order Status Tracker"));
        trackStatusLabel = styledLabel("Select an order and click Refresh Status.", Font.PLAIN, 13);
        trackRiderLabel  = styledLabel("", Font.PLAIN, 12);
        statusBox.add(trackStatusLabel, BorderLayout.CENTER);
        statusBox.add(trackRiderLabel, BorderLayout.SOUTH);

        JPanel bottomBar = darkPanel(new BorderLayout());
        bottomBar.add(statusBox, BorderLayout.CENTER);

        JPanel btns = darkPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        JButton refreshBtn = accentButton("Refresh Status");
        JButton deliveredBtn = smallButton("Delivered");
        JButton notDeliveredBtn = smallButton("Still Not Delivered");
        JButton complaintBtn = smallButton("Complaint");
        JButton backBtn    = smallButton("Browse");
        btns.add(refreshBtn);
        btns.add(deliveredBtn);
        btns.add(notDeliveredBtn);
        btns.add(complaintBtn);
        btns.add(backBtn);
        bottomBar.add(btns, BorderLayout.SOUTH);
        root.add(bottomBar, BorderLayout.SOUTH);

        
        backBtn.addActionListener(e -> cardLayout.show(this, "BROWSE"));

        refreshBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) currentTrackOrderId = (String) orderTableModel.getValueAt(row, 0);
            if (currentTrackOrderId == null) {
                trackStatusLabel.setText("No order selected."); return;
            }
            Order order = orderService.getOrderById(currentTrackOrderId);
            if (order != null) { updateTrackPanel(order); refreshOrderTable(); }
        });

        deliveredBtn.addActionListener(e -> {
            Order order = selectedTrackOrder(table);
            if (order == null) {
                return;
            }
            if (order.getStatus() == OrderStatus.DELIVERED) {
                JOptionPane.showMessageDialog(this, "Order is already marked as Delivered.");
                return;
            }
            if (order.getStatus() == OrderStatus.CANCELLED) {
                JOptionPane.showMessageDialog(this, "Cancelled orders cannot be marked as Delivered.");
                return;
            }

            orderService.updateStatus(order.getOrderId(), OrderStatus.DELIVERED);
            refreshOrderTable();
            updateTrackPanel(orderService.getOrderById(order.getOrderId()));
            JOptionPane.showMessageDialog(this, "Thanks. Order marked as Delivered.");
        });

        notDeliveredBtn.addActionListener(e -> {
            Order order = selectedTrackOrder(table);
            if (order == null) {
                return;
            }
            if (order.getStatus() == OrderStatus.DELIVERED) {
                JOptionPane.showMessageDialog(this,
                        "This order is already Delivered. Use Complaint if there is an issue.");
                return;
            }
            trackStatusLabel.setText("Order " + order.getOrderId() + " is still not delivered.");
            JOptionPane.showMessageDialog(this,
                    "Noted. The order is still pending delivery. You can file a complaint now.");
        });

        complaintBtn.addActionListener(e -> {
            Order order = selectedTrackOrder(table);
            if (order == null) {
                return;
            }
            String message = JOptionPane.showInputDialog(this,
                    "Write your complaint message:", "Complaint", JOptionPane.PLAIN_MESSAGE);
            if (message == null) {
                return;
            }
            if (message.isBlank()) {
                JOptionPane.showMessageDialog(this, "Complaint message cannot be empty.");
                return;
            }

                System.out.println("[CustomerPanel] Complaint received for "
                    + order.getOrderId() + ": " + message.trim());
            JOptionPane.showMessageDialog(this,
                    "Complaint received for order " + order.getOrderId() + ".");
        });

        return root;
    }

    
    
    
    private void refreshRestaurantTable(String query) {
        refreshRestaurantTable(query, "Restaurant Name");
    }

    private void refreshRestaurantTable(String query, String searchBy) {
        if (query == null || query.isBlank()) {
            populateRestaurantTable(restService.getAllRestaurants());
            return;
        }

        String q = query.toLowerCase().trim();
        boolean byLocation = "Location".equalsIgnoreCase(searchBy);
        List<Restaurant> list = searchService.searchRestaurants(query);
        list = list.stream()
                .filter(r -> byLocation
                ? r.getArea().toLowerCase().contains(q)
                : r.getName().toLowerCase().contains(q))
                .toList();
        populateRestaurantTable(list);
    }

    private void populateRestaurantTable(List<Restaurant> list) {
        restaurantTableModel.setRowCount(0);
        for (Restaurant r : list) {
            restaurantTableModel.addRow(new Object[]{
                    r.getName(), r.getCuisine(), r.getArea(),
                    String.format("%.1f", r.getRating()),
                    r.getOpeningHours(),
                    r.isOpen() ? "OPEN" : "CLOSED"
            });
        }
    }

    private void refreshMenuTable() {
        if (selectedRestaurant == null) return;
        menuTableModel.setRowCount(0);
        for (com.fooddelivery.models.MenuItem item : selectedRestaurant.getMenu()) {
            menuTableModel.addRow(new Object[]{
                    item.getName(), item.getCategory(),
                    String.format("%.2f", item.getPrice()),
                    item.isAvailable() ? "Yes" : "No",
                    String.join(", ", item.getOptions())
            });
        }
        
        for (Component comp : getComponents()) {
            if ("menuCard".equals(comp.getName()) && comp instanceof JPanel p) {
                for (Component c : p.getComponents()) {
                    if (c instanceof JLabel lbl && "restNameLabel".equals(lbl.getName())) {
                        lbl.setText("Menu: " + selectedRestaurant.getName()
                                + "  (" + selectedRestaurant.getCuisine() + ")");
                    }
                }
            }
        }
    }

    private void refreshCartTable() {
        if (loggedInCustomer == null) return;
        cartTableModel.setRowCount(0);
        for (OrderItem item : loggedInCustomer.getCart()) {
            cartTableModel.addRow(new Object[]{
                    item.getMenuItemName(), item.getQuantity(),
                    String.format("%.2f", item.getUnitPrice()),
                    String.format("%.2f", item.getSubtotal()),
                    String.join(", ", item.getChosenOptions())
            });
        }
        double raw = cartService.getCartTotal(loggedInCustomer);
        double total = couponService.applyDiscount(raw, appliedCoupon);
        cartTotalLabel.setText(String.format("Total: ৳%.2f", total));
    }

    private void refreshOrderTable() {
        if (loggedInCustomer == null) return;
        orderTableModel.setRowCount(0);
        for (Order o : orderService.getOrdersByCustomer(loggedInCustomer.getUserName())) {
            orderTableModel.addRow(new Object[]{
                    o.getOrderId(), o.getRestaurantName(),
                    String.format("%.2f", o.getTotalPrice()),
                    o.getStatus().toString(),
                    o.getTimestamp()
            });
        }
        refreshCustomerDashboard();
    }

    private void updateTrackPanel(Order order) {
        DeliveryService ds = new DeliveryService();
        trackStatusLabel.setText("Order " + order.getOrderId() + " -> " + order.getStatus());
        String tracking = ds.getTrackingMessage(order);
        trackRiderLabel.setText(tracking.contains("Rider:") ?
                tracking.substring(tracking.indexOf("Rider:")) : "");
    }

    private Order selectedTrackOrder(JTable table) {
        int row = table.getSelectedRow();
        if (row >= 0) {
            currentTrackOrderId = (String) orderTableModel.getValueAt(row, 0);
        }
        if (currentTrackOrderId == null) {
            JOptionPane.showMessageDialog(this, "Please select an order first.");
            return null;
        }
        Order order = orderService.getOrderById(currentTrackOrderId);
        if (order == null) {
            JOptionPane.showMessageDialog(this, "Order not found.");
            return null;
        }
        return order;
    }

    private JPanel buildCustomerDashboardHeader() {
        JPanel box = darkPanel(new GridLayout(0, 1, 0, 2));
        box.setBorder(titledBorder("Dashboard"));

        dashboardWelcomeLabel = styledLabel("Welcome", Font.BOLD, 14);
        dashboardRoleLabel = styledLabel("Role: Customer", Font.PLAIN, 12);
        dashboardMessageLabel = styledLabel("Use search to find restaurants and place orders.", Font.PLAIN, 12);
        dashboardOrderHistoryLabel = styledLabel("Order History: 0", Font.PLAIN, 12);
        dashboardCurrentOrderLabel = styledLabel("Current Order: None", Font.PLAIN, 12);

        box.add(dashboardWelcomeLabel);
        box.add(dashboardRoleLabel);
        box.add(dashboardMessageLabel);
        box.add(dashboardOrderHistoryLabel);
        box.add(dashboardCurrentOrderLabel);
        return box;
    }

    private void refreshCustomerDashboard() {
        if (dashboardWelcomeLabel == null) {
            return;
        }
        if (loggedInCustomer == null) {
            dashboardWelcomeLabel.setText("Welcome");
            dashboardRoleLabel.setText("Role: Customer");
            dashboardMessageLabel.setText("Please login to view your dashboard.");
            dashboardOrderHistoryLabel.setText("Order History: 0");
            dashboardCurrentOrderLabel.setText("Current Order: None");
            return;
        }

        List<Order> orders = orderService.getOrdersByCustomer(loggedInCustomer.getUserName());
        long activeCount = orders.stream()
                .filter(o -> o.getStatus() != OrderStatus.DELIVERED && o.getStatus() != OrderStatus.CANCELLED)
                .count();

        String currentOrderText = "Current Order: None";
        for (int i = orders.size() - 1; i >= 0; i--) {
            Order o = orders.get(i);
            if (o.getStatus() != OrderStatus.DELIVERED && o.getStatus() != OrderStatus.CANCELLED) {
                currentOrderText = "Current Order: " + o.getOrderId() + " (" + o.getStatus() + ")";
                break;
            }
        }

        dashboardWelcomeLabel.setText("Welcome, " + loggedInCustomer.getUserName());
        dashboardRoleLabel.setText("Role: Customer");
        dashboardMessageLabel.setText("You have " + activeCount + " active order(s). Track or update from My Orders.");
        dashboardOrderHistoryLabel.setText("Order History: " + orders.size());
        dashboardCurrentOrderLabel.setText(currentOrderText);
    }

    
    
    
    private JPanel darkPanel(LayoutManager layout) {
        return new JPanel(layout);
    }

    private JLabel styledLabel(String text, int style, int size) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", style, size));
        return l;
    }

    private JTextField styledField(int cols) {
        JTextField f = new JTextField(cols);
        styleField(f);
        return f;
    }

    private void styleField(JTextField f) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
    }

    private JButton accentButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton smallButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JTable styledTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        t.setShowGrid(true);
        return t;
    }

    private void styleScrollPane(JScrollPane sp) {
        sp.setBorder(BorderFactory.createEmptyBorder());
    }

    private void styleCombo(JComboBox<String> box) {
        box.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    }

    private TitledBorder titledBorder(String title) {
        TitledBorder b = BorderFactory.createTitledBorder(title);
        b.setTitleFont(new Font("Segoe UI", Font.BOLD, 12));
        return b;
    }

    private GridBagConstraints gbc() {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 10, 6, 10);
        g.anchor = GridBagConstraints.WEST;
        return g;
    }

    private void addRow(JPanel p, GridBagConstraints g, int row, String label, JComponent field) {
        g.gridx = 0; g.gridy = row; g.gridwidth = 1;
        p.add(styledLabel(label, Font.PLAIN, 13), g);
        g.gridx = 1;
        p.add(field, g);
    }

    private void clearFields(JTextField... fields) {
        for (JTextField f : fields) f.setText("");
    }
}
