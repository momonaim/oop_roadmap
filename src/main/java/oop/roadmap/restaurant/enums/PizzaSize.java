package oop.roadmap.restaurant.enums;

import java.math.BigDecimal;

public enum PizzaSize {
    SMALL(new BigDecimal("35.00")),
    MEDIUM(new BigDecimal("50.00")),
    LARGE(new BigDecimal("68.00"));

    private final BigDecimal basePrice;

    PizzaSize(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }
}
