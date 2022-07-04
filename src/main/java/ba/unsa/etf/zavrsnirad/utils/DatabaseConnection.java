package ba.unsa.etf.zavrsnirad.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class DatabaseConnection {
    private static DatabaseConnection instance = null;
    private Connection connection;

    private DatabaseConnection () throws SQLException {
        if(ReportData.getDatabaseConnectionString() == null)
            connection = DriverManager.getConnection("jdbc:sqlite:projectdatabase.db");
        else
            connection = DriverManager.getConnection("jdbc:sqlite:" + ReportData.getDatabaseConnectionString());
    }

    // METHODS FOR DATABASE
    public static DatabaseConnection getInstance() throws SQLException {
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
