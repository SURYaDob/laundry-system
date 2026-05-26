<p align="center">
  <img src="https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 17"/>
  <img src="https://img.shields.io/badge/Spring_Boot-3.2.5-6DB33F?style=for-the-badge&logo=spring&logoColor=white" alt="Spring Boot 3.2.5"/>
  <img src="https://img.shields.io/badge/Thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white" alt="Thymeleaf"/>
  <img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL"/>
  <img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" alt="Maven"/>
  <img src="https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge" alt="License MIT"/>
</p>

<h1 align="center">🧺 AquaClean Luxe</h1>
<p align="center">
  <em>A full-featured Laundry Management System with role-based dashboards, real-time order tracking, and Swagger-documented REST APIs.</em>
</p>

<p align="center">
  <a href="#-features">Features</a> •
  <a href="#-tech-stack">Tech Stack</a> •
  <a href="#-quick-start">Quick Start</a> •
  <a href="#-demo-accounts">Demo Accounts</a> •
  <a href="#-architecture">Architecture</a> •
  <a href="#-api-reference">API Reference</a> •
  <a href="#-testing">Testing</a>
</p>

---

## ✨ Features

| Role | Capabilities |
|------|-------------|
| 👑 **Admin** | Full control — manage orders, services, staff, view analytics & reports, download invoices |
| 👔 **Staff** | View assigned tasks, update order progress (Picked Up → Washing → Ironing → Ready → Delivered) |
| 👤 **Customer** | Place new orders, view order history, download PDF invoices, track real-time order status |

### 🖥️ UI Highlights
- **Dark theme** with neon cyan, purple, and emerald accents
- **Glassmorphism** card effects with animated particles
- Role-specific dashboards with **Chart.js** analytics
- **Toast notifications** and scroll animations
- Responsive layout — works on desktop & tablet

### 📋 Order Lifecycle
```
PENDING → PICKED_UP → WASHING → IRONING → READY → OUT_FOR_DELIVERY → DELIVERED
                                                                         ↕
                                                                     CANCELLED
```

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| **Backend** | Java 17, Spring Boot 3.2.5, Spring MVC, Spring Data JPA |
| **Security** | Spring Security 6, BCrypt, Role-based access (RBAC) |
| **Frontend** | Thymeleaf, HTML5, CSS3, JavaScript, Chart.js |
| **Database** | H2 (dev), MySQL (prod) |
| **API Documentation** | SpringDoc OpenAPI (Swagger UI) |
| **Build Tool** | Maven 3.8+ |
| **PDF Generation** | iTextPDF |
| **Testing** | JUnit 5, Mockito, Spring Security Test |

---

## 🚀 Quick Start

### Prerequisites
- **Java JDK 17+** — verify with `java -version`
- **Maven 3.8+** — or use the included `mvnw.cmd` wrapper
- **Browser** — Chrome, Edge, or Firefox

### Run the Application

```bash
# Clone the repository
git clone https://github.com/SURYaDob/laundry-system.git
cd laundry-system

# Run with Maven
./mvnw spring-boot:run    # macOS / Linux
mvnw.cmd spring-boot:run  # Windows
```

The app starts on **http://localhost:8080** with demo data pre-loaded (no setup required).

> **💡 Dev mode (default):** Uses H2 in-memory database — zero configuration needed.  
> **⚙️ Production:** Switch to MySQL by setting `spring.profiles.active=prod` in `application.properties`.

### Import into Eclipse (Optional)

1. **File → Import → Maven → Existing Maven Projects**
2. Browse to the project directory
3. Select `pom.xml` → **Finish**
4. Run `LaundrySystemApplication.java` as **Spring Boot App**

---

## 🎭 Demo Accounts

The app comes pre-loaded with demo data. Log in using any of these accounts:

| Role | Email | Password | Name |
|------|-------|----------|------|
| 👑 **ADMIN** | `admin@aquaclean.com` | `password123` | Arjun Sharma |
| 👔 **STAFF** | `raj@aquaclean.com` | `password123` | Raj Verma (Washing & Dry Cleaning) |
| 👔 **STAFF** | `priya@aquaclean.com` | `password123` | Priya Patel (Ironing & Folding) |
| 👤 **CUSTOMER** | `amit@example.com` | `password123` | Amit Kumar |
| 👤 **CUSTOMER** | `neha@example.com` | `password123` | Neha Singh |

