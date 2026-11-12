# Object-Oriented Programming Roadmap: Restaurant Orders, Menu Items, Combos, and Payments

**Language:** Java\
**Goal:** Build a small restaurant-ordering domain step by step while
understanding encapsulation, inheritance, composition, polymorphism,
abstraction, and interfaces.

---

## 1. What we are building

The application models a restaurant menu and a customer order.

A menu item can be:

- A `Drink`, whose price depends on its volume.
- A `Pizza`, whose price depends on its size and selected toppings.
- A `Dessert`, whose price is based on its details.
- A `Combo`, which contains several menu items and may apply a bundle
  discount.

An `Order` contains ordered menu items and delegates payment to a
payment method such as `CardPayment` or `WalletPayment`.

### Conceptual class structure

```text
                    MenuItem (abstract class)
                    - name
                    + display()
                    + calculatePrice()
                         /       |        \
                        /        |         \
                    Drink      Pizza     Dessert

                  Combo
                  - List<MenuItem>
                  - discount
                  + display()
                  + calculatePrice()

                  Order
                  - List<MenuItem>
                  - PaymentMethod
                  + calculateTotal()
                  + requestPayment()
                  + succeeded()

          PaymentMethod (interface)
                 /              \
          CardPayment        WalletPayment
          + processPayment() + processPayment()
```

`Combo` is not a subclass of `MenuItem` merely because it contains menu
items. In this design it _is_ a menu item from the customer's
perspective, and it _has_ other menu items internally. That makes it
both a subtype and an example of composition.

---

## 2. Before coding: the OOP principles

### 2.1 Encapsulation

Encapsulation means keeping an object's data protected and allowing
controlled access through methods.

Instead of exposing fields:

```java
public double price;
```

prefer private fields:

```java
private BigDecimal price;
```

Then validate values in constructors or methods. For example, a pizza
should not accept a negative topping price.

**Why it matters here:** callers should not be able to change an order
total or a menu item's price to an invalid value.

### 2.2 Inheritance

Inheritance allows a specialized class to reuse and extend a general
class.

`Drink`, `Pizza`, and `Dessert` are all kinds of `MenuItem`, so they
inherit from the abstract `MenuItem` class.

Use inheritance when the relationship genuinely means **is-a**:

- A pizza **is a** menu item.
- A drink **is a** menu item.

Do not use inheritance just to reuse unrelated code.

### 2.3 Composition

Composition means an object is built from, or contains, other objects.
It models a **has-a** relationship.

A `Combo` has multiple `MenuItem` objects:

```java
private final List<MenuItem> items;
```

An `Order` also has multiple menu items:

```java
private final List<MenuItem> items;
```

This lets a combo contain a pizza, a drink, a dessert, or even another
combo if the business rules permit it.

### 2.4 Polymorphism

Polymorphism means code can work with a parent type or interface while
the actual object decides which implementation runs.

For example:

```java
for (MenuItem item : items) {
    item.display();
    System.out.println(item.calculatePrice());
}
```

The loop does not need to ask whether each item is a pizza, drink,
dessert, or combo. Java calls the overridden method belonging to the
actual object.

This is **runtime polymorphism** through method overriding.

### 2.5 Abstraction

Abstraction exposes what an object can do while hiding the internal
details of how it does it.

An abstract class can define shared state and common behavior while
leaving some operations to subclasses:

```java
public abstract class MenuItem {
    public abstract BigDecimal calculatePrice();
}
```

The caller knows every menu item can calculate its price. It does not
need to know the formula for every type.

### 2.6 Interfaces

An interface defines a contract: classes that implement it promise to
provide certain operations.

`PaymentMethod` describes the ability to process a payment. A card
payment and a wallet payment can implement that contract differently.

An interface is useful when different classes can perform the same
capability without needing to share a parent class.

---

## 3. Project setup

Create a Maven project and use Java 17 or later.

Suggested package structure:

```text
com.example.restaurant
│
├── RestaurantApplication.java
│
├── model
│   ├── MenuItem.java
│   ├── Drink.java
│   ├── Pizza.java
│   ├── Dessert.java
│   ├── Combo.java
│   └── Order.java
│
├── enums
│   ├── PizzaSize.java
│   ├── DrinkVolume.java
│   └── OrderStatus.java
│
├── payment
│   ├── PaymentMethod.java
│   ├── CardPayment.java
│   ├── WalletPayment.java
│   └── PaymentResult.java
│
├── service
│   ├── OrderService.java
│   └── MenuService.java
│
├── controller
│   ├── OrderController.java
│   └── MenuController.java
│
└── dto
    ├── CreateOrderRequest.java
    └── OrderResponse.java
```

