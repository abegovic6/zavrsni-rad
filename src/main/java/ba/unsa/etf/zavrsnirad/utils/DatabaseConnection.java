package ba.unsa.etf.zavrsnirad.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class DatabaseConnection {
    private static DatabaseConnection instance = null;
    private Connection connection;
    private static String connectionString;

    private DatabaseConnection () {
        try {
            if(connectionString == null)
                connection = DriverManager.getConnection("jdbc:sqlite:projectdatab");
            else
                connection = DriverManager.getConnection(connectionString);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    // METHODS FOR DATABASE
    public static DatabaseConnection getInstance(String newConnectionString) {
        if(!Objects.equals(newConnectionString, connectionString)) {
            connectionString = newConnectionString;
            instance = new DatabaseConnection ();
        }
        else if(instance == null) instance = new DatabaseConnection ();
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
