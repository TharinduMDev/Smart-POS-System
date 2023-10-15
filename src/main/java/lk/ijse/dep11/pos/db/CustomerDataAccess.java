package lk.ijse.dep11.pos.db;

import lk.ijse.dep11.pos.tm.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerDataAccess {

    static final PreparedStatement STM_GET_ALL_CUSTOMERS;
    static final PreparedStatement STM_INSERT_CUSTOMERS;
    static final PreparedStatement STM_UPDATE_CUSTOMERS;
    static final PreparedStatement STM_DELETE_CUSTOMER;


    static {
        try {
            Connection connection = SingleConnectionDataSource.getInstance().getConnection();
            STM_GET_ALL_CUSTOMERS = connection.prepareStatement("SELECT * FROM customer ORDER BY id");
            STM_INSERT_CUSTOMERS = connection.prepareStatement("INSERT INTO customer (id, name, address) values (?,?,?)");
            STM_UPDATE_CUSTOMERS =connection.prepareStatement("UPDATE customer SET name=?,address=? WHERE id=?");
            STM_DELETE_CUSTOMER = connection.prepareStatement("DELETE FROM customer WHERE id=?");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static List<Customer> getAllCustomers(){
        try {
            ResultSet resultSet = STM_GET_ALL_CUSTOMERS.executeQuery();
            ArrayList<Customer> customerList = new ArrayList<>();
            while(resultSet.next()){
                String customerId = resultSet.getString("id");
                String customerName = resultSet.getString("name");
                String customerAddress = resultSet.getString("address");
                Customer newCustomer = new Customer(customerId, customerName, customerAddress);
                customerList.add(newCustomer);
            }
            return customerList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void saveCustomer(Customer customer){
        String id = customer.getId();
        String name = customer.getName();
        String address = customer.getAddress();
        try {
            STM_INSERT_CUSTOMERS.setString(1,id);
            STM_INSERT_CUSTOMERS.setString(2,name);
            STM_INSERT_CUSTOMERS.setString(3,address);
            STM_INSERT_CUSTOMERS.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void updateCustomer(String customerId,String name,String address){
        try {
            STM_UPDATE_CUSTOMERS.setString(1, name);
            STM_UPDATE_CUSTOMERS.setString(2, address);
            STM_UPDATE_CUSTOMERS.setString(3, customerId);
            STM_UPDATE_CUSTOMERS.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteCustomer(String customerId){
        try {
            STM_DELETE_CUSTOMER.setString(1,customerId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
