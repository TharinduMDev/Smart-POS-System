package lk.ijse.dep11.pos.db;

import lk.ijse.dep11.pos.tm.Customer;
import lk.ijse.dep11.pos.tm.Item;
import lk.ijse.dep11.pos.tm.OrderItem;

import java.sql.*;
import java.util.List;

public class OrderDataAccess {
    static final PreparedStatement STM_ORDER_EXIST_BY_CUSTOMER_ID;
    static final PreparedStatement STM_ORDER_EXIST_BY_ITEM_ID;
    static final PreparedStatement STM_GET_LAST_ORDER_ID;
    static final PreparedStatement STM_INSERT_ORDER;
    static final PreparedStatement  STM_INSERT_ORDER_ITEM;
    static final PreparedStatement  STM_UPDATE_STOCK;


    static {
        Connection connection = SingleConnectionDataSource.getInstance().getConnection();
        try {
            STM_ORDER_EXIST_BY_CUSTOMER_ID =  connection.prepareStatement("SELECT * FROM \"order\" WHERE customer_id=?");
            STM_ORDER_EXIST_BY_ITEM_ID = connection.prepareStatement("SELECT * FROM order_item WHERE item_code=?");
            STM_GET_LAST_ORDER_ID = connection.prepareStatement("SELECT id FROM \"order\" ORDER BY id DESC FETCH FIRST ROWS ONLY ");
            STM_INSERT_ORDER = connection.prepareStatement("INSERT INTO \"order\" (id, date, customer_id) VALUES (?,?,?)");
            STM_INSERT_ORDER_ITEM = connection.prepareStatement("INSERT INTO order_item (order_id, item_code, qty, unit_price) VALUES (?,?,?,?)");
            STM_UPDATE_STOCK = connection.prepareStatement("UPDATE item SET qty=? WHERE code=?");
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
    public static String getLastOrderId(){

        try {
            ResultSet resultSet = STM_GET_LAST_ORDER_ID.executeQuery();
            if(resultSet.next()){
                String orderId = String.format("OD%03d",Integer.parseInt(resultSet.getString("id").strip().substring(2))+1);
                return orderId;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void saveOrder(String orderId, Date orderDate, String customerId, List<OrderItem> orderItemList)throws SQLException{
        SingleConnectionDataSource.getInstance().getConnection().setAutoCommit(false);
        try {
            /* 1. Save Order */
            STM_INSERT_ORDER.setString(1, orderId);
            STM_INSERT_ORDER.setDate(2, orderDate);
            STM_INSERT_ORDER.setString(3, customerId);
            STM_INSERT_ORDER.executeUpdate();

            /* 2. Save Order Item List */
            /* 3. Update the Stock of each Order Item */
            for (OrderItem orderItem : orderItemList) {
                STM_INSERT_ORDER_ITEM.setString(1, orderId);
                STM_INSERT_ORDER_ITEM.setString(2, orderItem.getCode());
                STM_INSERT_ORDER_ITEM.setInt(3, orderItem.getQty());
                STM_INSERT_ORDER_ITEM.setBigDecimal(4, orderItem.getUnitPrice());
                STM_INSERT_ORDER_ITEM.executeUpdate();

                STM_UPDATE_STOCK.setInt(1, orderItem.getQty());
                STM_UPDATE_STOCK.setString(2, orderItem.getCode());
                STM_UPDATE_STOCK.executeUpdate();
            }

            SingleConnectionDataSource.getInstance().getConnection().commit();
        }catch (Throwable t){
            SingleConnectionDataSource.getInstance().getConnection().rollback();
            throw new SQLException(t);
        }finally{
            SingleConnectionDataSource.getInstance().getConnection().setAutoCommit(true);
        }
    }
}
