package com.codealpha.hotel.model;

import java.time.LocalDateTime;

/**
 * Payment — Represents a payment transaction for a reservation.
 */
public class Payment {

    public enum PaymentMethod  { CASH, CREDIT_CARD, DEBIT_CARD, ONLINE }
    public enum PaymentStatus  { PENDING, COMPLETED, FAILED, REFUNDED }

    private int            id;
    private int            reservationId;
    private double         amount;
    private PaymentMethod  paymentMethod;
    private PaymentStatus  paymentStatus;
    private String         transactionId;
    private LocalDateTime  paymentDate;

    // For display
    private String customerName;
    private String roomNumber;

    // ─── Constructors ──────────────────────────────────────────────────────────
    public Payment() {}

    public Payment(int reservationId, double amount, PaymentMethod method) {
        this.reservationId = reservationId;
        this.amount        = amount;
        this.paymentMethod = method;
        this.paymentStatus = PaymentStatus.PENDING;
        this.transactionId = generateTransactionId();
        this.paymentDate   = LocalDateTime.now();
    }

    // ─── Generate unique transaction ID ───────────────────────────────────────
    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis()
               + (int)(Math.random() * 9000 + 1000);
    }

    // ─── Getters & Setters ─────────────────────────────────────────────────────
    public int           getId()                  { return id; }
    public void          setId(int id)            { this.id = id; }

    public int           getReservationId()                      { return reservationId; }
    public void          setReservationId(int reservationId)     { this.reservationId = reservationId; }

    public double        getAmount()               { return amount; }
    public void          setAmount(double amount)  { this.amount = amount; }

    public PaymentMethod getPaymentMethod()                        { return paymentMethod; }
    public void          setPaymentMethod(PaymentMethod method)    { this.paymentMethod = method; }

    public PaymentStatus getPaymentStatus()                        { return paymentStatus; }
    public void          setPaymentStatus(PaymentStatus status)    { this.paymentStatus = status; }

    public String        getTransactionId()                        { return transactionId; }
    public void          setTransactionId(String transactionId)    { this.transactionId = transactionId; }

    public LocalDateTime getPaymentDate()                          { return paymentDate; }
    public void          setPaymentDate(LocalDateTime date)        { this.paymentDate = date; }

    public String        getCustomerName()                         { return customerName; }
    public void          setCustomerName(String name)              { this.customerName = name; }

    public String        getRoomNumber()                           { return roomNumber; }
    public void          setRoomNumber(String roomNumber)          { this.roomNumber = roomNumber; }

    @Override
    public String toString() {
        return String.format(
            "Payment[TXN:%s | PKR %.0f | %s | %s | %s]",
            transactionId, amount, paymentMethod, paymentStatus, paymentDate);
    }
}