**All accounts use the same password:** `password123`

> New user registrations are automatically assigned the **CUSTOMER** role.

### 📦 Pre-loaded Demo Data
- **5 laundry services** — Standard Wash (₹80/kg), Dry Clean (₹150/kg), Ironing Only (₹40/kg), Wash & Fold (₹100/kg), Premium Care (₹200/kg)
- **2 staff members** with specializations
- **2 customers** with addresses and phone numbers
- **14 sample orders** spanning 2 months with various statuses, payments, invoices, and notifications

---

## 🗺️ Pages by Role

### 👑 Admin Pages

| Page | URL |
|------|-----|
| Dashboard | `/admin/dashboard` |
| Manage Orders | `/admin/orders` |
| Order Details | `/admin/orders/{id}` |
| Manage Services | `/admin/services` |
| Manage Staff | `/admin/staff` |
| Analytics & Reports | `/admin/reports` |

### 👔 Staff Pages

| Page | URL |
|------|-----|
| Dashboard | `/staff/dashboard` |
| Assigned Tasks | `/staff/orders` |
| Task Details | `/staff/orders/{id}` |

### 👤 Customer Pages

| Page | URL |
|------|-----|
| Dashboard | `/customer/dashboard` |
| Place New Order | `/customer/orders/new` |
| My Orders | `/customer/orders` |

### Utility Pages

| Page | URL |
|------|-----|
| Access Denied | `/access-denied` |
| H2 Console | `/h2-console` |

> 💡 **Tip:** The `/dashboard` endpoint auto-redirects you to the correct role-specific dashboard based on your login.

---

## 🏗️ Architecture

```
src/
├── main/
│   ├── java/com/laundry/system/
│   │   ├── LaundrySystemApplication.java    # 🚀 Entry point
│   │   ├── config/                          # Security, JPA, Swagger, Data seeding
│   │   ├── controller/                      # Thymeleaf page controllers
│   │   │   ├── api/                         # REST API controllers
│   │   │   ├── AuthController.java          # Login & registration
│   │   │   ├── DashboardController.java     # Role-based dashboards
│   │   │   ├── OrderController.java         # Admin order management
│   │   │   ├── CustomerController.java      # Customer views
│   │   │   ├── StaffController.java         # Staff management (CRUD)
│   │   │   ├── StaffOperationsController.java # Staff task operations
│   │   │   ├── ServiceController.java       # Service management (CRUD)
│   │   │   ├── InvoiceController.java       # PDF invoice download
│   │   │   └── ReportsController.java       # Analytics & reports
│   │   ├── entity/                          # JPA entities (User, Order, Service, etc.)
│   │   ├── repository/                      # Spring Data JPA repositories
│   │   ├── service/                         # Business logic layer
│   │   ├── dto/                             # Data transfer objects
│   │   └── exception/                       # Global error handling
│   └── resources/
│       ├── application.properties           # App configuration
│       ├── static/css/dashboard.css         # Dark theme styles
│       ├── static/js/dashboard.js           # Charts & interactions
│       └── templates/                       # 16 Thymeleaf views
└── test/                                    # 100+ unit & integration tests
```

---

## 📖 API Reference

### REST API Endpoints

#### Orders (`/api/orders`)

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `GET` | `/api/orders` | ADMIN/STAFF | List all orders |
| `GET` | `/api/orders/{id}` | Any logged-in | Get order by ID |
| `GET` | `/api/orders/number/{orderNumber}` | Any logged-in | Get order by order number |
| `POST` | `/api/orders` | CUSTOMER | Place a new order |
| `PATCH` | `/api/orders/{id}/status` | ADMIN/STAFF | Update order status |
| `PATCH` | `/api/orders/{id}/assign` | ADMIN | Assign staff to order |
| `DELETE` | `/api/orders/{id}` | ADMIN | Delete an order |

