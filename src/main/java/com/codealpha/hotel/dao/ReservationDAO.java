package com.codealpha.hotel.dao;

import com.codealpha.hotel.model.Reservation;
import com.codealpha.hotel.model.Reservation.ReservationStatus;
import java.sql.SQLException;
import java.util.List;

public interface ReservationDAO {
    int                  addReservation(Reservation r)                   throws SQLException;
    Reservation          getReservationById(int id)                      throws SQLException;
    List<Reservation>    getAllReservations()                             throws SQLException;
    List<Reservation>    getReservationsByCustomer(int customerId)       throws SQLException;
    List<Reservation>    getReservationsByStatus(ReservationStatus s)    throws SQLException;
    void                 updateReservationStatus(int id, ReservationStatus s) throws SQLException;
    void                 cancelReservation(int id)                       throws SQLException;
    boolean              isRoomAvailable(int roomId, String checkIn,
                                         String checkOut)                throws SQLException;
    double               getTotalRevenue()                               throws SQLException;
}
