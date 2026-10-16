package common.db;

import java.util.Properties;

public class ConnectionFactory {
    private static final String CONFIG_FILE="application.properties";
    private static final Properties PROPERTIES = loadProperties();

    private ConnectionFactory(){}

    public static Connection getConnection(){

    }
}
