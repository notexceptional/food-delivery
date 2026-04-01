package com.fooddelivery.ui;

import com.fooddelivery.models.*;
import com.fooddelivery.services.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Customer-side UI panel with a CardLayout.
 * Cards: AUTH → BROWSE → MENU → CART → TRACK
 */
public class CustomerPanel extends JPanel {

    // ── Colours ───────────────────────────────────────────────────────────
    private static final Color BG      = new Color(30, 30, 46);
    private static final Color CARD_BG = new Color(49, 50, 68);
    private static final Color ACCENT  = new Color(203, 166, 247);
    private static final Color GREEN   = new Color(166, 227, 161);
    private static final Color RED     = new Color(243, 139, 168);
    private static final Color FG      = Color.WHITE;

    // ── Services ──────────────────────────────────────────────────────────
    private final AuthService      authService    = new AuthService();
    private final RestaurantService restService   = new RestaurantService();
    private final SearchService    searchService  = new SearchService();
    private final CartService      cartService    = new CartService();
    private final CouponService    couponService  = new CouponService();
    private final PaymentService   paymentService = new PaymentService();
    private final OrderService     orderService   = new OrderService();

    // ── State ─────────────────────────────────────────────────────────────
    private Customer        loggedInCustomer;
    private Restaurant      selectedRestaurant;
    private Coupon          appliedCoupon;

    // ── Card navigation ───────────────────────────────────────────────────
    private final CardLayout cardLayout = new CardLayout();

    // ── Table models (reused across refreshes) ────────────────────────────
    private DefaultTableModel restaurantTableModel;
    private DefaultTableModel menuTableModel;
    private DefaultTableModel cartTableModel;
    private DefaultTableModel orderTableModel;

    // ── Labels updated dynamically ────────────────────────────────────────
    private JLabel cartTotalLabel;
    private JLabel discountLabel;
    private JLabel trackStatusLabel;
    private JLabel trackRiderLabel;
    private JTextField couponField;
    private JComboBox<String> paymentMethodBox;
    private JTextField orderSearchField;
    private String currentTrackOrderId;

