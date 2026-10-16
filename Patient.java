import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class Patient extends JFrame {

    private final PatientDAO dao = new PatientDAO();

    private DefaultTableModel tblModel;
    private JTable tblPatients;

    // Fields declared here so both the button listener and the table
    // selection listener (for editing) can reach them.
    private JTextField txtId, txtName, txtAge, txtPhone, txtAddress;
    private JComboBox<String> cmbGender;

    public Patient() {
        setTitle("Patient Management");
        setSize(800, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Add / Edit Patient", createFormPanel());
        tabs.addTab("View / Search Patients", createViewPanel());

        add(tabs);
        setVisible(true);
    }

    // ==========================================================
    // TAB 1: ADD / EDIT PATIENT
    // ==========================================================
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        txtId = new JTextField(15);
        txtId.setEditable(false); // filled automatically when editing an existing patient
        txtName = new JTextField(15);
        txtAge = new JTextField(15);
        cmbGender = new JComboBox<>(new String[]{"M", "F"});
        txtPhone = new JTextField(15);
        txtAddress = new JTextField(15);

        JButton btnSave = new JButton("Save New Patient");
        JButton btnUpdate = new JButton("Update Selected Patient");
        JButton btnClear = new JButton("Clear Form");
        JButton btnBack = new JButton("Back");

        int row = 0;
        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Patient ID (auto):"), c);
        c.gridx = 1; panel.add(txtId, c);

        row++;
        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Name:"), c);
        c.gridx = 1; panel.add(txtName, c);

        row++;
        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Age:"), c);
        c.gridx = 1; panel.add(txtAge, c);

        row++;
        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Gender:"), c);
        c.gridx = 1; panel.add(cmbGender, c);

        row++;
        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Phone:"), c);
        c.gridx = 1; panel.add(txtPhone, c);

        row++;
        c.gridx = 0; c.gridy = row; panel.add(new JLabel("Address:"), c);
        c.gridx = 1; panel.add(txtAddress, c);

        row++;
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnRow.add(btnSave);
        btnRow.add(btnUpdate);
        btnRow.add(btnClear);
        btnRow.add(btnBack);
        c.gridx = 0; c.gridy = row; c.gridwidth = 2;
        panel.add(btnRow, c);

        btnSave.addActionListener(e -> {
            if (!validateForm()) return;

            boolean success = dao.addPatient(
                    txtName.getText().trim(),
                    Integer.parseInt(txtAge.getText().trim()),
                    (String) cmbGender.getSelectedItem(),
                    txtPhone.getText().trim(),
                    txtAddress.getText().trim());

            if (success) {
                JOptionPane.showMessageDialog(this, "Patient added successfully!");
                clearForm();
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Could not save patient.\n\nDatabase said:\n" + PatientDAO.lastError,
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnUpdate.addActionListener(e -> {
            if (txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Select a patient from the View tab first, then edit fields here.");
                return;
            }
            if (!validateForm()) return;

            boolean success = dao.updatePatient(
                    Integer.parseInt(txtId.getText().trim()),
                    txtName.getText().trim(),
                    Integer.parseInt(txtAge.getText().trim()),
                    (String) cmbGender.getSelectedItem(),
                    txtPhone.getText().trim(),
                    txtAddress.getText().trim());

            if (success) {
                JOptionPane.showMessageDialog(this, "Patient updated successfully!");
                clearForm();
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Could not update patient.\n\nDatabase said:\n" + PatientDAO.lastError,
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnClear.addActionListener(e -> clearForm());

        btnBack.addActionListener(e -> {
            new Dashboard();
            dispose();
        });

        return panel;
    }

    private boolean validateForm() {
        if (txtName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            Integer.parseInt(txtAge.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Age must be a number.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void clearForm() {
        txtId.setText("");
        txtName.setText("");
        txtAge.setText("");
        cmbGender.setSelectedIndex(0);
        txtPhone.setText("");
        txtAddress.setText("");
    }

    // ==========================================================
    // TAB 2: VIEW / SEARCH PATIENTS
    // ==========================================================
    private JPanel createViewPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"ID", "Name", "Age", "Gender", "Phone", "Address"};
        tblModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblPatients = new JTable(tblModel);

        JTextField txtSearch = new JTextField(15);
        JButton btnSearch = new JButton("Search by Name");
        JButton btnRefresh = new JButton("Show All");
        JButton btnLoad = new JButton("Load Selected into Form");
        JButton btnDelete = new JButton("Delete Selected");

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Name contains:"));
        topPanel.add(txtSearch);
        topPanel.add(btnSearch);
        topPanel.add(btnRefresh);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.add(btnLoad);
        bottomPanel.add(btnDelete);

        btnSearch.addActionListener(e -> {
            List<PatientRecord> results = dao.searchByName(txtSearch.getText().trim());
            fillTable(results);
        });

        btnRefresh.addActionListener(e -> refreshTable());

        btnLoad.addActionListener(e -> {
            int selectedRow = tblPatients.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Select a row first.");
                return;
            }
            txtId.setText(String.valueOf(tblModel.getValueAt(selectedRow, 0)));
            txtName.setText(String.valueOf(tblModel.getValueAt(selectedRow, 1)));
            txtAge.setText(String.valueOf(tblModel.getValueAt(selectedRow, 2)));
            cmbGender.setSelectedItem(String.valueOf(tblModel.getValueAt(selectedRow, 3)));
            txtPhone.setText(String.valueOf(tblModel.getValueAt(selectedRow, 4)));
            txtAddress.setText(String.valueOf(tblModel.getValueAt(selectedRow, 5)));
            JOptionPane.showMessageDialog(this,
                    "Loaded into the Add/Edit tab - switch tabs, edit fields, then click Update.");
        });

        btnDelete.addActionListener(e -> {
            int selectedRow = tblPatients.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Select a row first.");
                return;
            }
            int patientId = (int) tblModel.getValueAt(selectedRow, 0);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete patient #" + patientId + "? This will also delete their appointments.",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = dao.deletePatient(patientId);
                if (success) {
                    refreshTable();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Could not delete patient.\n\nDatabase said:\n" + PatientDAO.lastError,
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(tblPatients), BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        refreshTable();
        return panel;
    }

    private void refreshTable() {
        fillTable(dao.getAllPatients());
    }

    private void fillTable(List<PatientRecord> records) {
        tblModel.setRowCount(0);
        for (PatientRecord r : records) {
            tblModel.addRow(new Object[]{
                    r.patientId, r.name, r.age, r.gender, r.phone, r.address
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Patient::new);
    }
}