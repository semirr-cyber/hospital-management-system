import java.awt.*;
import javax.swing.*;

class Dashboard {

    JFrame frame;
    JPanel contentPanel;

    Dashboard() {

        frame = new JFrame("Hospital Management Dashboard");
        frame.setSize(900, 550);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        // Header
        JLabel title = new JLabel("HOSPITAL MANAGEMENT SYSTEM");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBounds(220, 20, 500, 30);

        // Left Menu Panel
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(null);
        menuPanel.setBounds(20, 70, 200, 400);
        menuPanel.setBorder(BorderFactory.createTitledBorder("Menu"));

        JButton doctorBtn = new JButton("Doctors");
        doctorBtn.setBounds(25, 40, 150, 40);

        JButton billBtn = new JButton("Billing");
        billBtn.setBounds(25, 90, 150, 40);

        JButton patientBtn = new JButton("Patients");
        patientBtn.setBounds(25, 140, 150, 40);

        JButton appointmentBtn = new JButton("Appointments");
        appointmentBtn.setBounds(25, 190, 150, 40);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBounds(25, 240, 150, 40);

        menuPanel.add(doctorBtn);
        menuPanel.add(billBtn);
        menuPanel.add(patientBtn);
        menuPanel.add(appointmentBtn);
        menuPanel.add(logoutBtn);

        // Content Area
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBounds(250, 70, 600, 400);
        contentPanel.setBorder(BorderFactory.createTitledBorder("Information"));

        JTextArea display = new JTextArea();
        display.setEditable(false);

        contentPanel.add(new JScrollPane(display), BorderLayout.CENTER);

        // button actions
        patientBtn.addActionListener(e -> {
            frame.dispose();
            new Patient();
        });

        doctorBtn.addActionListener(e -> {
            frame.dispose();
            new Doctor();
        });

        appointmentBtn.addActionListener(e -> {
            frame.dispose();
            new Appointment();
        });

        
        // open billing window
        billBtn.addActionListener(e -> {
            frame.dispose();
            new MedicalBillingSystemGUI().setVisible(true);
        });

        // logout and go back to login screen
        logoutBtn.addActionListener(e -> {
            frame.dispose();
            new Login();
        });

        frame.add(title);
        frame.add(menuPanel);
        frame.add(contentPanel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Dashboard());
    }
}