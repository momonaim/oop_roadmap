package oop.roadmap.restaurant.model;

import oop.roadmap.restaurant.enums.DrinkVolume;

import java.math.BigDecimal;

public class Drink extends MenuItem {

    private final DrinkVolume volume;

    public Drink(String name, DrinkVolume volume) {
        super(name);
        this.volume = volume;
    }

    public DrinkVolume getVolume() {
        return volume;
    }

    @Override
    public BigDecimal calculatePrice() {
        return volume.getPrice();
    }

    @Override
    public void display() {
        System.out.println(getName() + " (" + volume + ") - " + calculatePrice() + " MAD");
    }
}
