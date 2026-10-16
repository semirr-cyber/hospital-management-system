import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple data model for one patient row.
 */
class PatientRecord {
    int patientId;
    String name;
    int age;
    String gender;
    String phone;
    String address;

    PatientRecord(int patientId, String name, int age, String gender, String phone, String address) {
        this.patientId = patientId;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.phone = phone;
        this.address = address;
    }
}

/**
 * DAO (Data Access Object) - all SQL for the patients table lives
 * here. The GUI never writes SQL directly, it just calls these methods.
 */
public class PatientDAO {

    // Holds the message from the last SQL error so the GUI can show it in a popup.
    public static String lastError = "";

    // ---- CREATE ----
    public boolean addPatient(String name, int age, String gender, String phone, String address) {
        String sql = "INSERT INTO patients (name, age, gender, phone, address) VALUES (?, ?, ?, ?, ?)";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            lastError = "No database connection - check DatabaseConnection.java credentials "
                      + "and that SQL Server is running (or JDBC driver is on the classpath).";
            return false;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setString(3, gender);
            ps.setString(4, phone);
            ps.setString(5, address);
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
    public List<PatientRecord> getAllPatients() {
        List<PatientRecord> list = new ArrayList<>();
        
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            lastError = "Database not connected. Please check SQL Server connection.";
            System.out.println(lastError);
            return list; // Return empty list instead of crashing
        }
        
        String sql = "SELECT * FROM patients ORDER BY patient_id";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new PatientRecord(
                        rs.getInt("patient_id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("gender"),
                        rs.getString("phone"),
                        rs.getString("address")
                ));
            }
        } catch (SQLException e) {
            lastError = e.getMessage();
            e.printStackTrace();
        }
        return list;
    }

    // ---- READ (search by name, partial match) ----
    public List<PatientRecord> searchByName(String namePart) {
        List<PatientRecord> list = new ArrayList<>();
        
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            lastError = "Database not connected. Please check SQL Server connection.";
            return list;
        }
        
        String sql = "SELECT * FROM patients WHERE name LIKE ? ORDER BY patient_id";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + namePart + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new PatientRecord(
                            rs.getInt("patient_id"),
                            rs.getString("name"),
                            rs.getInt("age"),
                            rs.getString("gender"),
                            rs.getString("phone"),
                            rs.getString("address")
                    ));
                }
            }
        } catch (SQLException e) {
            lastError = e.getMessage();
            e.printStackTrace();
        }
        return list;
    }

    // ---- UPDATE ----
    public boolean updatePatient(int patientId, String name, int age, String gender,
                                  String phone, String address) {
        String sql = "UPDATE patients SET name=?, age=?, gender=?, phone=?, address=? WHERE patient_id=?";
        
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            lastError = "Database not connected. Please check SQL Server connection.";
            return false;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setString(3, gender);
            ps.setString(4, phone);
            ps.setString(5, address);
            ps.setInt(6, patientId);
            int rows = ps.executeUpdate();
            lastError = "";
            return rows > 0;
        } catch (SQLException e) {
            lastError = e.getMessage();
            e.printStackTrace();
            return false;
        }
    }

    // ---- DELETE ----
    public boolean deletePatient(int patientId) {
        String sql = "DELETE FROM patients WHERE patient_id = ?";
        
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            lastError = "Database not connected. Please check SQL Server connection.";
            return false;
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            int rows = ps.executeUpdate();
            lastError = "";
            return rows > 0;
        } catch (SQLException e) {
            lastError = e.getMessage();
            e.printStackTrace();
            // This will fail if the patient still has appointments (foreign key constraint) -
            // that's intentional, it protects your data from orphaned appointment rows.
            return false;
        }
    }
}