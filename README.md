# 🧺 AquaClean Luxe — Laundry Management System

A full-featured, dark-themed laundry management web application built with **Spring Boot 3.2.5**, **Thymeleaf**, **Spring Security**, and **Swagger/OpenAPI**. Features a premium UI with neon-accent glassmorphism, real-time order tracking, role-based dashboards, and Swagger-documented REST APIs.

---

## 📋 Prerequisites (Before Opening Eclipse)

Make sure these are installed on your PC:

| Required | Version | How to Check |
|----------|---------|-------------|
| **Java JDK** | 17 or higher | Open CMD → `java -version` |
| **Maven** | 3.8+ (or use included `mvnw.cmd`) | Open CMD → `mvn -version` |
| **Eclipse IDE** | Latest (with Spring Tools) | Already installed |
| **Browser** | Chrome / Edge / Firefox | Already installed |

> **Note:** The project includes the Maven Wrapper (`mvnw.cmd`), so you don't need to install Maven separately. Eclipse's built-in Maven will also work.

---

## 🚀 Step 1 — Import Project into Eclipse

1. **Open Eclipse**
2. Go to **File → Import...**
3. Select **Maven → Existing Maven Projects**
4. Click **Next**
5. Click **Browse...** and navigate to:
   ```
   C:\Users\suraj\laundry-system
   ```
6. Make sure `pom.xml` is checked in the list
7. Click **Finish**

Eclipse will now download dependencies and build the project. Wait for the progress bar in the bottom-right corner to complete.

> ⏱ This may take 2-5 minutes on the first import.

---

## 🚀 Step 2 — Run the Web Application

### Option A: Run from Eclipse (Recommended)

1. In the **Project Explorer** (left panel), expand:
   ```
   laundry-system → src/main/java → com.laundry.system
   ```
2. Right-click on **`LaundrySystemApplication.java`**
3. Select **Run As → Java Application** (or **Run As → Spring Boot App** if Spring Tools is installed)
4. Wait for the console output to show:
   ```
   Started LaundrySystemApplication in X seconds
   ```

### Option B: Run from Command Line

Open a **Command Prompt (CMD)** inside the project folder:

```cmd
cd C:\Users\suraj\laundry-system
mvnw.cmd spring-boot:run
```

---

### ✅ Confirm the App is Running

Open your browser and go to:

👉 **http://localhost:8080**

You should see the **AquaClean Luxe** dark-themed login page.

---

## 🎭 Demo Accounts (Pre-Loaded Data)

The app comes with **pre-loaded demo data** — no need to register! Just log in with any of these accounts:

| Role | Email | Password | Full Name | What You Can Do |
|------|-------|----------|-----------|----------------|
| 👑 **ADMIN** | `admin@aquaclean.com` | `password123` | Arjun Sharma | Full access — manage orders, services, staff, view analytics |
| 👔 **STAFF** | `raj@aquaclean.com` | `password123` | Raj Verma | View assigned tasks, update order progress (Washing) |
| 👔 **STAFF** | `priya@aquaclean.com` | `password123` | Priya Patel | View assigned tasks, update order progress (Ironing) |
| 👤 **CUSTOMER** | `amit@example.com` | `password123` | Amit Kumar | Place orders, view order history, download invoices |
| 👤 **CUSTOMER** | `neha@example.com` | `password123` | Neha Singh | Place orders, view order history, download invoices |

> **All accounts use the same password:** `password123`

### What Demo Data is Included?

✅ **5 laundry services** — Standard Wash (₹80/kg), Dry Clean (₹150/kg), Ironing Only (₹40/kg), Wash & Fold (₹100/kg), Premium Care (₹200/kg)
✅ **2 staff members** — Raj (Washing & Dry Cleaning), Priya (Ironing & Folding)
✅ **2 customers** — Amit and Neha with address and phone
✅ **6 sample orders** in different statuses so the dashboard looks populated:
   - Order 1: Delivered ✅
   - Order 2: Ready for delivery 📦
   - Order 3: Currently washing 🧺
   - Order 4: Picked up 🚚
   - Order 5: Pending ⏳
   - Order 6: Out for delivery 🚛
✅ **Payments, invoices, and notifications** linked to each order

### Quick Login Instructions

1. Open **http://localhost:8080/login**
2. Enter `admin@aquaclean.com` and password `password123`
3. You'll land on the **Admin Dashboard** with charts and stats
4. Try `amit@example.com` to see the **Customer Dashboard** with order tracker

---

## 🌐 Step 3 — Browse the Web Application

All pages require you to be logged in (except Login & Register). Use the demo accounts above to log in, or register a new account.

### Public Pages (No Login Required)

| Page | Link |
|------|------|
| **Login** | http://localhost:8080/login |
| **Register** | http://localhost:8080/register |

> ℹ️ New registrations are automatically assigned the **CUSTOMER** role.

### Pages by Role

#### 👤 Customer Pages

| Page | Link |
|------|------|
| **Customer Dashboard** | http://localhost:8080/dashboard |
| **Place New Order** | http://localhost:8080/customer/orders/new |
| **My Orders (History)** | http://localhost:8080/customer/orders |