For money, use `BigDecimal`, not `double`. Floating-point numbers can
introduce rounding errors in financial calculations.

A small helper convention:

```java
new BigDecimal("12.50")
```

Prefer string constructors over `new BigDecimal(12.50)`.

---

## 4. Step 1 --- Create the abstract MenuItem class

This is the common parent of all items that can be placed on the menu.

```java
package com.example.restaurant.model;

import java.math.BigDecimal;
import java.util.Objects;

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
```

**What to notice:**

1.  `abstract` means you cannot create `new MenuItem(...)` directly.
2.  `private final` protects the name from arbitrary changes after
    construction.
3.  `protected` lets subclasses call the parent constructor.
4.  `calculatePrice()` is abstract because each subtype has its own
    pricing rule.
5.  `display()` is shared behavior. Subclasses may override it for more
    details.

**Principles:** abstraction, encapsulation, inheritance foundation.

---

## 5. Step 2 --- Model drinks and volume-based pricing

First define the available drink sizes and their prices. This example
uses fixed prices per volume.

```java
package com.example.restaurant.model;

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
```

Now create `Drink`:

```java
package com.example.restaurant.model;

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
        System.out.println(getName() + " (" + volume + ") - "
                + calculatePrice() + " MAD");
    }
}
```

Example:

```java
Drink cola = new Drink("Cola", DrinkVolume.ML_330);
cola.display();
```

Expected output:

```text
Cola (ML_330) - 10.00 MAD
```

**Why use an enum?** The available volumes are a fixed set of choices.
Each choice carries its corresponding price, avoiding scattered `if`
statements.

**Principles:** inheritance, overriding, encapsulation.

---

## 6. Step 3 --- Model pizza sizes and toppings

A pizza price depends on its size and toppings.

```java
package com.example.restaurant.model;

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
```

Represent toppings as objects so each topping has a name and an extra
price:

```java
package com.example.restaurant.model;

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
```

Now implement `Pizza`:

```java
package com.example.restaurant.model;

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
```

Example:

```java
Topping cheese = new Topping("Extra cheese", new BigDecimal("7.00"));
Topping olives = new Topping("Olives", new BigDecimal("4.00"));

Pizza pizza = new Pizza(
        "Margherita",
        PizzaSize.MEDIUM,
        List.of(cheese, olives)
);

pizza.display();
```

Calculation:

```text
Medium base price = 50.00 MAD
Extra cheese      =  7.00 MAD
Olives            =  4.00 MAD
Total             = 61.00 MAD
```

`List.copyOf` creates an unmodifiable copy, preventing outside code from
changing the pizza's toppings through the original list.

**Principles:** inheritance, encapsulation, composition (a pizza
contains topping objects), polymorphism through overriding.

---

## 7. Step 4 --- Model desserts

For this example, dessert pricing is a fixed price, while `details`
describes the dessert.

```java
package com.example.restaurant.model;

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
        System.out.println(getName() + " (" + details + ") - "
                + calculatePrice() + " MAD");
    }
}
```

Example:

```java
Dessert cake = new Dessert(
        "Chocolate cake",
        "One slice, chocolate topping",
        new BigDecimal("25.00")
);
```

Notice that `Dessert` does not need the same fields or pricing algorithm
as `Pizza`. It only needs to respect the `MenuItem` contract.

---

## 8. Step 5 --- Create Combo using composition and polymorphism

A combo contains several `MenuItem` objects. Its regular price is the
sum of the contained items. A percentage discount is then applied.

```java
package com.example.restaurant.model;

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

        BigDecimal discount = subtotal.multiply(discountRate);

        return subtotal.subtract(discount)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public void display() {
        System.out.println("Combo: " + getName());

        for (MenuItem item : items) {
            item.display();
        }

        System.out.println("Discount: "
                .concat(discountRate.multiply(new BigDecimal("100")).toPlainString())
                .concat("%"));
        System.out.println("Combo total: " + calculatePrice() + " MAD");
    }
}
```

