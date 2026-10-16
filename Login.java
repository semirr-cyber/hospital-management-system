import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login {

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setTitle("Hospital Login");
        frame.setSize(450, 450);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        ImageIcon logo = new ImageIcon("Logo.jpg");
        frame.setIconImage(logo.getImage());

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(40, 150, 90, 30);

        JButton resetButton = new JButton("Reset");
        resetButton.setBounds(150, 150, 90, 30);

        JTextField usernameField = new JTextField();
        usernameField.setBounds(140, 40, 140, 30);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(150, 100, 150, 30);

        JLabel usernameLabel = new JLabel("User Name");
        usernameLabel.setBounds(40, 40, 100, 30);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(40, 90, 100, 30);

        frame.add(usernameLabel);
        frame.add(passwordLabel);
        frame.add(loginButton);
        frame.add(resetButton);
        frame.add(usernameField);
        frame.add(passwordField);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = String.valueOf(passwordField.getPassword());
                String message = checkLogin(username, password);
    
                if (message.equals("Welcome")) {
                    JOptionPane.showMessageDialog(frame, "Login Successful!");
                    frame.dispose();
                    new Dashboard();
                } else {
                    JOptionPane.showMessageDialog(frame, message, "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                usernameField.setText("");
                passwordField.setText("");
            }
        });

        frame.setVisible(true);
    }

    private static String checkLogin(String username, String password) {
        String savedUsername = "HOS123";
        String savedPassword = "hospital";

        if (username.length() > 6) {
            return "User name must be less than 7 characters.";
        }
        if (password.length() > 8) {
            return "Password must be less than 9 characters.";
        }
        if (username.equals(savedUsername) && password.equals(savedPassword)) {
            return "Welcome";
        }
        return "Incorrect user name or password.";
    }
}
