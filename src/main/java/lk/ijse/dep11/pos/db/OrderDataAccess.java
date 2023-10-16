package lk.ijse.dep11.pos.db;

import lk.ijse.dep11.pos.tm.Customer;
import lk.ijse.dep11.pos.tm.Item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderDataAccess {
    static final PreparedStatement STM_ORDER_EXIST_BY_CUSTOMER_ID;
    static final PreparedStatement STM_ORDER_EXIST_BY_ITEM_ID;
    static {
        Connection connection = SingleConnectionDataSource.getInstance().getConnection();
        try {
            STM_ORDER_EXIST_BY_CUSTOMER_ID =  connection.prepareStatement("SELECT * FROM \"order\" WHERE customer_id=?");
            STM_ORDER_EXIST_BY_ITEM_ID = connection.prepareStatement("SELECT * FROM order_item WHERE item_code=?");
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

    public static boolean isExistsOrderByItemCode(String itemCode){
        try {
            STM_ORDER_EXIST_BY_ITEM_ID.setString(1,itemCode);
            return (STM_ORDER_EXIST_BY_ITEM_ID.executeQuery().next());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
