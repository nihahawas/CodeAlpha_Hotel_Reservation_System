package com.codealpha.hotel.dao;

import com.codealpha.hotel.model.Customer;
import java.sql.SQLException;
import java.util.List;

public interface CustomerDAO {
    void           addCustomer(Customer customer)       throws SQLException;
    Customer       getCustomerById(int id)              throws SQLException;
    Customer       getCustomerByEmail(String email)     throws SQLException;
    List<Customer> getAllCustomers()                    throws SQLException;
    void           updateCustomer(Customer customer)    throws SQLException;
    void           deleteCustomer(int id)               throws SQLException;
    boolean        customerExists(String email)         throws SQLException;
}
