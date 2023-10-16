package lk.ijse.dep11.pos.db;

import lk.ijse.dep11.pos.tm.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderDataAccess {
    static final PreparedStatement STM_ORDER_EXIST_BY_CUSTOMER_ID;
    static {
        Connection connection = SingleConnectionDataSource.getInstance().getConnection();
        try {
            STM_ORDER_EXIST_BY_CUSTOMER_ID =  connection.prepareStatement("SELECT * FROM \"order\" WHERE customer_id=?");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static boolean isOrderExistByCustomerId(Customer customer){
        try {
            STM_ORDER_EXIST_BY_CUSTOMER_ID.setString(1,customer.getId());
            ResultSet resultSet = STM_ORDER_EXIST_BY_CUSTOMER_ID.executeQuery();
            if(resultSet.next()){
                return true;
            }else{
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