#### 👔 Staff Pages

| Page | Link |
|------|------|
| **Staff Dashboard** | http://localhost:8080/dashboard |
| **Assigned Tasks** | http://localhost:8080/staff/orders |
| **Task Details** | http://localhost:8080/staff/orders/{id} |

#### 🔧 Admin Pages

| Page | Link |
|------|------|
| **Admin Dashboard** | http://localhost:8080/dashboard |
| **Manage Orders** | http://localhost:8080/admin/orders |
| **Order Details** | http://localhost:8080/admin/orders/{id} |
| **Manage Services** | http://localhost:8080/admin/services |
| **Manage Staff** | http://localhost:8080/admin/staff |
| **Analytics & Reports** | http://localhost:8080/admin/reports |

#### Utility Pages

| Page | Link |
|------|------|
| **Access Denied (403)** | http://localhost:8080/access-denied |
| **H2 Database Console** | http://localhost:8080/h2-console |

> **H2 Console Login:** JDBC URL: `jdbc:h2:mem:laundrydb` | Username: `root` | Password: `root`

---

## 📡 Step 4 — Test REST APIs with Swagger UI

The project includes **Swagger UI** for interactive API testing directly in your browser.

### Open Swagger UI

👉 **http://localhost:8080/swagger-ui/index.html**

You'll see the **AquaClean Luxe API** documentation page listing all REST endpoints.

### OpenAPI Spec Links (for Postman or other tools)

| Format | Link |
|--------|------|
| **JSON** | http://localhost:8080/v3/api-docs |
| **YAML** | http://localhost:8080/v3/api-docs.yaml |

---

## 🧪 Step 5 — API Testing Walkthrough (In Swagger UI)

### Public API — No Login Needed

These endpoints work without authentication — great for a quick test:

**1. List all services**

- In Swagger UI, expand **Services → GET `/api/services`**
- Click **Try it out**
- Click **Execute**
- ✅ You should get a **200 OK** response with a JSON array of services

**2. Get a specific service**

- Expand **Services → GET `/api/services/{id}`**
- Enter `id = 1`
- Click **Execute**
- ✅ You should get a **200 OK** with service details

---

### Authenticated API — Testing with Session Login

For endpoints that require login (orders, admin operations), you need to first log in through the web app, then Swagger UI will use the same session.

**Step-by-step:**

