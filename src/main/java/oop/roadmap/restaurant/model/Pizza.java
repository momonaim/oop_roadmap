package oop.roadmap.restaurant.model;

import oop.roadmap.restaurant.enums.PizzaSize;

import java.math.BigDecimal;
import java.util.List;

public class Pizza extends MenuItem {

    private final PizzaSize size;
    private final List<Topping> toppings;

    public Pizza(String name, PizzaSize size, List<Topping> toppings) {
        super(name);
        this.size = size;
        this.toppings = List.copyOf(toppings);
    }

    @Override
    public BigDecimal calculatePrice() {
        BigDecimal toppingsTotal = toppings.stream()
                .map(Topping::extraPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return size.getBasePrice().add(toppingsTotal);
    }

    @Override
    public void display() {
        String toppingNames = toppings.isEmpty()
                ? "No extra toppings"
                : toppings.stream()
                    .map(Topping::name)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");

        System.out.println(getName() + " - " + size
                + " - " + toppingNames
                + " - " + calculatePrice() + " MAD");
    }
}
