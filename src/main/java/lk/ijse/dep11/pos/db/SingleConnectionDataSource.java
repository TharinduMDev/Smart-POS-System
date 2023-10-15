package lk.ijse.dep11.pos.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class SingleConnectionDataSource {
    private  static SingleConnectionDataSource instance;
    private final Connection connection = null;

    private SingleConnectionDataSource() throws IOException {
        Properties properties = new Properties();
        properties.load(this.getClass().getResourceAsStream("/Application.properties"));
        String url = properties.getProperty("app.datasource.url");
        String username = properties.getProperty("app.datasource.username");
        String password = properties.getProperty("app.datasource.password");
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        
    }
    public static SingleConnectionDataSource getInstance() throws IOException {
        return instance==null ? instance=new SingleConnectionDataSource() : instance;
    }

    public Connection getConnection(){
        return connection;
    }
}
