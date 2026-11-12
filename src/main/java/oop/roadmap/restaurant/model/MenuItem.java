package oop.roadmap.restaurant.model;

import java.math.BigDecimal;

public abstract class MenuItem {

    private final String name;

    protected MenuItem(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract BigDecimal calculatePrice();

    public void display() {
        System.out.println(name + " - " + calculatePrice() + " MAD");
    }
}
