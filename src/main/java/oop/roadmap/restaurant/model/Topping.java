package oop.roadmap.restaurant.model;

import java.math.BigDecimal;

public record Topping(String name, BigDecimal extraPrice) {
    public Topping {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Topping name is required");
        }
        if (extraPrice == null || extraPrice.signum() < 0) {
            throw new IllegalArgumentException("Topping price cannot be negative");
        }
    }
}
