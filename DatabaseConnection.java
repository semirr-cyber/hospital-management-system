import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton JDBC connection manager - SQL SERVER, SQL SERVER AUTHENTICATION.
 * Every DAO class asks THIS class for the connection instead of
 * opening its own - keeps one connection shared across the app.
 */
public class DatabaseConnection {

    // ==== EDIT THESE TO MATCH YOUR SQL SERVER SETUP ====
    // Use instanceName rather than the backslash form; this works with SQL Browser
    private static final String URL =
        "jdbc:sqlserver://localhost;instanceName=SQLEXPRESS;databaseName=HospitalDB;integratedSecurity=false;encrypt=true;trustServerCertificate=true";
    private static final String USER = "sa";
    private static final String PASSWORD = "Passw0rd!"; // set this to what you just created
    // =============================================================

    // Expose last error for the GUI to show (helpful for diagnosing connection problems)
    public static String lastError = "";

    private static Connection connection;

    private DatabaseConnection() {}

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Database connected successfully.");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("SQL Server JDBC Driver not found. Did you add mssql-jdbc jar to Libraries?");
            System.out.println("Make sure mssql-jdbc JAR is in lib/ folder and project.properties is configured.");
            lastError = "SQL Server JDBC Driver not found: " + e.getMessage();
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
            lastError = e.getMessage();
            System.out.println("\nTroubleshooting:");
            System.out.println("1. Make sure SQL Server is RUNNING: Get-Service MSSQL$SQLEXPRESS (should show 'Running')");
            System.out.println("2. Make sure TCP/IP is enabled in SQL Server Configuration Manager");
            System.out.println("3. Make sure database 'HospitalDB' exists");
            System.out.println("4. Check credentials: USER='" + USER + "' PASSWORD='****'");
            System.out.println("5. Check connection string: " + URL);
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
