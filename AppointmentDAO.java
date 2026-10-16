import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple data model for one appointment row.
 */
class AppointmentRecord {
    int appointmentId;
    int patientId;
    int doctorId;
    String date;      // yyyy-MM-dd
    String time;       // HH:mm:ss
    String reason;
    String status;

    AppointmentRecord(int appointmentId, int patientId, int doctorId,
                       String date, String time, String reason, String status) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.date = date;
        this.time = time;
        this.reason = reason;
        this.status = status;
    }
}

/**
 * DAO (Data Access Object) - all SQL for the appointments table
 * lives here. The GUI never writes SQL directly, it just calls
 * these methods.
 */
public class AppointmentDAO {

    // Holds the message from the last SQL error so the GUI can show it
    // in a popup instead of it only appearing in the console.
    public static String lastError = "";

    // ---- CREATE ----
    public boolean bookAppointment(int patientId, int doctorId, String date,
                                    String time, String reason) {
        String sql = "INSERT INTO appointments (patient_id, doctor_id, appointment_date, "
                   + "appointment_time, reason, status) VALUES (?, ?, ?, ?, ?, 'Scheduled')";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            lastError = "No database connection - check DatabaseConnection.java credentials "
                      + "and that MySQL is running.";
            return false;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ps.setInt(2, doctorId);
            ps.setString(3, date);
            ps.setString(4, time);
            ps.setString(5, reason);
            int rows = ps.executeUpdate();
            lastError = "";
            return rows > 0;
        } catch (SQLException e) {
            lastError = e.getMessage();
            e.printStackTrace();
            return false;
        }
    }

    // ---- READ (all) ----
    public List<AppointmentRecord> getAllAppointments() {
        List<AppointmentRecord> list = new ArrayList<>();
        
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            System.out.println("Database not connected. Please check SQL Server connection.");
            return list;
        }
        
        String sql = "SELECT * FROM appointments ORDER BY appointment_date, appointment_time";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new AppointmentRecord(
                        rs.getInt("appointment_id"),
                        rs.getInt("patient_id"),
                        rs.getInt("doctor_id"),
                        rs.getString("appointment_date"),
                        rs.getString("appointment_time"),
                        rs.getString("reason"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ---- READ (search by patient id) ----
    public List<AppointmentRecord> searchByPatientId(int patientId) {
        List<AppointmentRecord> list = new ArrayList<>();
        
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            System.out.println("Database not connected. Please check SQL Server connection.");
            return list;
        }
        
        String sql = "SELECT * FROM appointments WHERE patient_id = ? ORDER BY appointment_date";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new AppointmentRecord(
                            rs.getInt("appointment_id"),
                            rs.getInt("patient_id"),
                            rs.getInt("doctor_id"),
                            rs.getString("appointment_date"),
                            rs.getString("appointment_time"),
                            rs.getString("reason"),
                            rs.getString("status")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ---- UPDATE status (e.g. mark Completed / Cancelled) ----
    public boolean updateStatus(int appointmentId, String newStatus) {
        String sql = "UPDATE appointments SET status = ? WHERE appointment_id = ?";
        
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            lastError = "Database not connected. Please check SQL Server connection.";
            return false;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, appointmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            lastError = e.getMessage();
            e.printStackTrace();
            return false;
        }
    }

    // ---- DELETE ----
    public boolean deleteAppointment(int appointmentId) {
        String sql = "DELETE FROM appointments WHERE appointment_id = ?";
        
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            lastError = "Database not connected. Please check SQL Server connection.";
            return false;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, appointmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            lastError = e.getMessage();
            e.printStackTrace();
            return false;
        }
    }
}