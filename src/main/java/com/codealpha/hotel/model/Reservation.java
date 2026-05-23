package com.codealpha.hotel.model;

import java.time.LocalDate;

/**
 * Reservation — Represents a hotel booking.
 */
public class Reservation {

    public enum ReservationStatus {
        PENDING, CONFIRMED, CHECKED_IN, CHECKED_OUT, CANCELLED
    }

    private int                id;
    private int                customerId;
    private int                roomId;
    private LocalDate          checkInDate;
    private LocalDate          checkOutDate;
    private int                totalNights;
    private double             totalAmount;
    private ReservationStatus  status;

    // For display purposes (joined data)
    private String customerName;
    private String roomNumber;
    private String roomCategory;

    // ─── Constructors ──────────────────────────────────────────────────────────
    public Reservation() {}

    public Reservation(int customerId, int roomId, LocalDate checkInDate,
                       LocalDate checkOutDate, double pricePerNight) {
        this.customerId   = customerId;
        this.roomId       = roomId;
        this.checkInDate  = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalNights  = (int) (checkOutDate.toEpochDay() - checkInDate.toEpochDay());
        this.totalAmount  = this.totalNights * pricePerNight;
        this.status       = ReservationStatus.PENDING;
    }

    // ─── Getters & Setters ─────────────────────────────────────────────────────
    public int  getId()             { return id; }
    public void setId(int id)       { this.id = id; }

    public int  getCustomerId()                  { return customerId; }
    public void setCustomerId(int customerId)    { this.customerId = customerId; }

    public int  getRoomId()              { return roomId; }
    public void setRoomId(int roomId)    { this.roomId = roomId; }

    public LocalDate getCheckInDate()                      { return checkInDate; }
    public void      setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }

    public LocalDate getCheckOutDate()                       { return checkOutDate; }
    public void      setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }

    public int  getTotalNights()                 { return totalNights; }
    public void setTotalNights(int totalNights)  { this.totalNights = totalNights; }

    public double getTotalAmount()                  { return totalAmount; }
    public void   setTotalAmount(double amount)     { this.totalAmount = amount; }

    public ReservationStatus getStatus()                       { return status; }
    public void              setStatus(ReservationStatus st)   { this.status = st; }

    public String getCustomerName()                      { return customerName; }
    public void   setCustomerName(String customerName)   { this.customerName = customerName; }

    public String getRoomNumber()                    { return roomNumber; }
    public void   setRoomNumber(String roomNumber)   { this.roomNumber = roomNumber; }

    public String getRoomCategory()                      { return roomCategory; }
    public void   setRoomCategory(String roomCategory)   { this.roomCategory = roomCategory; }

    @Override
    public String toString() {
        return String.format(
            "Reservation[ID:%d | Customer:%s | Room:%s | %s to %s | %d nights | PKR %.0f | %s]",
            id, customerName, roomNumber, checkInDate, checkOutDate,
            totalNights, totalAmount, status);
    }
}
