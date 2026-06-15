# 🛍️ Bhuvi Style — Full Stack E-Commerce Website

A complete, production-ready **full stack e-commerce web application** built with **Spring Boot (Java 17)**, **MySQL**, **HTML/CSS/JavaScript**, and **JWT Authentication**. Features include product browsing, search, cart, wishlist, order management, admin dashboard, image uploads, and an AI-powered fashion chatbot.

---

## 🚀 Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 17, Spring Boot 3.3.5 |
| Security | Spring Security + JWT (JJWT 0.12.6) |
| Database | MySQL 8.x + Spring Data JPA (Hibernate) |
| Frontend | HTML5, CSS3, Vanilla JavaScript |
| Build Tool | Apache Maven |
| AI Chatbot | OpenAI API (with local fallback) |

---

## 📁 Project Structure

```
E-Commerce website/
├── stylegpt-store/                  ← Main Spring Boot project
│   ├── src/
│   │   └── main/
│   │       ├── java/com/stylegpt/store/
│   │       │   ├── config/          # SecurityConfig, DataSeeder
│   │       │   ├── controller/      # REST API controllers
│   │       │   ├── dto/             # Request/Response DTOs
│   │       │   ├── entity/          # JPA entities (User, Product, Order...)
│   │       │   ├── exception/       # Global error handling
│   │       │   ├── repository/      # Spring Data JPA repositories
│   │       │   ├── security/        # JWT filter, UserPrincipal
│   │       │   └── service/         # Business logic services
│   │       └── resources/
│   │           ├── static/          # Frontend (HTML, CSS, JS)
│   │           │   ├── css/
│   │           │   ├── js/
│   │           │   ├── index.html
│   │           │   ├── login.html
│   │           │   ├── register.html
│   │           │   ├── product.html
│   │           │   ├── cart.html
│   │           │   ├── orders.html
│   │           │   ├── wishlist.html
│   │           │   └── admin.html
│   │           └── application.properties
│   ├── uploads/                     # Product image uploads
│   └── pom.xml
└── README.md
```

---

## ⚙️ Setup & Run

### Prerequisites
- ✅ Java 17+
- ✅ Apache Maven 3.6+
- ✅ MySQL 8.0+

### Step 1 — Create MySQL Database

```sql
CREATE DATABASE stylegpt_store;
```

### Step 2 — Configure Database Password

Edit `stylegpt-store/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/stylegpt_store
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

### Step 3 — Run the Application

```powershell
cd stylegpt-store
mvn spring-boot:run
```

### Step 4 — Open in Browser

```
http://localhost:8080/index.html
```

---

## 🔐 Demo Accounts

| Role | Email | Password |
|------|-------|----------|
| 👤 User | `user@stylegpt.com` | `user123` |
| 🔑 Admin | `admin@stylegpt.com` | `admin123` |

---

## 🛒 Features

### Customer Features
- 🔍 **Search** — Search across all product categories
- 🗂️ **Category Filter** — Shoes, Dresses, Shirts, Pants, Sarees, Kurtis, Grocery, Snacks, Accessories
- 💰 **Price Filter** — Filter by price range
- 🛒 **Cart** — Add/remove items, adjust quantity
- ❤️ **Wishlist** — Save favourite products
- 📦 **Orders** — Place orders and view order history
- 👤 **Auth** — Register and login with Email + Password
- 🤖 **AI Chatbot** — Fashion recommendations from StyleGPT assistant

### Admin Features
- ➕ **Add Products** — Upload with image, category, price, stock
- ✏️ **Edit Products** — Update any product details
- 🗑️ **Delete Products** — Remove from catalog
- 📊 **Dashboard** — Manage all products, orders, users

---

## 🔑 Authentication

- **Register** → `POST /api/auth/register`
- **Login** → `POST /api/auth/login` (Email + Password)
- JWT token stored in `localStorage`
- Passwords hashed with **BCrypt**
- Protected routes require `Authorization: Bearer <token>`

---

## 🗄️ Database Schema

| Table | Description |
|-------|-------------|
| `users` | Registered users with roles |
| `products` | Product catalog |
| `cart_items` | Shopping cart items |
| `wishlist_items` | Saved wishlist items |
| `orders` | Order headers |
| `order_items` | Individual order line items |
| `chatbot_history` | Chat history per user |

---

## 📦 Product Categories

| Category | Examples |
|----------|---------|
| 👟 Shoes | Sneakers, Loafers, Sandals, Slippers, Heels, Boys Shoes |
| 👗 Dresses | Party, Casual, Maxi, Office, Kids |
| 👔 Shirts | Formal, Casual, T-Shirts |
| 👖 Pants | Jeans, Chinos, Track Pants, Leggings |
| 🥻 Sarees | Silk, Cotton, Printed, Designer |
| 👘 Kurtis | Cotton, Anarkali, Ethnic |
| 🛒 Grocery | Rice, Oil, Flour, Sugar, Masala, Tea |
| 🍿 Snacks | Cookies, Chips, Dry Fruits, Noodles |
| 💍 Accessories | Sunglasses, Watch, Handbag, Earrings |

---

## 🌐 Hosting (Deploy Online)

### Option 1 — Railway (Recommended)
1. Push code to GitHub
2. Go to [railway.app](https://railway.app) → Deploy from GitHub
3. Add MySQL service
4. Set environment variables:
   ```
   SPRING_DATASOURCE_URL=<railway mysql url>
   SPRING_DATASOURCE_USERNAME=root
   SPRING_DATASOURCE_PASSWORD=<password>
   ```
5. Live at `https://your-app.railway.app` ✅

### Option 2 — Ngrok (Quick Share)
```powershell
mvn spring-boot:run
ngrok http 8080
```
Share the `https://xxx.ngrok.io` URL with anyone!

---

## 🤖 AI Fashion Chatbot

- Uses **OpenAI API** when `OPENAI_API_KEY` env variable is set
- Falls back to **local smart suggestions** automatically based on:
  - Color, Category, Occasion, Budget detection

---

## 📝 Built With ❤️ by Bhuvi