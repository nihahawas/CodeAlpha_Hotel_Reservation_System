-- ============================================================
--  CodeAlpha Hotel Reservation System — Database Setup
--  Run this file in MySQL Workbench or command line:
--  mysql -u root -p < hotel_db.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS hotel_db;
USE hotel_db;

-- ── ROOMS TABLE ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS rooms (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    room_number      VARCHAR(10)  UNIQUE NOT NULL,
    category         ENUM('STANDARD','DELUXE','SUITE') NOT NULL,
    price_per_night  DECIMAL(10,2) NOT NULL,
    capacity         INT NOT NULL,
    description      TEXT,
    is_available     BOOLEAN DEFAULT TRUE,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ── CUSTOMERS TABLE ──────────────────────────────────────────
CREATE TABLE IF NOT EXISTS customers (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(100) UNIQUE NOT NULL,
    phone       VARCHAR(20)  NOT NULL,
    address     TEXT,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ── RESERVATIONS TABLE ───────────────────────────────────────
CREATE TABLE IF NOT EXISTS reservations (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    customer_id     INT NOT NULL,
    room_id         INT NOT NULL,
    check_in_date   DATE NOT NULL,
    check_out_date  DATE NOT NULL,
    total_nights    INT NOT NULL,
    total_amount    DECIMAL(10,2) NOT NULL,
    status          ENUM('PENDING','CONFIRMED','CHECKED_IN','CHECKED_OUT','CANCELLED') DEFAULT 'PENDING',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    FOREIGN KEY (room_id)     REFERENCES rooms(id)     ON DELETE CASCADE
);

-- ── PAYMENTS TABLE ───────────────────────────────────────────
CREATE TABLE IF NOT EXISTS payments (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    reservation_id   INT NOT NULL,
    amount           DECIMAL(10,2) NOT NULL,
    payment_method   ENUM('CASH','CREDIT_CARD','DEBIT_CARD','ONLINE') NOT NULL,
    payment_status   ENUM('PENDING','COMPLETED','FAILED','REFUNDED') DEFAULT 'PENDING',
    transaction_id   VARCHAR(50) UNIQUE,
    payment_date     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (reservation_id) REFERENCES reservations(id) ON DELETE CASCADE
);

-- ── SAMPLE ROOM DATA ─────────────────────────────────────────
INSERT INTO rooms (room_number, category, price_per_night, capacity, description) VALUES
-- Standard Rooms
('101', 'STANDARD', 5000.00, 2, 'Cozy room with twin beds, AC, TV, and free WiFi'),
('102', 'STANDARD', 5000.00, 2, 'Standard room with double bed, AC, and city view'),
('103', 'STANDARD', 5500.00, 3, 'Standard family room with extra bed, AC, and TV'),
('104', 'STANDARD', 4500.00, 1, 'Compact single room, AC, work desk, and WiFi'),
-- Deluxe Rooms
('201', 'DELUXE',   9000.00, 2, 'Luxurious room with king bed, mini bar, and garden view'),
('202', 'DELUXE',   9500.00, 2, 'Deluxe room with queen bed, jacuzzi, and mountain view'),
('203', 'DELUXE',  10000.00, 4, 'Spacious family deluxe with two king beds and lounge'),
('204', 'DELUXE',   9000.00, 2, 'Modern deluxe room with smart TV, mini bar, city view'),
-- Suites
('301', 'SUITE',   18000.00, 2, 'Presidential suite with living room, dining, and private balcony'),
('302', 'SUITE',   15000.00, 2, 'Executive suite with workspace, lounge, and panoramic view'),
('303', 'SUITE',   20000.00, 6, 'Royal family suite with 3 bedrooms and private pool');
