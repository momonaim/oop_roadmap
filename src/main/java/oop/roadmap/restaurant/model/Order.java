package oop.roadmap.restaurant.model;

import oop.roadmap.restaurant.payment.PaymentMethod;
import oop.roadmap.restaurant.payment.PaymentResult;

import java.math.BigDecimal;
import java.util.List;

public class Order {

    private final String orderId;
    private final List<MenuItem> items;
    private PaymentResult paymentResult;

    public Order(String orderId, List<MenuItem> items) {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("Order ID is required");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("An order must contain items");
        }
        this.orderId = orderId;
        this.items = List.copyOf(items);
    }

    public BigDecimal calculateTotal() {
        return items.stream()
                .map(MenuItem::calculatePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void displayOrder() {
        System.out.println("Order: " + orderId);
        items.forEach(MenuItem::display);
        System.out.println("Order total: " + calculateTotal() + " MAD");
    }

    public PaymentResult requestPayment(PaymentMethod paymentMethod) {
        if (paymentMethod == null) {
            throw new IllegalArgumentException("Payment method is required");
        }
        paymentResult = paymentMethod.processPayment(calculateTotal());
        return paymentResult;
    }

    public boolean succeeded() {
        return paymentResult != null && paymentResult.succeeded();
    }
}
