package t12311m0.shoes_store;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {
    public static Connection connectDB() {
        String url = "jdbc:mysql://localhost:3306/ShoesStore?zeroDateTimeBehavior=CONVERT_TO_NULL";
        String user = "root";
        String password = ""; // If your MySQL has no password

        Connection connect = null;
        try {
            // Establish the connection
            connect = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the database successfully!");
        } catch (SQLException e) {
            System.out.println("Connection failed!");
            e.printStackTrace();
        }
        return connect;
    }
}
