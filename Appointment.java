import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class Appointment extends JFrame {

    private final AppointmentDAO dao = new AppointmentDAO();

    private DefaultTableModel tblModel;
    private JTable tblAppointments;

    public Appointment() {
        setTitle("Appointment Management");
        setSize(750, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Book Appointment", createBookPanel());
        tabs.addTab("View / Cancel Appointments", createViewPanel());

        add(tabs);
        setVisible(true);
    }

    // ==========================================================
    // TAB 1: BOOK APPOINTMENT
    // ==========================================================
    private JPanel createBookPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtPatientId = new JTextField(15);
        JTextField txtDoctorId = new JTextField(15);
        JTextField txtDate = new JTextField("yyyy-MM-dd", 15);
        JTextField txtTime = new JTextField("HH:mm:ss", 15);
        JTextField txtReason = new JTextField(15);
        JButton btnBook = new JButton("Book Appointment");
        JButton btnBack = new JButton("Back");

        int row = 0;
        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Patient ID:"), c);
        c.gridx = 1; panel.add(txtPatientId, c);

        row++;
        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Doctor ID:"), c);
        c.gridx = 1; panel.add(txtDoctorId, c);

        row++;
        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Date:"), c);
        c.gridx = 1; panel.add(txtDate, c);

        row++;
        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Time:"), c);
        c.gridx = 1; panel.add(txtTime, c);

        row++;
        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Reason:"), c);
        c.gridx = 1; panel.add(txtReason, c);

        row++;
        c.gridx = 1; c.gridy = row; panel.add(btnBook, c);

        row++;
        c.gridx = 1; c.gridy = row; panel.add(btnBack, c);

        btnBack.addActionListener(e -> {
            new Dashboard();
            dispose();
        });

        btnBook.addActionListener(e -> {
            try {
                int patientId = Integer.parseInt(txtPatientId.getText().trim());
                int doctorId = Integer.parseInt(txtDoctorId.getText().trim());
                String date = txtDate.getText().trim();
                String time = txtTime.getText().trim();
                String reason = txtReason.getText().trim();

                if (date.isEmpty() || time.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Date and time are required.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean success = dao.bookAppointment(patientId, doctorId, date, time, reason);

                if (success) {
                    JOptionPane.showMessageDialog(this, "Appointment booked successfully!");
                    txtPatientId.setText("");
                    txtDoctorId.setText("");
                    txtDate.setText("yyyy-MM-dd");
                    txtTime.setText("HH:mm:ss");
                    txtReason.setText("");
                    refreshTable();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Could not save appointment.\n\nDatabase said:\n"
                          + AppointmentDAO.lastError,
                            "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Patient ID and Doctor ID must be numbers.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    // ==========================================================
    // TAB 2: VIEW / CANCEL APPOINTMENTS
    // ==========================================================
    private JPanel createViewPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"Appt ID", "Patient ID", "Doctor ID", "Date", "Time", "Reason", "Status"};
        tblModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // read-only table, actions happen through buttons
            }
        };
        tblAppointments = new JTable(tblModel);

        JButton btnRefresh = new JButton("Refresh");
        JButton btnComplete = new JButton("Mark Completed");
        JButton btnCancel = new JButton("Cancel Appointment");
        JButton btnDelete = new JButton("Delete Record");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnComplete);
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnDelete);

        btnRefresh.addActionListener(e -> refreshTable());

        btnComplete.addActionListener(e -> updateSelectedStatus("Completed"));
        btnCancel.addActionListener(e -> updateSelectedStatus("Cancelled"));

        btnDelete.addActionListener(e -> {
            int selectedRow = tblAppointments.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Select a row first.");
                return;
            }
            int apptId = (int) tblModel.getValueAt(selectedRow, 0);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Permanently delete appointment #" + apptId + "?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                dao.deleteAppointment(apptId);
                refreshTable();
            }
        });

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(tblAppointments), BorderLayout.CENTER);

        refreshTable(); // load data as soon as the tab is built
        return panel;
    }

    private void updateSelectedStatus(String newStatus) {
        int selectedRow = tblAppointments.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a row first.");
            return;
        }
        int apptId = (int) tblModel.getValueAt(selectedRow, 0);
        dao.updateStatus(apptId, newStatus);
        refreshTable();
    }

    private void refreshTable() {
        tblModel.setRowCount(0); // clear existing rows
        List<AppointmentRecord> records = dao.getAllAppointments();

        for (AppointmentRecord r : records) {
            tblModel.addRow(new Object[]{
                    r.appointmentId, r.patientId, r.doctorId,
                    r.date, r.time, r.reason, r.status
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Appointment::new);
    }
}