Example:

```java
Combo lunchCombo = new Combo(
        "Lunch Combo",
        List.of(pizza, cola, cake),
        new BigDecimal("0.10")
);
```

If the items cost 61 + 10 + 25 = 96 MAD, a 10% discount gives 86.40 MAD.

### Where is composition?

The combo owns a list of menu items:

```java
private final List<MenuItem> items;
```

### Where is polymorphism?

The combo does not need separate code for pizza, drink, and dessert:

```java
.map(MenuItem::calculatePrice)
```

Each object calculates its own price. The combo simply asks every item
for its price.

### A useful design distinction

This example allows a combo to contain another combo because `Combo` is
a `MenuItem`. If nested combos are not allowed by the business rules,
validate the list in the constructor and reject `Combo` elements.

---

## 9. Step 6 --- Create a payment abstraction with an interface

Payment is a separate responsibility from menu pricing. The order should
not need to know the internal details of card or wallet processing.

Define the contract:

```java
package com.example.restaurant.payment;

import java.math.BigDecimal;

public interface PaymentMethod {
    PaymentResult processPayment(BigDecimal amount);
}
```

A result object:

```java
package com.example.restaurant.payment;

public record PaymentResult(boolean succeeded, String message) {
}
```

Implement card payment:

```java
package com.example.restaurant.payment;

import java.math.BigDecimal;

public class CardPayment implements PaymentMethod {
    private final String cardToken;

    public CardPayment(String cardToken) {
        if (cardToken == null || cardToken.isBlank()) {
            throw new IllegalArgumentException("A payment token is required");
        }
        this.cardToken = cardToken;
    }

    @Override
    public PaymentResult processPayment(BigDecimal amount) {
        // Demonstration only: a real application calls a payment provider.
        if (amount.signum() <= 0) {
            return new PaymentResult(false, "Invalid payment amount");
        }

        return new PaymentResult(true, "Card payment accepted");
    }
}
```

Implement wallet payment:

```java
package com.example.restaurant.payment;

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
        // Demonstration only: a real application calls a wallet provider.
        if (amount.signum() <= 0) {
            return new PaymentResult(false, "Invalid payment amount");
        }

        return new PaymentResult(true, "Wallet payment accepted");
    }
}
```

**Important:** these implementations simulate successful payments. They
do not charge a real card or wallet. In a real system, use a trusted
payment provider, tokenized payment details, server-side verification,
and provider webhooks. Never store raw card numbers or security codes.

### Why an interface?

Both classes support the same operation:

```java
processPayment(amount)
```

But each can implement it differently. This is abstraction through an
interface and polymorphism through interface dispatch.

---

## 10. Step 7 --- Build the Order class

An order contains items and uses a payment method. It should calculate
the total and delegate payment.

```java
package com.example.restaurant.model;

import com.example.restaurant.payment.PaymentMethod;
import com.example.restaurant.payment.PaymentResult;

import java.math.BigDecimal;
import java.util.List;

public class Order {
    private final String orderId;
    private final List<MenuItem> items;
    private PaymentResult paymentResult;

    public Order(String orderId, List<MenuItem> items) {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("Order ID is required");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("An order must contain items");
        }

        this.orderId = orderId;
        this.items = List.copyOf(items);
    }

    public BigDecimal calculateTotal() {
        return items.stream()
                .map(MenuItem::calculatePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void displayOrder() {
        System.out.println("Order: " + orderId);

        for (MenuItem item : items) {
            item.display();
        }

        System.out.println("Order total: " + calculateTotal() + " MAD");
    }

    public PaymentResult requestPayment(PaymentMethod paymentMethod) {
        if (paymentMethod == null) {
            throw new IllegalArgumentException("Payment method is required");
        }

        paymentResult = paymentMethod.processPayment(calculateTotal());
        return paymentResult;
    }

    public boolean succeeded() {
        return paymentResult != null && paymentResult.succeeded();
    }
}
```

### Understand the payment flow

1.  The caller creates an order.
2.  The caller selects a `PaymentMethod`.
3.  `requestPayment()` calculates the order total.
4.  The order calls `processPayment()` on the supplied implementation.
5.  The payment implementation returns a `PaymentResult`.
6.  `succeeded()` reports whether a result exists and indicates success.

