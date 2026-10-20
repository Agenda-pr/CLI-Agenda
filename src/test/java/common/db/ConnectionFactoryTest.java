package common.db;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class ConnectionFactoryTest {
    @Test
    void getConnectionOpensWorkingConnection() throws Exception {
        Connection connection = ConnectionFactory.getConnection();

        try (Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery("SELECT 1")) {

            assertNotNull(connection);
            assertTrue(result.next());
            assertEquals(1, result.getInt(1));
        }
    }

    @Test
    void getConnectionReturnsTheSameConnection() {
        assertSame(ConnectionFactory.getConnection(), ConnectionFactory.getConnection());
    }

    @Test
    void getConnectionOpensNewConnectionAfterClosing() throws Exception {
        Connection first = ConnectionFactory.getConnection();
        ConnectionFactory.closeConnection();

        Connection second = ConnectionFactory.getConnection();

        assertTrue(first.isClosed());
        assertTrue(second.isValid(2));
    }

}
