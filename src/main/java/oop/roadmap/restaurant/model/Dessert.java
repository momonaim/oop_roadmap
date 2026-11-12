package oop.roadmap.restaurant.model;

import java.math.BigDecimal;

public class Dessert extends MenuItem {

    private final String details;
    private final BigDecimal price;

    public Dessert(String name, String details, BigDecimal price) {
        super(name);
        if (details == null || details.isBlank()) {
            throw new IllegalArgumentException("Dessert details are required");
        }
        if (price == null || price.signum() < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.details = details;
        this.price = price;
    }

    @Override
    public BigDecimal calculatePrice() {
        return price;
    }

    @Override
    public void display() {
        System.out.println(getName() + " (" + details + ") - " + calculatePrice() + " MAD");
    }
}
