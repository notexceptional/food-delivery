# Food Delivery System (Java + Swing + SOAP)

A desktop food-delivery application built in Java with:
- A SOAP API for external integration
- JSON file-based persistence (no database required)
- A Swing UI for customers and restaurants

The app supports restaurant browsing, menu management, cart/checkout flow, coupon discounts, order tracking, and rider assignment.

## Tech

- Java 25 (as configured in `pom.xml`)
- Maven
- Swing (desktop UI)
- Jakarta JAX-WS (SOAP)
- Gson (JSON storage)
- JUnit 5 (unit testing)

## Project Structure

```text
src/main/java/com/fooddelivery
├── Main.java                  # App entrypoint
├── api/
│   └── FoodDeliveryService.java
├── models/                    # Domain models (Customer, Restaurant, Order, etc.)
├── services/                  # Business logic
├── storage/                   # In-memory store + JSON persistence
└── ui/                        # Swing UI panels/frames

data/
├── customers.json
├── restaurants.json
├── orders.json
├── coupons.json
└── riders.json

src/test/java/com/fooddelivery/models
└── *Test.java                 # Model-focused unit tests
```

## Features

### Customer Side
- Register/Login customer accounts
- Search and browse restaurants
- Sort restaurants by name/rating
- View restaurant menus
- Add menu items with quantity/options to cart
- Apply coupon codes
- Choose payment method (cash/card/mobile banking)
- Place orders and track status/rider details

### Restaurant Side
- Register/Login restaurant owners
- Toggle restaurant open/closed status
- Add/remove/toggle menu items
- Manage item stock and options
- Create restaurant-specific coupons
- View incoming orders and update order status

### System Features
- SOAP endpoint published at startup
- Auto persistence to JSON after state changes
- Delivery rider auto-assignment on order placement
- Rider release on delivered/cancelled orders

### Start Workflow
1. Load JSON data into singleton `DataStore`
2. Publish SOAP service at `http://localhost:8888/FoodDeliveryService`
3. Launch Swing UI (`MainFrame`)

## Prerequisites

- JDK 25 installed and available in `PATH`
- Maven 3.9+

> Note: The project currently compiles with Java release `25` in `pom.xml`.

## Build & Test

From project root:

```bash
mvn clean compile
mvn test
```

## Run the Application

### Option 1: Compile then run main class

```bash
mvn clean compile
java -cp target/classes com.fooddelivery.Main
```

### Option 2: Package then run

```bash
mvn clean package
java -cp target/classes com.fooddelivery.Main
```

After startup:
- Swing UI appears with Customer and Restaurant tabs
- SOAP WSDL is available at:
  - `http://localhost:8888/FoodDeliveryService?wsdl`

## SOAP API

Service class: `com.fooddelivery.api.FoodDeliveryService`

Exposed operations:
- `getRestaurantsByArea(area)`
- `searchRestaurants(query)`
- `getMenuByRestaurantId(restaurantId)`
- `getOrderStatus(orderId)`

All operations return formatted text responses.

## Data Files

The app reads/writes these files under `data/`:
- `customers.json`
- `restaurants.json`
- `orders.json`
- `coupons.json`
- `riders.json`

If files are missing, empty lists are used and files are created when data is first saved.

## Testing

Current tests are model-level JUnit tests under:
- `src/test/java/com/fooddelivery/models`

Example report output (after running tests) can be found in:
- `target/surefire-reports`

## Known Notes

- Authentication uses plain text credentials stored in JSON (dev/demo style).
- Payment is simulated by `PaymentService` (no real gateway integration).
- Persistence is file-based and synchronous, intended for learning/demo use.
- Currency symbol displayed in UI/messages is Bangladeshi Taka (৳).

## License

No license file is currently included in this repository.
If you plan to distribute this project, add a `LICENSE` file.
