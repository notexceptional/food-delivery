package com.fooddelivery.services;

/**
 * Simulates payment processing.
 * In a real system this would integrate with a payment gateway (bKash, Stripe, etc.)
 */
public class PaymentService {

    public enum PaymentMethod {
        CASH_ON_DELIVERY,
        CARD,
        MOBILE_BANKING
    }

    /**
     * Simulate processing a payment.
     * @param amount        total amount to charge
     * @param method        payment method chosen
     * @param customerName  name of the customer
     * @return a payment confirmation string / transaction ID
     */
    public String processPayment(double amount, PaymentMethod method, String customerName) {
        // Simulate a small processing delay
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}

        String txnId = "TXN-" + System.currentTimeMillis();
        System.out.printf("[PaymentService] Payment of ৳%.2f via %s for %s → %s%n",
                amount, method, customerName, txnId);
        return txnId;
    }

    /** Returns a user-friendly receipt string. */
    public String generateReceipt(String customerName, double amount,
                                   PaymentMethod method, String txnId) {
        return String.format(
                "=== Payment Receipt ===%n" +
                "Customer : %s%n" +
                "Amount   : ৳%.2f%n" +
                "Method   : %s%n" +
                "Txn ID   : %s%n" +
                "Status   : SUCCESS%n" +
                "======================",
                customerName, amount, method, txnId);
    }
}
