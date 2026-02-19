package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseManager implements AutoCloseable {

    private static final String JDBC_URL = "jdbc:h2:mem:libraryDB;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    private Connection connection;

    public DatabaseManager() throws SQLException {
        var props = new Properties();
        props.setProperty("user", USER);
        props.setProperty("password", PASSWORD);
        props.setProperty("ssl", "false");

        this.connection = DriverManager.getConnection(JDBC_URL, props);
    }

    public Connection getConnection() {
        return connection;
    }

    public void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Drop existing tables
            stmt.execute("DROP TABLE IF EXISTS borrow_records");
            stmt.execute("DROP TABLE IF EXISTS members");
            stmt.execute("DROP TABLE IF EXISTS items");

            // Create tables
            stmt.execute("""
                        CREATE TABLE items (
                            id VARCHAR(50) PRIMARY KEY,
                            title VARCHAR(200) NOT NULL,
                            type VARCHAR(50) NOT NULL,
                            available BOOLEAN DEFAULT TRUE,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                        )
                    """);

            stmt.execute("""
                        CREATE TABLE members (
                            id INT PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            email VARCHAR(100) UNIQUE NOT NULL,
                            phone VARCHAR(20),
                            status VARCHAR(20) DEFAULT 'ACTIVE',
                            membership_date DATE DEFAULT CURRENT_DATE
                        )
                    """);

            stmt.execute("""
                        CREATE TABLE borrow_records (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            item_id VARCHAR(50) NOT NULL,
                            member_id INT NOT NULL,
                            borrow_date DATE NOT NULL,
                            due_date DATE NOT NULL,
                            return_date DATE,
                            FOREIGN KEY (item_id) REFERENCES items(id),
                            FOREIGN KEY (member_id) REFERENCES members(id)
                        )
                    """);
        }
    }

    @Override
    public void close() throws Exception {
        if (connection != null && !connection.isClosed())
            connection.close();
    }
}
