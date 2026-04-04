package com.fooddelivery.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import com.fooddelivery.models.Order;
import com.fooddelivery.models.OrderStatus;
import com.fooddelivery.models.Restaurant;
import com.fooddelivery.services.AuthService;
import com.fooddelivery.services.CouponService;
import com.fooddelivery.services.MenuService;
import com.fooddelivery.services.OrderService;





public class RestaurantPanel extends JPanel {

    
    private final AuthService   authService  = new AuthService();
    private final MenuService   menuService  = new MenuService();
    private final OrderService  orderService = new OrderService();
    private final CouponService couponService = new CouponService();

    
    private Restaurant loggedInRestaurant;

    
    private final CardLayout cardLayout = new CardLayout();

    
    private DefaultTableModel menuTableModel;
    private DefaultTableModel orderTableModel;

    
    private JLabel dashRestNameLabel;

    public RestaurantPanel() {
        setLayout(cardLayout);
        add(buildAuthCard(),      "AUTH");
        add(buildDashboardCard(), "DASHBOARD");
        cardLayout.show(this, "AUTH");
    }

    
    
    
    private JPanel buildAuthCard() {
        JPanel root = darkPanel(new BorderLayout());

        JLabel title = styledLabel("Restaurant Login / Register", Font.BOLD, 20);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        root.add(title, BorderLayout.NORTH);

        JPanel form = darkPanel(new GridBagLayout());
        GridBagConstraints gbc = gbc();

        JTextField userField    = styledField(18);
        JPasswordField passField = new JPasswordField(18); styleField(passField);
        JTextField nameField    = styledField(18);
        JTextField locationField = styledField(18);
        JTextField areaField    = styledField(18);
        JTextField cuisineField = styledField(18);
        JTextField emailField   = styledField(18);
        JTextField phoneField   = styledField(18);
        JTextField hoursField   = styledField(18);

        addRow(form, gbc, 0, "Owner Username:",  userField);
        addRow(form, gbc, 1, "Owner Password:",  passField);
        addRow(form, gbc, 2, "Restaurant Name (reg):",  nameField);
        addRow(form, gbc, 3, "Location (reg):",  locationField);
        addRow(form, gbc, 4, "Area (reg):",       areaField);
        addRow(form, gbc, 5, "Cuisine (reg):",    cuisineField);
        addRow(form, gbc, 6, "Email (reg):",      emailField);
        addRow(form, gbc, 7, "Phone (reg):",      phoneField);
        addRow(form, gbc, 8, "Opening Hours (reg):", hoursField);

        JLabel msg = styledLabel("", Font.PLAIN, 12);
        gbc.gridx = 0; gbc.gridy = 9; gbc.gridwidth = 2;
        form.add(msg, gbc);

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        root.add(scroll, BorderLayout.CENTER);

        JPanel btnPanel = darkPanel(new FlowLayout(FlowLayout.CENTER, 16, 12));
        JButton loginBtn    = accentButton("Login");
        JButton registerBtn = accentButton("Register Restaurant");
        btnPanel.add(loginBtn);
        btnPanel.add(registerBtn);
        root.add(btnPanel, BorderLayout.SOUTH);

        
        loginBtn.addActionListener(e -> {
            String user = userField.getText().trim();
            String pass = new String(passField.getPassword());
            if (user.isEmpty() || pass.isEmpty()) { msg.setText("Fill username and password."); return; }
            Restaurant r = authService.loginRestaurant(user, pass);
            if (r == null) { msg.setText("Invalid credentials."); return; }
            loggedInRestaurant = r;
            msg.setText("");
            refreshDashboard();
            cardLayout.show(this, "DASHBOARD");
        });

        
        registerBtn.addActionListener(e -> {
            String user  = userField.getText().trim();
            String pass  = new String(passField.getPassword());
            String rName = nameField.getText().trim();
            String loc   = locationField.getText().trim();
            String area  = areaField.getText().trim();
            String cui   = cuisineField.getText().trim();
            String em    = emailField.getText().trim();
            String ph    = phoneField.getText().trim();
            String hrs   = hoursField.getText().trim();
            if (user.isEmpty() || pass.isEmpty() || rName.isEmpty() || area.isEmpty() || cui.isEmpty()) {
                msg.setText("Fill all required fields (Username, Password, Name, Area, Cuisine).");
                return;
            }
            try {
                Restaurant r = authService.registerRestaurant(rName, loc, area,
                        ph, em, hrs, cui, user, pass);
                loggedInRestaurant = r;
                msg.setText("");
                refreshDashboard();
                cardLayout.show(this, "DASHBOARD");
            } catch (IllegalArgumentException ex) {
                msg.setText(ex.getMessage());
            }
        });

        return root;
    }

    
    
    
    private JPanel buildDashboardCard() {
        JPanel root = darkPanel(new BorderLayout(0, 0));

        
        JPanel topBar = darkPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        dashRestNameLabel = styledLabel("Dashboard", Font.BOLD, 16);
        JButton toggleOpenBtn = accentButton("Toggle Open/Close");
        JButton logoutBtn     = smallButton("Logout");
        topBar.add(dashRestNameLabel);
        topBar.add(Box.createHorizontalStrut(20));
        topBar.add(toggleOpenBtn);
        topBar.add(logoutBtn);
        root.add(topBar, BorderLayout.NORTH);

        
        JTabbedPane innerTabs = new JTabbedPane();
        innerTabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        innerTabs.addTab("Menu Management", buildMenuManagementTab());
        innerTabs.addTab("Orders",          buildOrdersTab());
        root.add(innerTabs, BorderLayout.CENTER);

        
        toggleOpenBtn.addActionListener(e -> {
            if (loggedInRestaurant == null) return;
            boolean nowOpen = !loggedInRestaurant.isOpen();
            loggedInRestaurant.setOpen(nowOpen);
            com.fooddelivery.storage.DataStore.getInstance().saveChanges();
            refreshDashboard();
            JOptionPane.showMessageDialog(this,
                    "Restaurant is now " + (nowOpen ? "OPEN" : "CLOSED"));
        });

        logoutBtn.addActionListener(e -> {
            loggedInRestaurant = null;
            cardLayout.show(this, "AUTH");
        });

        return root;
    }

    
    private JPanel buildMenuManagementTab() {
        JPanel root = darkPanel(new BorderLayout(0, 6));
        root.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        
        String[] cols = {"Item Name", "Category", "Price (৳)", "Available", "Stock", "Options"};
        menuTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(menuTableModel);
        table.setRowHeight(26);
        table.getColumnModel().getColumn(0).setPreferredWidth(160);
        JScrollPane scroll = new JScrollPane(table);
        styleScrollPane(scroll);
        root.add(scroll, BorderLayout.CENTER);

        
        JPanel addForm = darkPanel(new GridBagLayout());
        addForm.setBorder(titledBorder("Add Menu Item"));
        GridBagConstraints g = gbc();

        JTextField nameF  = styledField(14);
        JTextField descF  = styledField(14);
        JTextField catF   = styledField(10);
        JTextField priceF = styledField(7);
        JTextField stockF = styledField(5);
        JTextField optF   = styledField(14);

        addRow(addForm, g, 0, "Name:",        nameF);
        addRow(addForm, g, 1, "Description:", descF);
        addRow(addForm, g, 2, "Category:",    catF);
        addRow(addForm, g, 3, "Price (৳):",   priceF);
        addRow(addForm, g, 4, "Stock (-1=unlimited):", stockF);
        addRow(addForm, g, 5, "Options (comma-sep):", optF);

        JLabel addMsg = styledLabel("", Font.PLAIN, 11);
        g.gridx = 0; g.gridy = 6; g.gridwidth = 2; addForm.add(addMsg, g);

        
        JPanel formBtns = darkPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JButton addBtn     = accentButton("Add Item");
        JButton removeBtn  = smallButton("Remove Selected");
        JButton toggleBtn  = smallButton("Toggle Availability");
        JButton couponBtn  = smallButton("Add Coupon...");
        JButton refreshBtn = smallButton("Refresh");
        formBtns.add(addBtn); formBtns.add(removeBtn);
        formBtns.add(toggleBtn); formBtns.add(couponBtn); formBtns.add(refreshBtn);

        JPanel south = darkPanel(new BorderLayout());
        south.add(addForm,   BorderLayout.CENTER);
        south.add(formBtns,  BorderLayout.SOUTH);
        root.add(south, BorderLayout.SOUTH);

        
        refreshBtn.addActionListener(e -> refreshMenuTable());

        addBtn.addActionListener(e -> {
            if (loggedInRestaurant == null) return;
            String n = nameF.getText().trim();
            String d = descF.getText().trim();
            String c = catF.getText().trim();
            String p = priceF.getText().trim();
            String s = stockF.getText().trim();
            String o = optF.getText().trim();
            if (n.isEmpty() || p.isEmpty()) {
                addMsg.setText("Name and Price are required."); return;
            }
            try {
                double price = Double.parseDouble(p);
                int stock = s.isEmpty() ? -1 : Integer.parseInt(s);
                com.fooddelivery.models.MenuItem item = new com.fooddelivery.models.MenuItem(n, d, c.isEmpty() ? "General" : c, price);
                item.setQuantity(stock);
                if (!o.isEmpty()) {
                    for (String opt : o.split(",")) item.getOptions().add(opt.trim());
                }
                menuService.addItem(loggedInRestaurant, item);
                clearFields(nameF, descF, catF, priceF, stockF, optF);
                addMsg.setText("Item '" + n + "' added.");
                refreshMenuTable();
            } catch (NumberFormatException ex) {
                addMsg.setText("Invalid price or stock value.");
            }
        });

        removeBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select an item to remove."); return; }
            String itemName = (String) menuTableModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Remove '" + itemName + "'?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                menuService.removeItem(loggedInRestaurant, itemName);
                addMsg.setText("Item removed.");
                refreshMenuTable();
            }
        });

        toggleBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select an item to toggle."); return; }
            String itemName = (String) menuTableModel.getValueAt(row, 0);
            menuService.toggleAvailability(loggedInRestaurant, itemName);
            addMsg.setText("'" + itemName + "' availability toggled.");
            refreshMenuTable();
        });

        couponBtn.addActionListener(e -> {
            if (loggedInRestaurant == null) return;
            JTextField codeF    = styledField(12);
            JTextField discountF = styledField(6);
            JPanel couponForm = darkPanel(new GridBagLayout());
            GridBagConstraints gc = gbc();
            addRow(couponForm, gc, 0, "Coupon Code:", codeF);
            addRow(couponForm, gc, 1, "Discount %:", discountF);
            int res = JOptionPane.showConfirmDialog(this, couponForm,
                    "Add Coupon for " + loggedInRestaurant.getName(),
                    JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                try {
                    String code = codeF.getText().trim().toUpperCase();
                    int pct  = Integer.parseInt(discountF.getText().trim());
                    couponService.createCoupon(code, pct, loggedInRestaurant.getRestaurantId());
                    JOptionPane.showMessageDialog(this, "Coupon '" + code + "' created.");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid discount value.");
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                }
            }
        });

        return root;
    }

    
    private JPanel buildOrdersTab() {
        JPanel root = darkPanel(new BorderLayout(0, 6));
        root.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        String[] cols = {"Order ID", "Customer", "Total (৳)", "Status", "Coupon", "Date"};
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

        
        JPanel controls = darkPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        JComboBox<OrderStatus> statusBox = new JComboBox<>(OrderStatus.values());
        statusBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JButton updateBtn  = accentButton("Update Status");
        JButton refreshBtn = smallButton("Refresh");
        controls.add(styledLabel("New Status:", Font.PLAIN, 13));
        controls.add(statusBox);
        controls.add(updateBtn);
        controls.add(refreshBtn);
        root.add(controls, BorderLayout.SOUTH);

        
        refreshBtn.addActionListener(e -> refreshOrderTable());

        updateBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select an order first."); return; }
            String orderId     = (String) orderTableModel.getValueAt(row, 0);
            OrderStatus status = (OrderStatus) statusBox.getSelectedItem();
            orderService.updateStatus(orderId, status);
            refreshOrderTable();
            JOptionPane.showMessageDialog(this,
                    "Order " + orderId + " updated to: " + status + ".");
        });

        return root;
    }

    
    
    
    private void refreshDashboard() {
        if (loggedInRestaurant == null) return;
        dashRestNameLabel.setText(loggedInRestaurant.getName()
                + "  (" + loggedInRestaurant.getArea() + ")  "
            + (loggedInRestaurant.isOpen() ? "OPEN" : "CLOSED"));

        refreshMenuTable();
        refreshOrderTable();
    }

    private void refreshMenuTable() {
        if (loggedInRestaurant == null || menuTableModel == null) return;
        menuTableModel.setRowCount(0);
        for (com.fooddelivery.models.MenuItem item : loggedInRestaurant.getMenu()) {
            menuTableModel.addRow(new Object[]{
                    item.getName(),
                    item.getCategory(),
                    String.format("%.2f", item.getPrice()),
                    item.isAvailable() ? "Available" : "Unavailable",
                    item.getQuantity() == -1 ? "unlimited" : String.valueOf(item.getQuantity()),
                    String.join(", ", item.getOptions())
            });
        }
    }

    private void refreshOrderTable() {
        if (loggedInRestaurant == null || orderTableModel == null) return;
        orderTableModel.setRowCount(0);
        for (Order o : orderService.getOrdersByRestaurant(loggedInRestaurant.getRestaurantId())) {
            orderTableModel.addRow(new Object[]{
                    o.getOrderId(),
                    o.getCustomerId(),
                    String.format("%.2f", o.getTotalPrice()),
                    o.getStatus().toString(),
                    o.getCouponCode() != null ? o.getCouponCode() : "-",
                    o.getTimestamp()
            });
        }
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
        JTextField f = new JTextField(cols); styleField(f); return f;
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
        return t;
    }

    private void styleScrollPane(JScrollPane sp) {
        sp.setBorder(BorderFactory.createEmptyBorder());
    }

    private TitledBorder titledBorder(String title) {
        TitledBorder b = BorderFactory.createTitledBorder(title);
        b.setTitleFont(new Font("Segoe UI", Font.BOLD, 12));
        return b;
    }

    private GridBagConstraints gbc() {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 8, 5, 8);
        g.anchor = GridBagConstraints.WEST;
        return g;
    }

    private void addRow(JPanel p, GridBagConstraints g, int row, String label, JComponent field) {
        g.gridx = 0; g.gridy = row; g.gridwidth = 1;
        p.add(styledLabel(label, Font.PLAIN, 13), g);
        g.gridx = 1; p.add(field, g);
    }

    private void clearFields(JTextField... fields) {
        for (JTextField f : fields) f.setText("");
    }
}