#### Services (`/api/services`)

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `GET` | `/api/services` | Public | List all services |
| `GET` | `/api/services/{id}` | Public | Get service by ID |
| `POST` | `/api/services` | ADMIN | Create a service |
| `PUT` | `/api/services/{id}` | ADMIN | Update a service |
| `DELETE` | `/api/services/{id}` | ADMIN | Delete a service |

### Interactive API Docs

Once running, access **Swagger UI** at:

👉 **http://localhost:8080/swagger-ui/index.html**

API specs also available in:
- **JSON:** `http://localhost:8080/v3/api-docs`
- **YAML:** `http://localhost:8080/v3/api-docs.yaml`

### Sample API Calls

```bash
# Public — list all services
curl http://localhost:8080/api/services

# Authenticated — place an order (needs login session cookie)
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "pickupDate": "2026-06-10T10:00:00",
    "items": [
      {"serviceId": 1, "quantity": 2.0},
      {"serviceId": 2, "quantity": 1.5}
    ]
  }'
```

### 🧪 Testing APIs with Swagger UI

1. **Log in** via the web app at `http://localhost:8080/login` using any demo account
2. **Open** `http://localhost:8080/swagger-ui/index.html` in the **same browser** (session cookie is shared)
3. **Browse endpoints** — expand any section (Orders, Services)
4. **Try it out** — click **Try it out**, enter parameters, click **Execute**

**Quick test sequence (recommended):**

| Step | Endpoint | What to Enter | Expected Result |
|------|----------|---------------|----------------|
| 1️⃣ | `GET /api/services` | Nothing (public) | ✅ 200 — JSON array of 5 services |
| 2️⃣ | `POST /api/orders` | JSON body (see above) | ✅ 201 — Order created |
| 3️⃣ | `PATCH /api/orders/{id}/status` | id=1, status=PICKED_UP | ✅ 200 — Status updated |
| 4️⃣ | `DELETE /api/orders/{id}` | id=1 | ✅ 204 — Order deleted |

> **🔐 Authenticated endpoints:** Swagger uses your browser's login session automatically — no need to manually pass tokens.

---

## 🧪 Testing

```bash
# Run ALL tests
./mvnw test

# Run a specific test class
./mvnw test -Dtest=OrderServiceImplTest

# Run tests with verbose output
./mvnw test -Dtest=OrderApiControllerTest -Dspring.test.console.output=always
```

All **100+ tests** cover:
- ✅ **Controllers** — 12 controller test classes (web layer)
- ✅ **Services** — 6 service implementation test classes (business logic)
- ✅ **Repositories** — 3 repository test classes (data access)
- ✅ **Security** — Authentication, authorization, role-based access

---

## 🔗 Quick Links

| Page | URL |
|------|-----|
| 🏠 Login | `/login` |
| 📝 Register | `/register` |
| 📊 Dashboard | `/dashboard` |
| 📖 Swagger UI | `/swagger-ui/index.html` |
| 📄 OpenAPI JSON | `/v3/api-docs` |
| 🗄️ H2 Console | `/h2-console` |

> **H2 Console:** JDBC URL: `jdbc:h2:mem:laundrydb` | Username: `root` | Password: `root`

---

## ⚙️ Configuration

### Switch to Production (MySQL)

1. Open `src/main/resources/application.properties`
2. Change `spring.profiles.active=dev` to `spring.profiles.active=prod`
3. Update MySQL credentials in `application-prod.properties`

### Change Port

```properties
server.port=9090
```

### Database Schema

The app uses **JPA auto-DDL** (`spring.jpa.hibernate.ddl-auto=update`) — tables are created and updated automatically based on entity classes. No manual SQL scripts needed.

---

## 🐞 Troubleshooting

| Problem | Solution |
|---------|----------|
| **Port 8080 in use** | Change `server.port` in `application.properties` |
| **Styling broken** | Hard refresh: **Ctrl+F5** or **Ctrl+Shift+R** |
| **H2 Console 403** | Ensure `spring.profiles.active=dev` |
| **White label error** | Check role-based access — you may not have permission |

---

<p align="center">
  Built with ❤️ using Spring Boot &middot; Powered by ☕ & 🧺
</p>
