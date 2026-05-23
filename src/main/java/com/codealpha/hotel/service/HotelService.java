package com.codealpha.hotel.service;

import com.codealpha.hotel.dao.*;
import com.codealpha.hotel.dao.impl.*;
import com.codealpha.hotel.model.*;
import com.codealpha.hotel.model.Reservation.ReservationStatus;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * HotelService — Central business logic layer.
 * Orchestrates all DAO operations and enforces business rules.
 */
public class HotelService {

    private final RoomDAO        roomDAO;
    private final CustomerDAO    customerDAO;
    private final ReservationDAO reservationDAO;
    private final PaymentDAO     paymentDAO;

    public HotelService() {
        this.roomDAO        = new RoomDAOImpl();
        this.customerDAO    = new CustomerDAOImpl();
        this.reservationDAO = new ReservationDAOImpl();
        this.paymentDAO     = new PaymentDAOImpl();
    }

    // ═══ ROOM OPERATIONS ══════════════════════════════════════════════════════

    public List<Room> getAllRooms()                              throws SQLException { return roomDAO.getAllRooms(); }
    public List<Room> getAvailableRooms()                       throws SQLException { return roomDAO.getAvailableRooms(); }
    public List<Room> getRoomsByCategory(Room.RoomCategory cat) throws SQLException { return roomDAO.getRoomsByCategory(cat); }
    public Room       getRoomById(int id)                       throws SQLException { return roomDAO.getRoomById(id); }

    // ═══ CUSTOMER OPERATIONS ══════════════════════════════════════════════════

    public void           registerCustomer(Customer c)  throws SQLException { customerDAO.addCustomer(c); }
    public List<Customer> getAllCustomers()              throws SQLException { return customerDAO.getAllCustomers(); }
    public Customer       getCustomerById(int id)       throws SQLException { return customerDAO.getCustomerById(id); }
    public Customer       getCustomerByEmail(String e)  throws SQLException { return customerDAO.getCustomerByEmail(e); }
    public boolean        customerExists(String email)  throws SQLException { return customerDAO.customerExists(email); }

    // ═══ RESERVATION OPERATIONS ═══════════════════════════════════════════════

    /**
     * Books a room: validates availability, creates reservation, marks room unavailable.
     */
    public Reservation bookRoom(int customerId, int roomId,
                                LocalDate checkIn, LocalDate checkOut) throws SQLException {
        // Business rule: check-out must be after check-in
        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date!");
        }

        // Business rule: cannot book in the past
        if (checkIn.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past!");
        }

        // Check room availability for date range
        boolean available = reservationDAO.isRoomAvailable(roomId, checkIn.toString(), checkOut.toString());
        if (!available) {
            throw new IllegalStateException("Room is not available for the selected dates!");
        }

        Room room = roomDAO.getRoomById(roomId);
        if (room == null) throw new IllegalArgumentException("Room not found!");
        if (!room.isAvailable()) throw new IllegalStateException("Room is currently unavailable!");

        // Create reservation
        Reservation reservation = new Reservation(customerId, roomId, checkIn, checkOut, room.getPricePerNight());
        reservation.setStatus(ReservationStatus.CONFIRMED);
        int newId = reservationDAO.addReservation(reservation);

        // Mark room unavailable
        roomDAO.updateRoomAvailability(roomId, false);

        return reservationDAO.getReservationById(newId);
    }

    /**
     * Cancels a reservation and frees the room. Issues refund if payment exists.
     */
    public void cancelReservation(int reservationId) throws SQLException {
        Reservation r = reservationDAO.getReservationById(reservationId);
        if (r == null) throw new IllegalArgumentException("Reservation not found!");
        if (r.getStatus() == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("Reservation is already cancelled!");
        }
        if (r.getStatus() == ReservationStatus.CHECKED_OUT) {
            throw new IllegalStateException("Cannot cancel a checked-out reservation!");
        }

        reservationDAO.cancelReservation(reservationId);
        roomDAO.updateRoomAvailability(r.getRoomId(), true);

        // Refund if payment exists
        Payment p = paymentDAO.getPaymentByReservation(reservationId);
        if (p != null && p.getPaymentStatus() == Payment.PaymentStatus.COMPLETED) {
            paymentDAO.refundPayment(reservationId);
        }
    }

    /**
     * Checks in a guest — updates reservation status.
     */
    public void checkIn(int reservationId) throws SQLException {
        Reservation r = reservationDAO.getReservationById(reservationId);
        if (r == null) throw new IllegalArgumentException("Reservation not found!");
        if (r.getStatus() != ReservationStatus.CONFIRMED) {
            throw new IllegalStateException("Reservation must be CONFIRMED to check in!");
        }
        reservationDAO.updateReservationStatus(reservationId, ReservationStatus.CHECKED_IN);
    }

    /**
     * Checks out a guest — updates status, frees room.
     */
    public void checkOut(int reservationId) throws SQLException {
        Reservation r = reservationDAO.getReservationById(reservationId);
        if (r == null) throw new IllegalArgumentException("Reservation not found!");
        if (r.getStatus() != ReservationStatus.CHECKED_IN) {
            throw new IllegalStateException("Guest must be CHECKED_IN to check out!");
        }
        reservationDAO.updateReservationStatus(reservationId, ReservationStatus.CHECKED_OUT);
        roomDAO.updateRoomAvailability(r.getRoomId(), true);
    }

    public List<Reservation> getAllReservations()                          throws SQLException { return reservationDAO.getAllReservations(); }
    public List<Reservation> getReservationsByCustomer(int cid)          throws SQLException { return reservationDAO.getReservationsByCustomer(cid); }
    public List<Reservation> getReservationsByStatus(ReservationStatus s) throws SQLException { return reservationDAO.getReservationsByStatus(s); }
    public Reservation       getReservationById(int id)                   throws SQLException { return reservationDAO.getReservationById(id); }

    // ═══ PAYMENT OPERATIONS ═══════════════════════════════════════════════════

    /**
     * Processes payment for a reservation (simulated).
     */
    public Payment processPayment(int reservationId, Payment.PaymentMethod method) throws SQLException {
        Reservation r = reservationDAO.getReservationById(reservationId);
        if (r == null) throw new IllegalArgumentException("Reservation not found!");

        Payment existing = paymentDAO.getPaymentByReservation(reservationId);
        if (existing != null && existing.getPaymentStatus() == Payment.PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Payment already completed for this reservation!");
        }

        Payment payment = new Payment(reservationId, r.getTotalAmount(), method);
        payment.setPaymentStatus(Payment.PaymentStatus.COMPLETED); // Simulate success
        paymentDAO.processPayment(payment);
        return payment;
    }

    public List<Payment> getAllPayments() throws SQLException { return paymentDAO.getAllPayments(); }
    public double        getTotalRevenue() throws SQLException { return paymentDAO.getTotalRevenue(); }
}
