package oop.roadmap.restaurant.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class Combo extends MenuItem {

    private final List<MenuItem> items;
    private final BigDecimal discountRate;

    public Combo(String name, List<MenuItem> items, BigDecimal discountRate) {
        super(name);
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("A combo must contain items");
        }
        if (discountRate == null
                || discountRate.compareTo(BigDecimal.ZERO) < 0
                || discountRate.compareTo(new BigDecimal("1.00")) > 0) {
            throw new IllegalArgumentException("Discount must be between 0 and 1");
        }
        this.items = List.copyOf(items);
        this.discountRate = discountRate;
    }

    @Override
    public BigDecimal calculatePrice() {
        BigDecimal subtotal = items.stream()
                .map(MenuItem::calculatePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return subtotal.subtract(subtotal.multiply(discountRate))
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public void display() {
        System.out.println("Combo: " + getName());
        items.forEach(MenuItem::display);
        System.out.println("Discount: " + discountRate.multiply(new BigDecimal("100")).toPlainString() + "%");
        System.out.println("Combo total: " + calculatePrice() + " MAD");
    }
}
