package oop.roadmap.restaurant.payment;

import java.math.BigDecimal;

public class WalletPayment implements PaymentMethod {

    private final String walletAccountId;

    public WalletPayment(String walletAccountId) {
        if (walletAccountId == null || walletAccountId.isBlank()) {
            throw new IllegalArgumentException("Wallet account is required");
        }
        this.walletAccountId = walletAccountId;
    }

    @Override
    public PaymentResult processPayment(BigDecimal amount) {
        if (amount.signum() <= 0) {
            return new PaymentResult(false, "Invalid payment amount");
        }
        return new PaymentResult(true, "Wallet payment accepted");
    }
}
