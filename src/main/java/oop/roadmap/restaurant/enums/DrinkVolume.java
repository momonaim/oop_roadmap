package oop.roadmap.restaurant.enums;

import java.math.BigDecimal;

public enum DrinkVolume {
    ML_250(new BigDecimal("8.00")),
    ML_330(new BigDecimal("10.00")),
    ML_500(new BigDecimal("14.00")),
    ML_1000(new BigDecimal("22.00"));

    private final BigDecimal price;

    DrinkVolume(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
