package ba.unsa.etf.zavrsnirad.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance = null;
    private Connection connection;

    private DatabaseConnection () {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:projectdatabase.db");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    // METHODS FOR DATABASE
    public static DatabaseConnection getInstance() {
        if(instance == null) instance = new DatabaseConnection ();
        return instance;
    }

    public static void removeInstance() {
        if (instance != null) {
            try {
                instance.connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        instance = null;
    }

    public Connection getConnection() {
        return connection;
    }
}
