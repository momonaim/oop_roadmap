package oop.roadmap.restaurant.payment;

import java.math.BigDecimal;

public class CardPayment implements PaymentMethod {

    private final String cardToken;

    public CardPayment(String cardToken) {
        if (cardToken == null || cardToken.isBlank()) {
            throw new IllegalArgumentException("A payment token is required");
        }
        this.cardToken = cardToken;
    }

    @Override
    public PaymentResult processPayment(BigDecimal amount) {
        if (amount.signum() <= 0) {
            return new PaymentResult(false, "Invalid payment amount");
        }
        return new PaymentResult(true, "Card payment accepted");
    }
}
