package com.codealpha.hotel.dao;

import com.codealpha.hotel.model.Payment;
import java.sql.SQLException;
import java.util.List;

public interface PaymentDAO {
    void          processPayment(Payment payment)              throws SQLException;
    Payment       getPaymentById(int id)                       throws SQLException;
    Payment       getPaymentByReservation(int reservationId)   throws SQLException;
    List<Payment> getAllPayments()                             throws SQLException;
    void          updatePaymentStatus(int id,
                      Payment.PaymentStatus status)            throws SQLException;
    void          refundPayment(int reservationId)             throws SQLException;
    double        getTotalRevenue()                            throws SQLException;
}
