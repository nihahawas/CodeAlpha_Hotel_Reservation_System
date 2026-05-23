package com.codealpha.hotel.dao;

import com.codealpha.hotel.model.Room;
import java.sql.SQLException;
import java.util.List;

public interface RoomDAO {
    void         addRoom(Room room)                          throws SQLException;
    Room         getRoomById(int id)                         throws SQLException;
    Room         getRoomByNumber(String roomNumber)          throws SQLException;
    List<Room>   getAllRooms()                               throws SQLException;
    List<Room>   getAvailableRooms()                        throws SQLException;
    List<Room>   getRoomsByCategory(Room.RoomCategory cat)  throws SQLException;
    void         updateRoomAvailability(int id, boolean av) throws SQLException;
    void         updateRoom(Room room)                      throws SQLException;
    void         deleteRoom(int id)                         throws SQLException;
}
