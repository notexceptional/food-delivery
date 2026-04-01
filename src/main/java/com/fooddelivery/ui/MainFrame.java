package com.fooddelivery.ui;

import javax.swing.*;
import java.awt.*;

/**
 * The main application window.
 * Contains two tabs: Customer and Restaurant.
 */
public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("🍔 Food Delivery App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setMinimumSize(new Dimension(800, 550));
        setLocationRelativeTo(null);

        // ── Look and feel ──────────────────────────────────────────────────
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // ── Global colour scheme ───────────────────────────────────────────
        Color bg        = new Color(30, 30, 46);
        Color tabBg     = new Color(49, 50, 68);
        Color accent    = new Color(203, 166, 247);

        UIManager.put("TabbedPane.background",        bg);
        UIManager.put("TabbedPane.selected",          tabBg);
        UIManager.put("TabbedPane.foreground",        Color.WHITE);
        UIManager.put("TabbedPane.selectedForeground",accent);
        UIManager.put("Panel.background",             bg);
        UIManager.put("Label.foreground",             Color.WHITE);

        getContentPane().setBackground(bg);

        // ── Header banner ──────────────────────────────────────────────────
        JLabel header = new JLabel("🍔  Food Delivery", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 26));
        header.setForeground(accent);
        header.setOpaque(true);
        header.setBackground(new Color(20, 20, 34));
        header.setBorder(BorderFactory.createEmptyBorder(14, 0, 14, 0));

        // ── Tabs ───────────────────────────────────────────────────────────
        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabs.setBackground(bg);
        tabs.addTab("🧑  Customer",    new CustomerPanel());
        tabs.addTab("🏪  Restaurant",  new RestaurantPanel());

        // ── Layout ────────────────────────────────────────────────────────
        setLayout(new BorderLayout());
        add(header, BorderLayout.NORTH);
        add(tabs,   BorderLayout.CENTER);

        // ── Footer ────────────────────────────────────────────────────────
        JLabel footer = new JLabel(
                "  SOAP API: http://localhost:8888/FoodDeliveryService?wsdl",
                SwingConstants.LEFT);
        footer.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        footer.setForeground(new Color(127, 132, 156));
        footer.setOpaque(true);
        footer.setBackground(new Color(20, 20, 34));
        footer.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        add(footer, BorderLayout.SOUTH);
    }
}
