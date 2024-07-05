package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionManager {

    private static final String DRIVER_KEY = "db.driver";
    private static final String URL_KEY= "db.url";
    private static final String USERNAME_KEY= "db.username";
    private static final String PASSWORD_KEY= "db.password";

    static {
        loadDriver();
    }

    private static void loadDriver() {
        try {
            Class.forName(PropertiesUtil.get(DRIVER_KEY));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private ConnectionManager() {
    }

    public static Connection get() {
       try{
           return DriverManager.getConnection(
                   PropertiesUtil.get(URL_KEY),
                   PropertiesUtil.get(USERNAME_KEY),
                   PropertiesUtil.get(PASSWORD_KEY)
           );
       } catch (SQLException e) {
           throw new RuntimeException(e);
       }
    }
}