package com.fooddelivery.services;

public class PaymentService {

    public enum PaymentMethod {
        CASH_ON_DELIVERY,
        CARD,
        MOBILE_BANKING
    }

    public String processPayment(double amount, PaymentMethod method, String customerName) {

        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {
        }

        String txnId = "TXN-" + System.currentTimeMillis();
        System.out.printf("[PaymentService] Payment of ৳%.2f via %s for %s → %s%n",
                amount, method, customerName, txnId);
        return txnId;
    }

    public String generateReceipt(String customerName, double amount,
            PaymentMethod method, String txnId) {
        return String.format(
                "=== Payment Receipt ===%n"
                + "Customer : %s%n"
                + "Amount   : ৳%.2f%n"
                + "Method   : %s%n"
                + "Txn ID   : %s%n"
                + "Status   : SUCCESS%n"
                + "======================",
                customerName, amount, method, txnId);
    }
}