The order depends on the `PaymentMethod` interface, not on `CardPayment`
or `WalletPayment` directly. This is called **programming to an
interface**.

**Design note:** a production order should also have an explicit status
such as `PENDING`, `PAID`, or `PAYMENT_FAILED`, prevent duplicate
payment attempts, and handle asynchronous payment confirmation. This
small example keeps the first version focused on OOP.

---

## 11. Step 8 --- Run the complete example

Create `Main.java`:

```java
package com.example.restaurant;

import com.example.restaurant.model.*;
import com.example.restaurant.payment.*;

import java.math.BigDecimal;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // 1. Toppings
        Topping cheese = new Topping(
                "Extra cheese", new BigDecimal("7.00"));
        Topping olives = new Topping(
                "Olives", new BigDecimal("4.00"));

        // 2. Menu items
        Pizza pizza = new Pizza(
                "Margherita",
                PizzaSize.MEDIUM,
                List.of(cheese, olives)
        );

        Drink cola = new Drink("Cola", DrinkVolume.ML_330);

        Dessert cake = new Dessert(
                "Chocolate cake",
                "One slice",
                new BigDecimal("25.00")
        );

        // 3. Combo: pizza + drink, with a 10% discount
        Combo lunchCombo = new Combo(
                "Lunch Combo",
                List.of(pizza, cola),
                new BigDecimal("0.10")
        );

        // 4. Order: combo + dessert
        Order order = new Order(
                "ORD-1001",
                List.of(lunchCombo, cake)
        );

        order.displayOrder();

        // 5. Choose a payment implementation
        PaymentMethod paymentMethod =
                new WalletPayment("wallet-account-123");

        // You can replace it with:
        // PaymentMethod paymentMethod = new CardPayment("provider-token");

        PaymentResult result = order.requestPayment(paymentMethod);

        System.out.println("Payment message: " + result.message());
        System.out.println("Payment succeeded? " + order.succeeded());
    }
}
```

### Follow the calculation

- Medium pizza: 50.00 MAD
- Extra cheese: 7.00 MAD
- Olives: 4.00 MAD
- Pizza total: 61.00 MAD
- Cola (330 ml): 10.00 MAD
- Combo subtotal: 71.00 MAD
- 10% combo discount: 7.10 MAD
- Combo price: 63.90 MAD
- Chocolate cake: 25.00 MAD
- Order total: **88.90 MAD**

The exact display order depends on the methods you call, but the
calculated total should be 88.90 MAD.

---

## 12. Step 9 --- Identify every OOP principle in the finished design

---

Principle Where it appears What it teaches

---

Encapsulation Private fields, constructors, validation, Protect object state
immutable lists

Inheritance `Drink extends MenuItem`, Reuse and specialize a
`Pizza extends MenuItem`, common type
`Dessert extends MenuItem`,  
 `Combo extends MenuItem`

Abstraction Abstract `MenuItem` and `PaymentMethod` Expose essential
contract operations, hide
implementation details

Polymorphism `MenuItem.calculatePrice()` and Same call, different
`PaymentMethod.processPayment()` runtime behavior

Composition `Combo` contains `List<MenuItem>`; `Order` Build complex objects
contains `List<MenuItem>`; `Pizza` from smaller objects
contains toppings

Interface `CardPayment implements PaymentMethod`; Depend on a contract
implementation `WalletPayment implements PaymentMethod` rather than a concrete
class

---

Remember: **inheritance is an is-a relationship; composition is a has-a
relationship.**

---

## 13. Step 10 --- Exercises to make sure you understand

Try these in order. Do not move to the next exercise until you can
explain why your solution works.

### Beginner

- [ ] Add a 750 ml drink volume with its own price.
- [ ] Add an `XL` pizza size and base price.
- [ ] Add a new topping and verify the pizza price.
- [ ] Create two desserts with different details and prices.
- [ ] Create an order containing a pizza, drink, and dessert.

### Intermediate

- [ ] Add a `VegetarianPizza` subclass if it has genuinely different
      behavior or rules. Otherwise, keep using `Pizza` with toppings; do
      not create subclasses without a reason.
- [ ] Add a fixed-amount discount to `Combo`, in addition to the
      percentage discount.
- [ ] Add a `DiscountPolicy` interface and implement
      `PercentageDiscount` and `FixedDiscount`.
