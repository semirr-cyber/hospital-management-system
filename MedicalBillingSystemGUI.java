import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MedicalBillingSystemGUI extends JFrame {

    // --- Original Core Data Structures ---
    static String[] serviceIds = {
        "SRV001", "SRV002", "SRV003", "SRV004", "SRV005", "SRV006", "SRV007",
        "SRV008", "SRV009", "SRV010", "SRV011", "SRV012", "SRV013", "SRV014"
    };

    static String[] serviceNames = {
        "blood test", "urine test", "X-rays", "CT-Scan", "MRI", "Ultrasounds",
        "Cardiology", "Operating room", "Recovery room", "ER", "ICU",
        "Inpatient medication", "Anesthesia medication", "Take home meds"
    };

    static double[] servicePrices = {
        40.00, 35.00, 120.00, 450.00, 850.00, 200.00, 300.00,
        2500.00, 1500.00, 3000.00, 1000.00, 400.00, 800.00, 500.00
    };

    static int nextBillNumber = 1;
    static String stayServiceId = "STAY001";
    static double dailyStayRate = 500.0;
    static int billCount = 0;

    static String[] billingIds = new String[100];
    static String[] patientIds = new String[100];
    static String[] doctorIds = new String[100];
    static int[] daysStayedList = new int[100];
    static double[] totalPayments = new double[100];
    static String[] billItemsList = new String[100];

    // --- GUI Components ---
    private JTextArea allBillsTextArea;
    private JTextArea searchResultTextArea;
    private JTextArea serviceListTextArea;

    public MedicalBillingSystemGUI() {
        // Setup Window Frame
        setTitle("Hospital Billing System");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Tabs Container
        JTabbedPane tabbedPane = new JTabbedPane();

        // Add Tab Panels
        tabbedPane.addTab("Generate New Bill", createGenerateBillPanel());
        tabbedPane.addTab("Find a Bill", createFindBillPanel());
        tabbedPane.addTab("Show All Saved Bills", createShowAllBillsPanel());
        tabbedPane.addTab("Service List & Prices", createServiceListPanel());

        add(tabbedPane);

        // Pre-fill service list text area
        updateServiceListDisplay();
    }

    // ==========================================
    // TAB 1: GENERATE NEW BILL PANEL
    // ==========================================
    private JPanel createGenerateBillPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Patient Metadata Panel (Top)
        JPanel metaPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        metaPanel.setBorder(BorderFactory.createTitledBorder("Patient Details"));
        
        JTextField patientIdField = new JTextField();
        JTextField doctorIdField = new JTextField();
        JTextField daysStayedField = new JTextField("0");

        metaPanel.add(new JLabel("Patient ID:"));
        metaPanel.add(patientIdField);
        metaPanel.add(new JLabel("Doctor ID (Optional):"));
        metaPanel.add(doctorIdField);
        metaPanel.add(new JLabel("Number of Days Stayed:"));
        metaPanel.add(daysStayedField);

        // Service Quantities Panel (Middle)
        JPanel servicesContainer = new JPanel(new BorderLayout());
        servicesContainer.setBorder(BorderFactory.createTitledBorder("Select Quantities for Additional Services"));
        
        JPanel servicesGrid = new JPanel(new GridLayout(serviceNames.length, 2, 5, 2));
        JTextField[] quantityFields = new JTextField[serviceNames.length];

        for (int i = 0; i < serviceNames.length; i++) {
            servicesGrid.add(new JLabel("[" + serviceIds[i] + "] " + serviceNames[i] + " ($" + servicePrices[i] + "):"));
            quantityFields[i] = new JTextField("0");
            servicesGrid.add(quantityFields[i]);
        }
        
        JScrollPane scrollPane = new JScrollPane(servicesGrid);
        servicesContainer.add(scrollPane, BorderLayout.CENTER);

        // Submit Button (Bottom)
        JButton submitButton = new JButton("Calculate and Save Bill");
        JButton btnBack = new JButton("Back");
        submitButton.setPreferredSize(new Dimension(0, 40));
        btnBack.setPreferredSize(new Dimension(0, 40));
        
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        bottomPanel.add(submitButton);
        bottomPanel.add(btnBack);
        
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String patientId = patientIdField.getText().trim();
                    String doctorId = doctorIdField.getText().trim();
                    int daysStayed = Integer.parseInt(daysStayedField.getText().trim());

                    if (patientId.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Patient ID cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (daysStayed < 0) {
                        JOptionPane.showMessageDialog(null, "Days stayed cannot be negative.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Calculate Bill
                    String billingId = createBillingId();
                    double stayCost = daysStayed * dailyStayRate;
                    double servicesCost = 0.0;
                    String billItems = stayServiceId + ":" + daysStayed;

                    for (int i = 0; i < serviceNames.length; i++) {
                        int quantity = Integer.parseInt(quantityFields[i].getText().trim());
                        if (quantity < 0) quantity = 0;

                        if (quantity > 0) {
                            double itemTotal = quantity * servicePrices[i];
                            servicesCost += itemTotal;
                            billItems = billItems + "," + serviceIds[i] + ":" + quantity;
                        }
                    }

                    double totalPayment = stayCost + servicesCost;

                    // Save Core Data
                    saveBill(billingId, patientId, doctorId, daysStayed, totalPayment, billItems);

                    // Clear Fields upon successful generation
                    patientIdField.setText("");
                    doctorIdField.setText("");
                    daysStayedField.setText("0");
                    for (JTextField field : quantityFields) {
                        field.setText("0");
                    }

                    // Refresh global text logs
                    updateAllBillsDisplay();

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Please enter valid integers for days and quantities.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnBack.addActionListener(e -> {
            new Dashboard();
            dispose();
        });

        mainPanel.add(metaPanel, BorderLayout.NORTH);
        mainPanel.add(servicesContainer, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    // ==========================================
    // TAB 2: FIND A BILL PANEL
    // ==========================================
    private JPanel createFindBillPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel searchControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> searchTypeBox = new JComboBox<>(new String[]{"Billing ID", "Patient ID"});
        JTextField searchField = new JTextField(15);
        JButton searchButton = new JButton("Search");

        searchControlPanel.add(new JLabel("Search By:"));
        searchControlPanel.add(searchTypeBox);
        searchControlPanel.add(new JLabel("Enter ID:"));
        searchControlPanel.add(searchField);
        searchControlPanel.add(searchButton);

        searchResultTextArea = new JTextArea();
        searchResultTextArea.setEditable(false);
        searchResultTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String query = searchField.getText().trim();
                if (query.isEmpty()) {
                    searchResultTextArea.setText("Please enter a search value.");
                    return;
                }

                StringBuilder resultSB = new StringBuilder();
                boolean found = false;
                int type = searchTypeBox.getSelectedIndex(); // 0 = Billing ID, 1 = Patient ID

                for (int i = 0; i < billCount; i++) {
                    boolean match = (type == 0) ? billingIds[i].equalsIgnoreCase(query) : patientIds[i].equalsIgnoreCase(query);
                    if (match) {
                        resultSB.append(formatBillString(i));
                        found = true;
                        if (type == 0) break; // Billing IDs are unique, exit early
                    }
                }

                if (!found) {
                    searchResultTextArea.setText("No matching bill records found.");
                } else {
                    searchResultTextArea.setText(resultSB.toString());
                }
            }
        });

        panel.add(searchControlPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(searchResultTextArea), BorderLayout.CENTER);
        return panel;
    }

    // ==========================================
    // TAB 3: SHOW ALL SAVED BILLS PANEL
    // ==========================================
    private JPanel createShowAllBillsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        allBillsTextArea = new JTextArea("No Bills Saved Yet.");
        allBillsTextArea.setEditable(false);
        allBillsTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        panel.add(new JLabel("All Registered Invoices:"), BorderLayout.NORTH);
        panel.add(new JScrollPane(allBillsTextArea), BorderLayout.CENTER);
        return panel;
    }

    // ==========================================
    // TAB 4: SERVICE LIST PANEL
    // ==========================================
    private JPanel createServiceListPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        serviceListTextArea = new JTextArea();
        serviceListTextArea.setEditable(false);
        serviceListTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        panel.add(new JLabel("Current hospital inventory price database:"), BorderLayout.NORTH);
        panel.add(new JScrollPane(serviceListTextArea), BorderLayout.CENTER);
        return panel;
    }

    // ==========================================
    // HELPER METHODS (Original Logic Implementations)
    // ==========================================
    public static String createBillingId() {
        String id = "BILL";
        if (nextBillNumber < 10) {
            id = id + "00" + nextBillNumber;
        } else if (nextBillNumber < 100) {
            id = id + "0" + nextBillNumber;
        } else {
            id = id + nextBillNumber;
        }
        nextBillNumber++;
        return id;
    }

    public static void saveBill(String billingId, String patientId, String doctorId, int daysStayed, double total, String billItems) {
        if (billCount >= billingIds.length) {
            JOptionPane.showMessageDialog(null, "Storage Full.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        billingIds[billCount] = billingId;
        patientIds[billCount] = patientId;
        doctorIds[billCount] = doctorId;
        daysStayedList[billCount] = daysStayed;
        totalPayments[billCount] = total;
        billItemsList[billCount] = billItems;

        billCount++;
        JOptionPane.showMessageDialog(null, "Bill Saved Successfully! ID: " + billingId, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private String formatBillString(int index) {
        return "========== BILL RECORD ==========\n" +
               "Billing ID    : " + billingIds[index] + "\n" +
               "Patient ID    : " + patientIds[index] + "\n" +
               "Doctor ID     : " + doctorIds[index] + "\n" +
               "Days Stayed   : " + daysStayedList[index] + "\n" +
               "Total Payment : $" + String.format("%.2f", totalPayments[index]) + "\n" +
               "Items         : " + billItemsList[index] + "\n" +
               "=================================\n\n";
    }

    private void updateAllBillsDisplay() {
        if (billCount == 0) {
            allBillsTextArea.setText("No Bills Saved Yet.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < billCount; i++) {
            sb.append(formatBillString(i));
        }
        allBillsTextArea.setText(sb.toString());
    }

    private void updateServiceListDisplay() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Service List ---\n");
        sb.append("[").append(stayServiceId).append("] Daily Hospital Stay - $").append(dailyStayRate).append(" per day\n\n");

        for (int i = 0; i < serviceNames.length; i++) {
            sb.append("[").append(serviceIds[i]).append("] ")
              .append(String.format("%-25s", serviceNames[i]))
              .append(" - $").append(servicePrices[i]).append("\n");
        }
        serviceListTextArea.setText(sb.toString());
    }

    // ==========================================
    // MAIN METHOD
    // ==========================================
    public static void main(String[] args) {
        // Run Swing Application safely on Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MedicalBillingSystemGUI().setVisible(true);
            }
        });
    }
}

