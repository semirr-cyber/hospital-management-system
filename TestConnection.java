import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Standalone test - run this by itself (Shift+F6 with this file open).
 * SQL SERVER + SQL SERVER AUTHENTICATION VERSION.
 */
public class TestConnection {
    public static void main(String[] args) {

        // Use instanceName so the driver can locate the SQLEXPRESS named instance
        String url = "jdbc:sqlserver://localhost;instanceName=SQLEXPRESS;databaseName=HospitalDB;encrypt=true;trustServerCertificate=true";
        String user = "sa";
        String password = "Passw0rd!"; // set this to what you just created

        System.out.println("STEP 1: Loading SQL Server driver...");
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            System.out.println("  OK - driver found.");
        } catch (ClassNotFoundException e) {
            System.out.println("  FAILED - driver not found. The mssql-jdbc JAR is not on the classpath.");
            e.printStackTrace();
            return;
        }

        System.out.println("STEP 2: Connecting to " + url + " ...");
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("  OK - connected to the database.");
        } catch (Exception e) {
            System.out.println("  FAILED - could not connect. Full error below:");
            e.printStackTrace();
            return;
        }

        System.out.println("STEP 3: Running a test query (SELECT * FROM appointments)...");
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM appointments");
            ResultSet rs = ps.executeQuery();
            int count = 0;
            while (rs.next()) {
                count++;
                System.out.println("  Row " + count + ": id=" + rs.getInt("appointment_id")
                        + ", patient=" + rs.getInt("patient_id")
                        + ", doctor=" + rs.getInt("doctor_id")
                        + ", date=" + rs.getString("appointment_date")
                        + ", status=" + rs.getString("status"));
            }
            System.out.println("  OK - query ran. Total rows found: " + count);
        } catch (Exception e) {
            System.out.println("  FAILED - query did not run. Full error below:");
            e.printStackTrace();
            return;
        }

        System.out.println("STEP 4: Trying an INSERT (the same kind of query booking uses)...");
        try {
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO appointments (patient_id, doctor_id, appointment_date, appointment_time, reason, status) "
              + "VALUES (1, 1, '2026-08-01', '11:00:00', 'Test insert', 'Scheduled')");
            int rows = ps.executeUpdate();
            System.out.println("  OK - insert worked, rows affected: " + rows);
        } catch (Exception e) {
            System.out.println("  FAILED - insert did not run. Full error below:");
            e.printStackTrace();
        }

        System.out.println("DONE.");
    }
}