# StyleGPT Store

StyleGPT Store is a complete Java Spring Boot + MySQL fashion e-commerce app with HTML, CSS, vanilla JavaScript, JWT authentication, admin product management, cart, wishlist, checkout, order history, and an OpenAI-backed fashion chatbot with local fallback.

## Run Setup

1. Install Java 17, Maven, and MySQL.
2. Create the database. JPA can create/update tables automatically, and `src/main/resources/schema.sql` is included to show the exact MySQL table relationships and constraints.

```sql
CREATE DATABASE stylegpt_store;
```

3. Edit `src/main/resources/application.properties` and set your MySQL password:

```properties
spring.datasource.username=root
spring.datasource.password=your_mysql_password
```

4. Optional AI key:

```powershell
$env:OPENAI_API_KEY="your_openai_api_key"
```

5. Run:

```powershell
mvn spring-boot:run
```

6. Open `http://localhost:8080/index.html`.

## Demo Logins

- User: `user@stylegpt.com` / `user123`
- Admin: `admin@stylegpt.com` / `admin123`

## JWT Authentication

Register and login call `/api/auth/register` and `/api/auth/login`. The backend hashes passwords using BCrypt and returns a JWT. The frontend stores the JWT in `localStorage` and sends it on protected requests using `Authorization: Bearer <token>`. Spring Security validates the JWT through `JwtAuthenticationFilter`.

## Backend Flow

Controllers receive REST requests, services enforce business rules, repositories read/write MySQL through JPA entities, and DTOs keep API responses clean. Admin APIs under `/api/admin/**` require `ROLE_ADMIN`.

## Frontend Flow

The static pages are served by Spring Boot. `app.js` handles login, product search, category/price filters, cart, wishlist, checkout, orders, and admin actions. `chatbot.js` powers the floating StyleGPT assistant.

## Database Relationships

`users` owns many `cart_items`, `wishlist_items`, `orders`, and `chatbot_history`. `orders` owns many `order_items`. Cart and wishlist use unique `(user_id, product_id)` constraints to prevent duplicate rows. Orders keep item price/name snapshots while still linking to products.

## AI Chatbot

`/api/chatbot/message` calls OpenAI Responses API when `OPENAI_API_KEY` is configured. If the key is missing or the call fails, the service returns local catalog-based fashion suggestions using colors, category, occasion, and budget detection.
