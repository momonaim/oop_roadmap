package oop.roadmap.restaurant.payment;

import java.math.BigDecimal;

public interface PaymentMethod {
    PaymentResult processPayment(BigDecimal amount);
}
