package com.codealpha.hotel.dao.impl;

import com.codealpha.hotel.config.DatabaseConfig;
import com.codealpha.hotel.dao.PaymentDAO;
import com.codealpha.hotel.model.Payment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAOImpl implements PaymentDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConfig.getInstance().getConnection();
    }

    private Payment mapRow(ResultSet rs) throws SQLException {
        Payment p = new Payment();
        p.setId            (rs.getInt   ("id"));
        p.setReservationId (rs.getInt   ("reservation_id"));
        p.setAmount        (rs.getDouble("amount"));
        p.setPaymentMethod (Payment.PaymentMethod.valueOf(rs.getString("payment_method")));
        p.setPaymentStatus (Payment.PaymentStatus.valueOf(rs.getString("payment_status")));
        p.setTransactionId (rs.getString("transaction_id"));
        Timestamp ts = rs.getTimestamp("payment_date");
        if (ts != null) p.setPaymentDate(ts.toLocalDateTime());
        try { p.setCustomerName(rs.getString("customer_name")); } catch (Exception ignored) {}
        try { p.setRoomNumber  (rs.getString("room_number"));   } catch (Exception ignored) {}
        return p;
    }

    @Override
    public void processPayment(Payment payment) throws SQLException {
        String sql = "INSERT INTO payments "
                   + "(reservation_id, amount, payment_method, payment_status, transaction_id) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt   (1, payment.getReservationId());
            ps.setDouble(2, payment.getAmount());
            ps.setString(3, payment.getPaymentMethod().name());
            ps.setString(4, payment.getPaymentStatus().name());
            ps.setString(5, payment.getTransactionId());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) payment.setId(keys.getInt(1));
        }
    }

    @Override
    public Payment getPaymentById(int id) throws SQLException {
        String sql = "SELECT p.*, c.name AS customer_name, r.room_number "
                   + "FROM payments p "
                   + "JOIN reservations res ON p.reservation_id = res.id "
                   + "JOIN customers c      ON res.customer_id  = c.id "
                   + "JOIN rooms r          ON res.room_id      = r.id "
                   + "WHERE p.id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? mapRow(rs) : null;
        }
    }

    @Override
    public Payment getPaymentByReservation(int reservationId) throws SQLException {
        String sql = "SELECT p.*, c.name AS customer_name, r.room_number "
                   + "FROM payments p "
                   + "JOIN reservations res ON p.reservation_id = res.id "
                   + "JOIN customers c      ON res.customer_id  = c.id "
                   + "JOIN rooms r          ON res.room_id      = r.id "
                   + "WHERE p.reservation_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? mapRow(rs) : null;
        }
    }

    @Override
    public List<Payment> getAllPayments() throws SQLException {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT p.*, c.name AS customer_name, r.room_number "
                   + "FROM payments p "
                   + "JOIN reservations res ON p.reservation_id = res.id "
                   + "JOIN customers c      ON res.customer_id  = c.id "
                   + "JOIN rooms r          ON res.room_id      = r.id "
                   + "ORDER BY p.payment_date DESC";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public void updatePaymentStatus(int id, Payment.PaymentStatus status) throws SQLException {
        String sql = "UPDATE payments SET payment_status = ? WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt   (2, id);
            ps.executeUpdate();
        }
    }

    @Override
    public void refundPayment(int reservationId) throws SQLException {
        String sql = "UPDATE payments SET payment_status = 'REFUNDED' "
                   + "WHERE reservation_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            ps.executeUpdate();
        }
    }

    @Override
    public double getTotalRevenue() throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM payments "
                   + "WHERE payment_status = 'COMPLETED'";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getDouble(1) : 0.0;
        }
    }
}
