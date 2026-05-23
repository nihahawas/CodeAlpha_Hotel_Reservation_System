package com.codealpha.hotel.dao.impl;

import com.codealpha.hotel.config.DatabaseConfig;
import com.codealpha.hotel.dao.RoomDAO;
import com.codealpha.hotel.model.Room;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAOImpl implements RoomDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConfig.getInstance().getConnection();
    }

    // ─── Map ResultSet → Room ──────────────────────────────────────────────────
    private Room mapRow(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setId(rs.getInt("id"));
        room.setRoomNumber(rs.getString("room_number"));
        room.setCategory(Room.RoomCategory.valueOf(rs.getString("category")));
        room.setPricePerNight(rs.getDouble("price_per_night"));
        room.setCapacity(rs.getInt("capacity"));
        room.setDescription(rs.getString("description"));
        room.setAvailable(rs.getBoolean("is_available"));
        return room;
    }

    @Override
    public void addRoom(Room room) throws SQLException {
        String sql = "INSERT INTO rooms (room_number, category, price_per_night, capacity, description) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, room.getRoomNumber());
            ps.setString(2, room.getCategory().name());
            ps.setDouble(3, room.getPricePerNight());
            ps.setInt   (4, room.getCapacity());
            ps.setString(5, room.getDescription());
            ps.executeUpdate();
        }
    }

    @Override
    public Room getRoomById(int id) throws SQLException {
        String sql = "SELECT * FROM rooms WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? mapRow(rs) : null;
        }
    }

    @Override
    public Room getRoomByNumber(String roomNumber) throws SQLException {
        String sql = "SELECT * FROM rooms WHERE room_number = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, roomNumber);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? mapRow(rs) : null;
        }
    }

    @Override
    public List<Room> getAllRooms() throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms ORDER BY category, room_number";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) rooms.add(mapRow(rs));
        }
        return rooms;
    }

    @Override
    public List<Room> getAvailableRooms() throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE is_available = TRUE ORDER BY category, price_per_night";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) rooms.add(mapRow(rs));
        }
        return rooms;
    }

    @Override
    public List<Room> getRoomsByCategory(Room.RoomCategory category) throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE category = ? ORDER BY price_per_night";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, category.name());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) rooms.add(mapRow(rs));
        }
        return rooms;
    }

    @Override
    public void updateRoomAvailability(int id, boolean available) throws SQLException {
        String sql = "UPDATE rooms SET is_available = ? WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setBoolean(1, available);
            ps.setInt    (2, id);
            ps.executeUpdate();
        }
    }

    @Override
    public void updateRoom(Room room) throws SQLException {
        String sql = "UPDATE rooms SET room_number=?, category=?, price_per_night=?, "
                   + "capacity=?, description=?, is_available=? WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString (1, room.getRoomNumber());
            ps.setString (2, room.getCategory().name());
            ps.setDouble (3, room.getPricePerNight());
            ps.setInt    (4, room.getCapacity());
            ps.setString (5, room.getDescription());
            ps.setBoolean(6, room.isAvailable());
            ps.setInt    (7, room.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void deleteRoom(int id) throws SQLException {
        String sql = "DELETE FROM rooms WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
