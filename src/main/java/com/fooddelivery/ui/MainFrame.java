package com.fooddelivery.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;





public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Food Delivery App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setMinimumSize(new Dimension(800, 550));
        setLocationRelativeTo(null);

        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        
        JLabel header = new JLabel("Food Delivery", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 26));
        header.setBorder(BorderFactory.createEmptyBorder(14, 0, 14, 0));

        
        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabs.addTab("Customer",    new CustomerPanel());
        tabs.addTab("Restaurant",  new RestaurantPanel());

        
        setLayout(new BorderLayout());
        add(header, BorderLayout.NORTH);
        add(tabs,   BorderLayout.CENTER);

        
        JLabel footer = new JLabel(
                "  SOAP API: http://localhost:8888/FoodDeliveryService?wsdl",
                SwingConstants.LEFT);
        footer.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        footer.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        add(footer, BorderLayout.SOUTH);
    }
}
