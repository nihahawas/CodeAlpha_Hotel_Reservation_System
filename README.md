# 🏨 Hotel — Reservation Management System

<img width="1522" height="983" alt="image" src="https://github.com/user-attachments/assets/bd9a29c4-94b9-4d97-bf1f-5eafe4ffd083" />

<img width="937" height="675" alt="image" src="https://github.com/user-attachments/assets/e89fa45f-2328-4542-919a-bac08698d9f8" />


### CodeAlpha Java Internship — Task 4

---

## 📋 Project Overview

A fully-featured **Hotel Reservation System** built with Java, MySQL, JDBC, and the DAO design pattern. Supports complete hotel operations including room management, customer registration, booking, check-in/check-out, payment simulation, and analytics reporting.

---

## ✨ Features

| Feature | Description |
|---------|-------------|
| 🛏️ Room Management | View all/available rooms, search by category (Standard/Deluxe/Suite) |
| 👤 Customer Management | Register guests, search by email/ID, view history |
| 📋 Reservation Management | Book, cancel, check-in, check-out |
| 💳 Payment Simulation | Cash, Credit Card, Debit Card, Online — with transaction IDs |
| 📊 Reports & Analytics | Revenue, occupancy stats, reservation summaries |
| 🎨 Colored Console UI | Beautiful ANSI-colored terminal interface |

---

## 🏗️ Project Architecture

```
CodeAlpha_HotelReservation/
├── src/main/java/com/codealpha/hotel/
│   ├── Main.java                    ← Entry point
│   ├── config/
│   │   └── DatabaseConfig.java      ← Singleton DB connection
│   ├── model/
│   │   ├── Room.java                ← Room entity (with RoomCategory enum)
│   │   ├── Customer.java            ← Customer entity
│   │   ├── Reservation.java         ← Reservation entity (with status enum)
│   │   └── Payment.java             ← Payment entity (with method/status enums)
│   ├── dao/
│   │   ├── RoomDAO.java             ← Room DAO interface
│   │   ├── CustomerDAO.java         ← Customer DAO interface
│   │   ├── ReservationDAO.java      ← Reservation DAO interface
│   │   ├── PaymentDAO.java          ← Payment DAO interface
│   │   └── impl/
│   │       ├── RoomDAOImpl.java     ← JDBC implementation
│   │       ├── CustomerDAOImpl.java ← JDBC implementation
│   │       ├── ReservationDAOImpl.java ← JDBC implementation
│   │       └── PaymentDAOImpl.java  ← JDBC implementation
│   ├── service/
│   │   └── HotelService.java        ← Business logic layer
│   └── ui/
│       └── ConsoleUI.java           ← Full ANSI console interface
├── src/main/resources/
│   └── database.properties          ← DB credentials
├── sql/
│   └── hotel_db.sql                 ← Database schema + sample data
├── pom.xml                          ← Maven config (MySQL connector)
└── README.md
```

---

## 🛠️ Technologies Used

- **Java 11+** — Core language
- **MySQL 8.x** — Database
- **JDBC** — `java.sql` for database connectivity
- **DAO Pattern** — Interface + Implementation separation
- **Maven** — Dependency management (auto-downloads MySQL connector)
- **OOP** — Encapsulation, abstraction, singleton pattern
- **ANSI Colors** — Beautiful console UI

---

## 🚀 How to Run

### Prerequisites
- JDK 11 or higher
- MySQL 8.x installed and running
- Maven installed (or use VS Code Java Extension Pack)

### Step 1 — Set up Database
```sql
-- In MySQL Workbench or terminal:
mysql -u root -p < sql/hotel_db.sql
```

### Step 2 — Configure Database
Edit `src/main/resources/database.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/hotel_db?useSSL=false&serverTimezone=UTC
db.username=root
db.password=YOUR_PASSWORD_HERE
```

### Step 3 — Run with Maven
```bash
mvn compile exec:java -Dexec.mainClass="com.codealpha.hotel.Main"
```

### OR Run with VS Code
- Open project in VS Code
- Install **Extension Pack for Java**
- Open `Main.java` → Click ▶ Run

---

## 📱 System Menu Structure

```
MAIN MENU
├── [1] Room Management
│     ├── View All Rooms
│     ├── View Available Rooms
│     ├── Search by Category
│     └── View Room Details
├── [2] Customer Management
│     ├── Register Customer
│     ├── View All Customers
│     ├── Search by Email
│     └── View by ID
├── [3] Reservation Management
│     ├── Book a Room ← validates availability
│     ├── Cancel Reservation ← auto-refund
│     ├── Check-In Guest
│     ├── Check-Out Guest
│     ├── View All Reservations
│     ├── View Details
│     └── View by Customer
├── [4] Payment Management
│     ├── Process Payment (Cash/Card/Online)
│     └── View All Payments
└── [5] Reports & Analytics
      └── Revenue, occupancy, reservation stats
```

---

## 🗄️ Database Schema

4 tables: `rooms`, `customers`, `reservations`, `payments` — with foreign key relationships and 11 pre-loaded sample rooms (Standard/Deluxe/Suite).

---

## 👤 Author

- **Name:** Niha Hawas
- **Internship:** CodeAlpha — Java Programming Task 4
- **GitHub:** https://github.com/nihahawas

---

## 📜 License
Educational project for CodeAlpha Java Internship Program.
