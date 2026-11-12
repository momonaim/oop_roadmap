package oop.roadmap.restaurant;

import oop.roadmap.restaurant.enums.DrinkVolume;
import oop.roadmap.restaurant.enums.PizzaSize;
import oop.roadmap.restaurant.model.*;
import oop.roadmap.restaurant.payment.PaymentMethod;
import oop.roadmap.restaurant.payment.PaymentResult;
import oop.roadmap.restaurant.payment.WalletPayment;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.util.List;

@SpringBootApplication
public class RestaurantOrderingApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestaurantOrderingApplication.class, args);
	}

	@Bean
	CommandLineRunner run() {
		return args -> {
			// 1. Toppings
			Topping cheese = new Topping("Extra cheese", new BigDecimal("7.00"));
			Topping olives = new Topping("Olives", new BigDecimal("4.00"));

			// 2. Menu items
			Pizza pizza = new Pizza("Margherita", PizzaSize.MEDIUM, List.of(cheese, olives));
			Drink cola = new Drink("Cola", DrinkVolume.ML_330);
			Dessert cake = new Dessert("Chocolate cake", "One slice", new BigDecimal("25.00"));

			// 3. Combo: pizza + drink with 10% discount
			Combo lunchCombo = new Combo("Lunch Combo", List.of(pizza, cola), new BigDecimal("0.10"));

			// 4. Order: combo + dessert
			Order order = new Order("ORD-1001", List.of(lunchCombo, cake));
			order.displayOrder();

			// 5. Pay with wallet
			PaymentMethod paymentMethod = new WalletPayment("wallet-account-123");
			PaymentResult result = order.requestPayment(paymentMethod);

			System.out.println("Payment message: " + result.message());
			System.out.println("Payment succeeded? " + order.succeeded());
		};
	}
}
