# 🏨 Hotel Reservation System

A professional **Java Console-Based Hotel Reservation System** developed as **Task 4** for the **CodeAlpha Java Programming Internship**.

The application allows users to search available rooms, make reservations, cancel bookings, simulate payments, and manage hotel reservations using Object-Oriented Programming principles and persistent file storage.

---

## 📌 Features

### 🔍 Room Search
- Search rooms by:
  - Check-in & Check-out dates
  - Room Category
  - Guest Capacity
- Displays available rooms with:
  - Room Number
  - Room Type
  - Capacity
  - Price
  - Amenities

---

### 🛏 Reservation Management

- Book hotel rooms
- Automatic Reservation ID generation
- View booking details
- Cancel reservations
- Refund simulation for paid bookings

---

### 💳 Payment Simulation

- Simulated Credit/Debit Card Payment
- Pending Payment Option
- Payment Validation
- Automatic Payment Status Update

Payment Status:
- Pending
- Paid
- Refunded

---

### 🏨 Room Categories

- Standard Room
- Deluxe Room
- Suite Room

Each room type has:
- Different pricing strategy
- Different amenities
- Different room capacity

---

### 📂 Persistent Storage

The application automatically stores data using CSV files.

Files generated:

```
data/
│
├── rooms.csv
└── reservations.csv
```

Data remains available after restarting the application.

---

## 💡 OOP Concepts Used

- Encapsulation
- Inheritance
- Polymorphism
- Abstraction
- Composition
- Enums
- Exception Handling

---

## 📁 Project Structure

```
src
│
├── main
│
├── model
│   ├── Room.java
│   ├── StandardRoom.java
│   ├── DeluxeRoom.java
│   ├── SuiteRoom.java
│   ├── Guest.java
│   ├── Reservation.java
│   ├── RoomType.java
│   ├── PaymentStatus.java
│   └── ReservationStatus.java
│
├── repository
│   └── FilePersistence.java
│
├── service
│   └── HotelService.java
│
├── HotelApp.java
└── HotelTest.java
```

---

## ⚙ Technologies Used

- Java 17
- Maven
- IntelliJ IDEA
- Java Collections Framework
- Java File I/O
- Java Time API
- Object-Oriented Programming (OOP)

---

## 🧠 Functionalities

- Search available rooms
- Filter by room type
- Filter by capacity
- Book rooms
- View reservation details
- Cancel reservation
- Payment simulation
- Booking receipt generation
- Reservation history
- CSV file persistence
- Automated verification tests

---

## ▶️ How to Run

### Clone Repository

```bash
git clone https://github.com/yourusername/CodeAlpha_HotelReservationSystem.git
```

### Open Project

Open the project in IntelliJ IDEA.

### Run

Execute:

```
HotelApp.java
```

To run automated verification tests:

```
HotelTest.java
```

---

## 🧪 Test Coverage

The project includes automated tests for:

- Room pricing (Polymorphism)
- Booking overlap validation
- File persistence
- Reservation loading
- Availability checking

---

## 📷 Console Preview

```
==================================================
        Welcome to Luxe Haven Resorts
==================================================

1. Search Available Rooms
2. Book a Room
3. View Booking Details
4. Cancel Reservation
5. View All Reservations
6. Exit
```

---

## 🚀 Future Improvements

- JavaFX GUI
- Database (MySQL/PostgreSQL)
- User Authentication
- Admin Dashboard
- Online Payment Gateway
- Email Confirmation
- QR Code Booking Receipt
- Booking Reports
- Customer Management
- Hotel Analytics

---

## 👨‍💻 Author

**Arnav Verma**

AI & Machine Learning Student

GitHub: https://github.com/yourusername

LinkedIn: https://linkedin.com/in/yourprofile

---

## 📜 License

This project was developed for educational purposes as part of the **CodeAlpha Java Programming Internship**.