    public CustomerPanel() {
        setLayout(cardLayout);
        setBackground(BG);
        add(buildAuthCard(),   "AUTH");
        add(buildBrowseCard(), "BROWSE");
        add(buildMenuCard(),   "MENU");
        add(buildCartCard(),   "CART");
        add(buildTrackCard(),  "TRACK");
        cardLayout.show(this, "AUTH");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // CARD 1 — AUTH (Login / Register)
    // ═══════════════════════════════════════════════════════════════════════
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

        JLabel msg = styledLabel("", Font.PLAIN, 12); msg.setForeground(RED);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        form.add(msg, gbc);

        root.add(form, BorderLayout.CENTER);

        JPanel btnPanel = darkPanel(new FlowLayout(FlowLayout.CENTER, 16, 12));
        JButton loginBtn    = accentButton("Login");
        JButton registerBtn = accentButton("Register");
        btnPanel.add(loginBtn);
        btnPanel.add(registerBtn);
        root.add(btnPanel, BorderLayout.SOUTH);

        // ── Login action ──────────────────────────────────────────────────
        loginBtn.addActionListener(e -> {
            String user = userField.getText().trim();
            String pass = new String(passField.getPassword());
            if (user.isEmpty() || pass.isEmpty()) { msg.setText("Please fill username and password."); return; }
            Customer c = authService.loginCustomer(user, pass);
            if (c == null) { msg.setText("Invalid username or password."); return; }
            loggedInCustomer = c;
            msg.setText("");
            refreshRestaurantTable("");
            cardLayout.show(this, "BROWSE");
        });

        // ── Register action ───────────────────────────────────────────────
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
                refreshRestaurantTable("");
                cardLayout.show(this, "BROWSE");
            } catch (IllegalArgumentException ex) {
                msg.setText(ex.getMessage());
            }
        });
        return root;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // CARD 2 — BROWSE restaurants
    // ═══════════════════════════════════════════════════════════════════════
    private JPanel buildBrowseCard() {
        JPanel root = darkPanel(new BorderLayout(0, 8));
        root.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        // ── Top toolbar ───────────────────────────────────────────────────
        JPanel toolbar = darkPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        orderSearchField = styledField(22);
        JButton searchBtn  = accentButton("Search");
        JButton sortRating = smallButton("Sort by Rating ↓");
        JButton sortName   = smallButton("Sort by Name ↑");
        JButton viewCart   = accentButton("🛒 Cart");
        JButton trackBtn   = smallButton("📦 My Orders");
        JButton logoutBtn  = smallButton("Logout");
        toolbar.add(styledLabel("Search:", Font.PLAIN, 13));
        toolbar.add(orderSearchField);
        toolbar.add(searchBtn);
        toolbar.add(sortRating);
        toolbar.add(sortName);
        toolbar.add(new JSeparator(JSeparator.VERTICAL));
        toolbar.add(viewCart);
        toolbar.add(trackBtn);
        toolbar.add(logoutBtn);
        root.add(toolbar, BorderLayout.NORTH);

        // ── Restaurant table ───────────────────────────────────────────────
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
        hint.setForeground(new Color(127, 132, 156));
        root.add(hint, BorderLayout.SOUTH);

        // ── Actions ────────────────────────────────────────────────────────
        searchBtn.addActionListener(e -> refreshRestaurantTable(orderSearchField.getText()));
        orderSearchField.addActionListener(e -> refreshRestaurantTable(orderSearchField.getText()));
        sortRating.addActionListener(e -> populateRestaurantTable(restService.sortByRating()));
        sortName.addActionListener(e -> populateRestaurantTable(restService.sortByName()));
        viewCart.addActionListener(e -> { refreshCartTable(); cardLayout.show(this, "CART"); });
        trackBtn.addActionListener(e -> { refreshOrderTable(); cardLayout.show(this, "TRACK"); });
        logoutBtn.addActionListener(e -> { loggedInCustomer = null; cardLayout.show(this, "AUTH"); });

        // Double-click → open menu
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

    // ═══════════════════════════════════════════════════════════════════════
    // CARD 3 — MENU of selected restaurant
    // ═══════════════════════════════════════════════════════════════════════
    private JPanel buildMenuCard() {
        JPanel root = darkPanel(new BorderLayout(0, 8));
        root.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        // ── Title will be set on refresh via a placeholder label ───────────
        JLabel restNameLabel = styledLabel("", Font.BOLD, 18);
        restNameLabel.setName("restNameLabel");
        root.add(restNameLabel, BorderLayout.NORTH);

        // ── Menu table ─────────────────────────────────────────────────────
        String[] cols = {"Item", "Category", "Price (৳)", "Available", "Options"};
        menuTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(menuTableModel);
        table.setRowHeight(26);
        JScrollPane scroll = new JScrollPane(table);
        styleScrollPane(scroll);
        root.add(scroll, BorderLayout.CENTER);

        // ── Bottom controls ─────────────────────────────────────────────────
        JPanel bottom = darkPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        qtySpinner.setPreferredSize(new Dimension(55, 28));
        JButton addToCart = accentButton("➕ Add to Cart");
        JButton backBtn   = smallButton("← Back");
        JButton viewCart  = accentButton("🛒 View Cart");
        bottom.add(styledLabel("Qty:", Font.PLAIN, 13));
        bottom.add(qtySpinner);
        bottom.add(addToCart);
        bottom.add(viewCart);
        bottom.add(backBtn);
        root.add(bottom, BorderLayout.SOUTH);

        // ── Actions ────────────────────────────────────────────────────────
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

            // Ask for customization options if available
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
                    qty + "x " + item.getName() + " added to cart! ✓",
                    "Cart", JOptionPane.INFORMATION_MESSAGE);
        });

        // Attach restoNameLabel refresh via a workaround — called from refreshMenuTable()
        root.setName("menuCard");
        return root;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // CARD 4 — CART + Checkout
    // ═══════════════════════════════════════════════════════════════════════
    private JPanel buildCartCard() {
        JPanel root = darkPanel(new BorderLayout(0, 8));
        root.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JLabel title = styledLabel("Your Cart", Font.BOLD, 18);
        root.add(title, BorderLayout.NORTH);

        // ── Cart table ─────────────────────────────────────────────────────
        String[] cols = {"Item", "Qty", "Unit Price (৳)", "Subtotal (৳)", "Options"};
        cartTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(cartTableModel);
        table.setRowHeight(26);
        JScrollPane scroll = new JScrollPane(table);
        styleScrollPane(scroll);
        root.add(scroll, BorderLayout.CENTER);

        // ── Bottom section ─────────────────────────────────────────────────
        JPanel southPanel = darkPanel(new BorderLayout(0, 6));
        southPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        // Totals + coupon row
        JPanel totalsRow = darkPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        cartTotalLabel   = styledLabel("Total: ৳0.00", Font.BOLD, 14);
        discountLabel    = styledLabel("", Font.PLAIN, 12);
        discountLabel.setForeground(GREEN);
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

        // Action buttons
        JPanel btns = darkPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        JButton removeBtn   = smallButton("Remove Selected");
        JButton clearBtn    = smallButton("Clear Cart");
        JButton checkoutBtn = accentButton("✅ Place Order");
        JButton backBtn     = smallButton("← Back to Menu");
        btns.add(removeBtn); btns.add(clearBtn); btns.add(checkoutBtn); btns.add(backBtn);
        southPanel.add(btns, BorderLayout.SOUTH);

        root.add(southPanel, BorderLayout.SOUTH);

        // ── Actions ────────────────────────────────────────────────────────
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
                discountLabel.setForeground(RED);
                discountLabel.setText("  Invalid or expired coupon.");
            } else {
                appliedCoupon = c;
                double raw = cartService.getCartTotal(loggedInCustomer);
                discountLabel.setForeground(GREEN);
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

            // Payment
            PaymentService.PaymentMethod method = switch (paymentMethodBox.getSelectedIndex()) {
                case 1 -> PaymentService.PaymentMethod.CARD;
                case 2 -> PaymentService.PaymentMethod.MOBILE_BANKING;
                default -> PaymentService.PaymentMethod.CASH_ON_DELIVERY;
            };
            String txnId = paymentService.processPayment(total, method, loggedInCustomer.getUserName());

            // Place order
            List<OrderItem> items = new ArrayList<>(loggedInCustomer.getCart());
            Order order = orderService.placeOrder(loggedInCustomer, selectedRestaurant,
                    items, total, couponCode);
            cartService.clearCart(loggedInCustomer);
            appliedCoupon = null;

            currentTrackOrderId = order.getOrderId();
            refreshCartTable();
            refreshOrderTable();

            JOptionPane.showMessageDialog(this,
                    "Order placed! 🎉\nOrder ID: " + order.getOrderId()
                            + "\nTxn: " + txnId
                            + "\n\nTracking your order...",
                    "Order Confirmed", JOptionPane.INFORMATION_MESSAGE);
            updateTrackPanel(order);
            cardLayout.show(this, "TRACK");
        });

        return root;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // CARD 5 — TRACK orders
    // ═══════════════════════════════════════════════════════════════════════
    private JPanel buildTrackCard() {
        JPanel root = darkPanel(new BorderLayout(0, 8));
        root.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JLabel title = styledLabel("My Orders", Font.BOLD, 18);
        root.add(title, BorderLayout.NORTH);

        // ── Order history table ────────────────────────────────────────────
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

        // ── Status display ─────────────────────────────────────────────────
        JPanel statusBox = darkPanel(new BorderLayout(0, 4));
        statusBox.setBorder(titledBorder("Order Status Tracker"));
        trackStatusLabel = styledLabel("Select an order and click Refresh Status.", Font.PLAIN, 13);
        trackRiderLabel  = styledLabel("", Font.PLAIN, 12);
        trackRiderLabel.setForeground(GREEN);
        statusBox.add(trackStatusLabel, BorderLayout.CENTER);
        statusBox.add(trackRiderLabel, BorderLayout.SOUTH);

        JPanel bottomBar = darkPanel(new BorderLayout());
        bottomBar.add(statusBox, BorderLayout.CENTER);

        JPanel btns = darkPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        JButton refreshBtn = accentButton("🔄 Refresh Status");
        JButton backBtn    = smallButton("← Browse");
        btns.add(refreshBtn); btns.add(backBtn);
        bottomBar.add(btns, BorderLayout.SOUTH);
        root.add(bottomBar, BorderLayout.SOUTH);

        // ── Actions ────────────────────────────────────────────────────────
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

        return root;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Refresh helpers
    // ═══════════════════════════════════════════════════════════════════════
    private void refreshRestaurantTable(String query) {
        List<Restaurant> list = query == null || query.isBlank()
                ? restService.getAllRestaurants()
                : searchService.searchRestaurants(query);
        populateRestaurantTable(list);
    }

    private void populateRestaurantTable(List<Restaurant> list) {
        restaurantTableModel.setRowCount(0);
        for (Restaurant r : list) {
            restaurantTableModel.addRow(new Object[]{
                    r.getName(), r.getCuisine(), r.getArea(),
                    String.format("⭐ %.1f", r.getRating()),
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
                    item.isAvailable() ? "✓" : "✗",
                    String.join(", ", item.getOptions())
            });
        }
        // Update the menu card header label
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
    }

    private void updateTrackPanel(Order order) {
        DeliveryService ds = new DeliveryService();
        trackStatusLabel.setText("Order " + order.getOrderId() + " → " + order.getStatus());
        String tracking = ds.getTrackingMessage(order);
        trackRiderLabel.setText(tracking.contains("Rider:") ?
                tracking.substring(tracking.indexOf("Rider:")) : "");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // UI helpers
    // ═══════════════════════════════════════════════════════════════════════
    private JPanel darkPanel(LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setBackground(BG);
        return p;
    }

    private JLabel styledLabel(String text, int style, int size) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", style, size));
        l.setForeground(FG);
        return l;
    }

    private JTextField styledField(int cols) {
        JTextField f = new JTextField(cols);
        styleField(f);
        return f;
    }

    private void styleField(JTextField f) {
        f.setBackground(CARD_BG);
        f.setForeground(FG);
        f.setCaretColor(FG);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(88, 91, 112)),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)));
    }

    private JButton accentButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(ACCENT);
        b.setForeground(new Color(30, 30, 46));
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton smallButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(CARD_BG);
        b.setForeground(FG);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JTable styledTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setBackground(CARD_BG);
        t.setForeground(FG);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.getTableHeader().setBackground(new Color(69, 71, 90));
        t.getTableHeader().setForeground(ACCENT);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        t.setGridColor(new Color(69, 71, 90));
        t.setSelectionBackground(new Color(88, 91, 112));
        t.setSelectionForeground(FG);
        t.setShowGrid(true);
        return t;
    }

    private void styleScrollPane(JScrollPane sp) {
        sp.getViewport().setBackground(CARD_BG);
        sp.setBorder(BorderFactory.createLineBorder(new Color(69, 71, 90)));
    }

    private void styleCombo(JComboBox<String> box) {
        box.setBackground(CARD_BG);
        box.setForeground(FG);
        box.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    }

    private TitledBorder titledBorder(String title) {
        TitledBorder b = BorderFactory.createTitledBorder(title);
        b.setTitleColor(ACCENT);
        b.setTitleFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBorder(BorderFactory.createLineBorder(new Color(88, 91, 112)));
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
