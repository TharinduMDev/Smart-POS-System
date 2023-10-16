package lk.ijse.dep11.pos.db;

import lk.ijse.dep11.pos.tm.Customer;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomerDataAccessTest {

    @BeforeEach
    void setUp() throws SQLException {
        SingleConnectionDataSource.getInstance().getConnection().setAutoCommit(false);
    }

    @AfterEach
    void tearDown() throws SQLException {
        SingleConnectionDataSource.getInstance().getConnection().rollback();
        SingleConnectionDataSource.getInstance().getConnection().setAutoCommit(true);
    }

    @Test
    void getAllCustomers() {
        assertDoesNotThrow(()->{
            CustomerDataAccess.saveCustomer(new Customer("123","kasun","galle"));
            CustomerDataAccess.saveCustomer(new Customer("345","Ruwan","colombo"));
        });
        assertDoesNotThrow(()->{
            List<Customer> allCustomers = CustomerDataAccess.getAllCustomers();
            assertTrue(allCustomers.size() >= 2);
        });
    }

    @Test
    void saveCustomer() {
        assertDoesNotThrow(()->{
            CustomerDataAccess.saveCustomer(new Customer("123","kasun","galle"));
            CustomerDataAccess.saveCustomer(new Customer("345","Ruwan","colombo"));
        });
        assertThrows(SQLException.class, ()-> CustomerDataAccess.saveCustomer(new Customer("345","Ruwan","colombo")));
    }

    @Test
    void updateCustomer() {
            CustomerDataAccess.saveCustomer(new Customer("123","kasun","galle"));
            CustomerDataAccess.saveCustomer(new Customer("345","Ruwan","colombo"));
        assertDoesNotThrow(()->{
            CustomerDataAccess.updateCustomer(new Customer("123","kamal","kandy"));
        });
    }

    @Test
    void deleteCustomer() {
        CustomerDataAccess.saveCustomer(new Customer("123","kasun","galle"));
        assertDoesNotThrow(()->{
            CustomerDataAccess.deleteCustomer("123");
        });
    }
}