1. **Open a new browser tab** and go to **http://localhost:8080/login**
2. Log in with your credentials
3. **Go back to the Swagger UI tab** (http://localhost:8080/swagger-ui/index.html)
4. The session cookie is now shared — you can test authenticated endpoints

**3. Get all orders (Admin/Staff only)**

- Expand **Orders → GET `/api/orders`**
- Click **Try it out → Execute**
- ✅ If logged in as ADMIN or STAFF, you get **200 OK** with orders list

**4. Place a new order (Customer role)**

- Expand **Orders → POST `/api/orders`**
- Click **Try it out**
- Enter this JSON body:
  ```json
  {
    "customerId": 1,
    "pickupDate": "2026-06-10T10:00:00",
    "items": [
      {"serviceId": 1, "quantity": 2.0},
      {"serviceId": 2, "quantity": 1.5}
    ]
  }
  ```
- Click **Execute**
- ✅ You should get a **201 Created** response

**5. Update order status (Admin/Staff)**

- Expand **Orders → PATCH `/api/orders/{id}/status`**
- Enter `id = 1`, `status = PICKED_UP`
- Click **Execute**
- ✅ You should get **200 OK**

**6. Delete an order (Admin only)**

- Expand **Orders → DELETE `/api/orders/{id}`**
- Enter `id = 1`
- Click **Execute**
- ✅ You should get **204 No Content**

---

## 📋 Complete API Endpoint Reference

### Order API (`/api/orders`)

| Method | Endpoint | Auth Required | What It Does |
|--------|----------|---------------|-------------|
| GET | `/api/orders` | ✅ ADMIN/STAFF | List all orders |
| GET | `/api/orders/{id}` | ✅ Any logged-in user | Get order by ID |
| GET | `/api/orders/number/{orderNumber}` | ✅ Any logged-in user | Get order by order number |
| POST | `/api/orders` | ✅ CUSTOMER | Place a new order |
| PATCH | `/api/orders/{id}/status` | ✅ ADMIN/STAFF | Update order status |
| PATCH | `/api/orders/{id}/assign` | ✅ ADMIN | Assign staff to order |
| DELETE | `/api/orders/{id}` | ✅ ADMIN | Delete an order |

### Service API (`/api/services`) — Partially Public

| Method | Endpoint | Auth Required | What It Does |
|--------|----------|---------------|-------------|
| GET | `/api/services` | ❌ No (Public) | List all services |
| GET | `/api/services/{id}` | ❌ No (Public) | Get service by ID |
| POST | `/api/services` | ✅ ADMIN | Create a new service |
| PUT | `/api/services/{id}` | ✅ ADMIN | Update a service |
| DELETE | `/api/services/{id}` | ✅ ADMIN | Delete a service |

> **Tip:** Public GET endpoints are great for a quick sanity check that the API is running.

---

## 🧪 How to Run Tests

### From Eclipse

1. Right-click the project in **Project Explorer**
2. Select **Run As → Maven test**
3. Watch the **Console** tab for results

### From Command Line

```cmd
cd C:\Users\suraj\laundry-system

# Run ALL tests
mvnw.cmd test

# Run a specific test class
mvnw.cmd test -Dtest=OrderServiceImplTest

# Run tests with detailed output
mvnw.cmd test -Dtest=OrderApiControllerTest -Dspring.test.console.output=always
```

All **100+ tests** should pass with **BUILD SUCCESS**.

---

## ⚙️ Configuration

### Switch Database (Dev vs Prod)

The app runs in **dev** mode by default (H2 in-memory database).

To switch to **production** (MySQL):

1. Open **`src/main/resources/application.properties`**
2. Change this line:
   ```properties
   spring.profiles.active=prod
   ```
3. Update MySQL credentials in **`application-prod.properties`**

### Change Port

In **`application.properties`**:
```properties
server.port=9090
```
Then all links become `http://localhost:9090/...`

---

## 🐞 Troubleshooting in Eclipse

| Problem | Solution |
|---------|----------|
| **Project has errors (red X)** | Right-click project → **Maven → Update Project...** → Check all boxes → OK |
| **Cannot find `LaundrySystemApplication`** | Make sure the import finished. Check `src/main/java/com/laundry/system/LaundrySystemApplication.java` exists |
| **Port 8080 already in use** | Change `server.port` in `application.properties` to 9090 |
| **Swagger UI shows blank page** | Try the direct URL: http://localhost:8080/swagger-ui/index.html |
| **"White label error page" after login** | You may not have the right role for that page. See the role-based table above |
| **H2 Console returns 403** | Make sure you're in dev mode (`spring.profiles.active=dev` in `application.properties`) |
| **Tests fail with compilation errors** | Right-click project → **Maven → Update Project...** |
| **App runs but pages look unstyled** | Hard refresh: **Ctrl + F5** (or **Ctrl + Shift + R**) in browser |
| **Eclipse not showing Thymeleaf templates** | Install **Spring Tools 4** from Eclipse Marketplace (Help → Eclipse Marketplace → search "Spring Tools") |

---

## 🎨 Theme

The entire UI uses a custom **dark theme** with:
- Deep navy/charcoal backgrounds
- Neon cyan, purple, and emerald accents
- Glassmorphism card effects
- Animated particles on auth pages
- Chart.js integration with dark-themed charts
- Toast notifications and scroll animations

---

## 🏗️ Project Structure

```
laundry-system/
├── pom.xml                              # Maven config (Spring Boot 3.2.5)
├── mvnw.cmd                             # Maven Wrapper (no Maven install needed)
│
├── src/main/java/com/laundry/system/
│   ├── LaundrySystemApplication.java    # 🟢 Entry point — RUN THIS
│   ├── config/
│   │   ├── SecurityConfig.java          # Login, roles, CSRF, Swagger
│   │   ├── JpaConfig.java               # JPA auditing
│   │   └── OpenApiConfig.java           # Swagger/OpenAPI config
│   ├── controller/                      # Web page controllers (Thymeleaf)
│   │   ├── AuthController.java
│   │   ├── DashboardController.java
│   │   ├── OrderController.java
│   │   ├── CustomerController.java
│   │   ├── StaffController.java
│   │   ├── StaffOperationsController.java
│   │   ├── ServiceController.java
│   │   ├── InvoiceController.java
│   │   ├── ReportsController.java
│   │   └── api/                         # REST API controllers
│   │       ├── OrderApiController.java
│   │       └── ServiceApiController.java
│   ├── entity/                          # JPA database models
│   ├── repository/                      # Database access layer
│   ├── service/                         # Business logic
│   ├── dto/                             # Data transfer objects
│   └── exception/                       # Error handling
│
├── src/main/resources/
│   ├── application.properties           # App config (port, profile)
│   ├── application-dev.properties       # H2 database settings
│   ├── application-prod.properties      # MySQL database settings
│   ├── static/
│   │   ├── css/dashboard.css            # Dark theme CSS
│   │   └── js/dashboard.js              # Charts, toasts, animations
│   └── templates/                       # 16 Thymeleaf HTML pages
│
└── src/test/java/com/laundry/system/    # 100+ tests
```

---

## 📌 Quick Reference — Important Links

Once the app is running on **http://localhost:8080**:

| What | Link |
|------|------|
| 🏠 Home / Login | http://localhost:8080/login |
| 📝 Register | http://localhost:8080/register |
| 📊 Dashboard | http://localhost:8080/dashboard |
| 📖 Swagger UI (API Docs) | http://localhost:8080/swagger-ui/index.html |
| 📄 OpenAPI JSON | http://localhost:8080/v3/api-docs |
| 📄 OpenAPI YAML | http://localhost:8080/v3/api-docs.yaml |
| 🗄️ H2 Database Console | http://localhost:8080/h2-console |
