package common.db;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class ConnectionFactoryTest {
    @Test
    void getConnectionOpensWorkingConnection() throws Exception {
        try (Connection connection = ConnectionFactory.getConnection();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery("SELECT 1")) {

            assertNotNull(connection);
            assertTrue(result.next());
            assertEquals(1, result.getInt(1));
        }
    }

}
