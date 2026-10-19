package common.db;

import common.exception.DatabaseException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class ConnectionFactory {
    private static final String CONFIG_FILE = "application.properties";
    private static final String CONFIG_TEMPLATE = CONFIG_FILE + ".example";
    private static final Properties PROPERTIES = loadProperties();

    private static Connection sharedConnection;

    private ConnectionFactory() {}

    public static synchronized Connection getConnection() {
        try {
            if (sharedConnection == null
                    || sharedConnection.isClosed()
                    || !sharedConnection.isValid(2)) {
                sharedConnection = openConnection();
            }
            return sharedConnection;
        } catch (SQLException error) {
            throw new DatabaseException("Could not check the database connection", error);
        }
    }

    public static synchronized void closeConnection() {
        if (sharedConnection != null) {
            try {
                sharedConnection.close();
            } catch (SQLException ignored) {
                // nothing useful to do while shutting down
            } finally {
                sharedConnection = null;
            }
        }
    }

    private static Connection openConnection() {
        String host = required("db.host");
        String port = required("db.port");
        String name = required("db.name");
        String user = required("db.user");
        String password = required("db.password");

        String url = "jdbc:mysql://" + host + ":" + port + "/" + name;
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException error) {
            throw new DatabaseException("Could not connect to the database at " + url, error);
        }
    }

    private static String required(String key) {
        String value = PROPERTIES.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new DatabaseException("Missing configuration: " + key);
        }
        return value.trim();
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = ConnectionFactory.class
                .getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new DatabaseException(CONFIG_FILE + " not found. Copy "
                        + CONFIG_TEMPLATE + " to " + CONFIG_FILE);
            }
            properties.load(input);
        } catch (IOException error) {
            throw new DatabaseException("Could not read " + CONFIG_FILE, error);
        }
        return properties;
    }
}
