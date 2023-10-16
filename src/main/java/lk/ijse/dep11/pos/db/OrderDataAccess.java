package lk.ijse.dep11.pos.db;

import lk.ijse.dep11.pos.tm.Customer;
import lk.ijse.dep11.pos.tm.Order;
import lk.ijse.dep11.pos.tm.OrderItem;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDataAccess {
    static final PreparedStatement STM_ORDER_EXIST_BY_CUSTOMER_ID;
    static final PreparedStatement STM_ORDER_EXIST_BY_ITEM_ID;
    static final PreparedStatement STM_GET_LAST_ORDER_ID;
    static final PreparedStatement STM_INSERT_ORDER;
    static final PreparedStatement  STM_INSERT_ORDER_ITEM;
    static final PreparedStatement  STM_UPDATE_STOCK;
    static final PreparedStatement  STM_FIND;


    static {
        Connection connection = SingleConnectionDataSource.getInstance().getConnection();
        try {
            STM_ORDER_EXIST_BY_CUSTOMER_ID =  connection.prepareStatement("SELECT * FROM \"order\" WHERE customer_id=?");
            STM_ORDER_EXIST_BY_ITEM_ID = connection.prepareStatement("SELECT * FROM order_item WHERE item_code=?");
            STM_GET_LAST_ORDER_ID = connection.prepareStatement("SELECT id FROM \"order\" ORDER BY id DESC FETCH FIRST ROWS ONLY ");
            STM_INSERT_ORDER = connection.prepareStatement("INSERT INTO \"order\" (id, date, customer_id) VALUES (?,?,?)");
            STM_INSERT_ORDER_ITEM = connection.prepareStatement("INSERT INTO order_item (order_id, item_code, qty, unit_price) VALUES (?,?,?,?)");
            STM_UPDATE_STOCK = connection.prepareStatement("UPDATE item SET qty=? WHERE code=?");
            STM_FIND = connection.prepareStatement("SELECT o.*, c.name, CAST(order_total.total AS DECIMAL(8,2))\n" +
                    "FROM \"order\" AS o\n" +
                    "         INNER JOIN customer AS c ON o.customer_id = c.id\n" +
                    "        INNER JOIN\n" +
                    "(SELECT o.id, SUM(qty * unit_price) AS total\n" +
                    "FROM \"order\" AS o\n" +
                    "         INNER JOIN order_item AS oi ON oi.order_id = o.id GROUP BY o.id) AS order_total\n" +
                    "ON o.id = order_total.id\n" +
                    "WHERE o.id LIKE ? OR CAST(o.date AS VARCHAR(20)) LIKE ? OR o.customer_id LIKE ? OR c.name LIKE ? " +
                    "ORDER BY o.id");
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

    public static List<Order> findOrders(String query) throws SQLException {
        for(int i = 1; i <= 4; i++)
            STM_FIND.setString(i, "%".concat(query).concat("%"));
        ResultSet rst = STM_FIND.executeQuery();
        List<Order> orderList = new ArrayList<>();
        while (rst.next()){
            String orderId = rst.getString("id");
            Date orderDate = rst.getDate("date");
            String customerId = rst.getString("customer_id");
            String customerName = rst.getString("name");
            BigDecimal orderTotal = rst.getBigDecimal("total");
            orderList.add(new Order(orderId, orderDate.toString(), customerId, customerName, orderTotal));
        }
        return orderList;
    }
}
