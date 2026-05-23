package com.codealpha.hotel.dao.impl;

import com.codealpha.hotel.config.DatabaseConfig;
import com.codealpha.hotel.dao.ReservationDAO;
import com.codealpha.hotel.model.Reservation;
import com.codealpha.hotel.model.Reservation.ReservationStatus;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAOImpl implements ReservationDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConfig.getInstance().getConnection();
    }

    private Reservation mapRow(ResultSet rs) throws SQLException {
        Reservation r = new Reservation();
        r.setId           (rs.getInt   ("id"));
        r.setCustomerId   (rs.getInt   ("customer_id"));
        r.setRoomId       (rs.getInt   ("room_id"));
        r.setCheckInDate  (rs.getDate  ("check_in_date").toLocalDate());
        r.setCheckOutDate (rs.getDate  ("check_out_date").toLocalDate());
        r.setTotalNights  (rs.getInt   ("total_nights"));
        r.setTotalAmount  (rs.getDouble("total_amount"));
        r.setStatus       (ReservationStatus.valueOf(rs.getString("status")));

        // Joined fields (may be null if not joined)
        try { r.setCustomerName(rs.getString("customer_name")); } catch (Exception ignored) {}
        try { r.setRoomNumber  (rs.getString("room_number"));   } catch (Exception ignored) {}
        try { r.setRoomCategory(rs.getString("category"));      } catch (Exception ignored) {}
        return r;
    }

    @Override
    public int addReservation(Reservation r) throws SQLException {
        String sql = "INSERT INTO reservations "
                   + "(customer_id, room_id, check_in_date, check_out_date, "
                   + "total_nights, total_amount, status) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt   (1, r.getCustomerId());
            ps.setInt   (2, r.getRoomId());
            ps.setDate  (3, Date.valueOf(r.getCheckInDate()));
            ps.setDate  (4, Date.valueOf(r.getCheckOutDate()));
            ps.setInt   (5, r.getTotalNights());
            ps.setDouble(6, r.getTotalAmount());
            ps.setString(7, r.getStatus().name());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int newId = keys.getInt(1);
                r.setId(newId);
                return newId;
            }
        }
        return -1;
    }

    @Override
    public Reservation getReservationById(int id) throws SQLException {
        String sql = "SELECT r.*, c.name AS customer_name, rm.room_number, rm.category "
                   + "FROM reservations r "
                   + "JOIN customers c  ON r.customer_id = c.id "
                   + "JOIN rooms rm     ON r.room_id = rm.id "
                   + "WHERE r.id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? mapRow(rs) : null;
        }
    }

    @Override
    public List<Reservation> getAllReservations() throws SQLException {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT r.*, c.name AS customer_name, rm.room_number, rm.category "
                   + "FROM reservations r "
                   + "JOIN customers c  ON r.customer_id = c.id "
                   + "JOIN rooms rm     ON r.room_id = rm.id "
                   + "ORDER BY r.created_at DESC";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public List<Reservation> getReservationsByCustomer(int customerId) throws SQLException {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT r.*, c.name AS customer_name, rm.room_number, rm.category "
                   + "FROM reservations r "
                   + "JOIN customers c  ON r.customer_id = c.id "
                   + "JOIN rooms rm     ON r.room_id = rm.id "
                   + "WHERE r.customer_id = ? ORDER BY r.check_in_date DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public List<Reservation> getReservationsByStatus(ReservationStatus status) throws SQLException {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT r.*, c.name AS customer_name, rm.room_number, rm.category "
                   + "FROM reservations r "
                   + "JOIN customers c  ON r.customer_id = c.id "
                   + "JOIN rooms rm     ON r.room_id = rm.id "
                   + "WHERE r.status = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, status.name());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public void updateReservationStatus(int id, ReservationStatus status) throws SQLException {
        String sql = "UPDATE reservations SET status = ? WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt   (2, id);
            ps.executeUpdate();
        }
    }

    @Override
    public void cancelReservation(int id) throws SQLException {
        updateReservationStatus(id, ReservationStatus.CANCELLED);
    }

    @Override
    public boolean isRoomAvailable(int roomId, String checkIn, String checkOut) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reservations "
                   + "WHERE room_id = ? AND status NOT IN ('CANCELLED','CHECKED_OUT') "
                   + "AND NOT (check_out_date <= ? OR check_in_date >= ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt   (1, roomId);
            ps.setString(2, checkIn);
            ps.setString(3, checkOut);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) == 0;
        }
    }

    @Override
    public double getTotalRevenue() throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM reservations "
                   + "WHERE status NOT IN ('CANCELLED', 'PENDING')";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getDouble(1) : 0.0;
        }
    }
}