- [ ] Add `CashPayment implements PaymentMethod`.
- [ ] Write a method that accepts `List<MenuItem>` and displays every
      item without checking its concrete type.
- [ ] Explain why `Order` should receive a `PaymentMethod` rather than
      instantiate `new CardPayment()` internally.

### Advanced

- [ ] Add `OrderStatus` (`CREATED`, `PAYMENT_PENDING`, `PAID`,
      `PAYMENT_FAILED`).
- [ ] Prevent a paid order from being paid again.
- [ ] Add an order receipt with item subtotals and combo discounts.
- [ ] Add unit tests for every pricing rule.
- [ ] Replace simulated payment with a provider sandbox, while keeping
      provider-specific code behind the `PaymentMethod` interface.
- [ ] Consider whether nested combos should be allowed and enforce the
      business rule.

---

## 14. Step 11 --- Suggested unit tests

Test behavior, not just whether methods execute.

Example with JUnit 5:

```java
import com.example.restaurant.model.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PricingTest {

    @Test
    void mediumPizzaIncludesToppingPrices() {
        Pizza pizza = new Pizza(
                "Test pizza",
                PizzaSize.MEDIUM,
                List.of(new Topping(
                        "Cheese", new BigDecimal("7.00")))
        );

        assertEquals(
                new BigDecimal("57.00"),
                pizza.calculatePrice()
        );
    }

    @Test
    void comboAppliesPercentageDiscount() {
        Drink drink = new Drink("Water", DrinkVolume.ML_250);
        Dessert dessert = new Dessert(
                "Cake", "Slice", new BigDecimal("20.00"));

        Combo combo = new Combo(
                "Test combo",
                List.of(drink, dessert),
                new BigDecimal("0.10")
        );

        // 8 + 20 = 28; 10% off = 25.20
        assertEquals(
                new BigDecimal("25.20"),
                combo.calculatePrice()
        );
    }
}
```

If you later change rounding rules, update the tests to reflect the
chosen business rule.

---

## 15. Common mistakes to avoid

1.  **Using `double` for money:** use `BigDecimal`.
2.  **Making every field public:** keep state private and validate it.
3.  **Using inheritance for code reuse alone:** verify the is-a
    relationship.
4.  **Using `instanceof` everywhere:** prefer polymorphic methods when
    behavior belongs to each type.
5.  **Putting all logic in `Main`:** let each class own its
    responsibilities.
6.  **Making `Order` know card and wallet internals:** depend on
    `PaymentMethod`.
7.  **Letting a combo calculate prices by checking item types:** call
    `calculatePrice()` polymorphically.
8.  **Confusing overloading and overriding:** overriding replaces
    inherited behavior; overloading uses the same method name with
    different parameter lists.
9.  **Treating a successful method return as proof of a real payment:**
    real payments need provider confirmation and robust error handling.
10. **Overengineering too early:** first make the simple model work,
    then introduce extra abstractions when a real requirement justifies
    them.

---

## 16. A practical learning sequence

---

Session Focus Deliverable

---

1 Classes, objects, Basic `MenuItem`
fields, constructors

2 Encapsulation and Safe constructors and
validation private fields

3 Abstract classes and `Drink`, `Pizza`,
inheritance `Dessert`

4 Enums and `BigDecimal` Volume- and size-based
prices

5 Composition Pizza toppings and
combo contents

6 Overriding and Generic menu display
polymorphism and pricing

7 Interfaces and Payment implementations
abstraction

8 Collaboration between Complete `Order`
objects workflow

9 Testing and edge cases JUnit pricing and
payment tests

10 Refactoring Cleaner, more
maintainable design

---

### Final self-check

You understand the example when you can answer these questions without
looking at the code:

1.  Why is `MenuItem` abstract?
2.  Why does `Pizza` extend `MenuItem`?
3.  Why does `Combo` contain a list of `MenuItem` objects?
4.  How does the combo calculate the price of an unknown item type?
5.  What is the difference between overriding and overloading?
6.  Why is `PaymentMethod` an interface?
7.  How can `Order` use a new payment type without changing its own
    code?
8.  Where is encapsulation enforced?
9.  What could go wrong if money were stored as `double`?
10. What extra safeguards would a real payment workflow need?

If you can implement the project from an empty Maven project and explain
each answer, you have a solid practical foundation in the main OOP
principles.
