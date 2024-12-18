import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

public class Client extends JFrame implements ActionListener {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private String thisUserName;
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();


    public Client() {
        // Connect to server
        final int port = 4242;
        final String host = "localhost";

        try {
            socket = new Socket(host, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Connection failed to server", "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        setTitle("Log in");
        setSize(screenSize.width, screenSize.height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        homeScreen();
    }

    private void homeScreen() {
        JPanel homeScreen = new JPanel(new GridLayout(3, 0));
        JLabel welcome = new JLabel("Welcome to ChronoDo!" + "\nWould you like to log in or " +
                "create an account?", SwingConstants.CENTER);
        welcome.setFont(new Font("Calibri", Font.BOLD, 39));
        welcome.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        homeScreen.add(welcome);

        JButton login = new JButton("Log in");
        homeScreen.add(login);
        login.addActionListener(e -> {
            getContentPane().remove(homeScreen);
            homeScreen.setVisible(false);
            login();
        });

        JButton create = new JButton("Create account");
        homeScreen.add(create);
        create.addActionListener(e -> {
            getContentPane().remove(homeScreen);
            homeScreen.setVisible(false);
            createAccount();
        });

        homeScreen.setBackground(new Color(216, 219, 222));
        getContentPane().add(homeScreen);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void login() {
        writer.println("Login");

        // Create a JDialog
        JDialog dialog = new JDialog((Frame) null, "Login", true);
        dialog.setSize(300, 200);
        dialog.setLayout(new BorderLayout(10, 10));

        // Login Panel
        JPanel loginPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        loginPanel.add(new JLabel("Username:"));
        JTextField loginUsernameField = new JTextField();
        loginPanel.add(loginUsernameField);

        loginPanel.add(new JLabel("Password:"));
        JPasswordField loginPasswordField = new JPasswordField();
        loginPanel.add(loginPasswordField);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            handleLogin(loginUsernameField.getText(), new String(loginPasswordField.getPassword()));
            dialog.dispose();
        });
        buttonPanel.add(loginButton);

        // Add panels to the dialog
        dialog.add(loginPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void handleLogin(String username, String password) {
        try {
            writer.println("Login");
            writer.println(username);
            writer.println(password);

            boolean condition = Boolean.parseBoolean(reader.readLine());
            if (condition) {
                JOptionPane.showMessageDialog(this, "Welcome, " + username, "Logged In", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Incorrect username or password", "Error", JOptionPane.ERROR_MESSAGE);
                homeScreen();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error during login", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createAccount() {
        writer.println("Create Account");

        // Create a JDialog
        JDialog dialog = new JDialog((Frame) null, "Create Account", true);
        dialog.setSize(300, 200);
        dialog.setLayout(new BorderLayout(10, 10));

        // Create Account Panel
        JPanel createAccountPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        createAccountPanel.add(new JLabel("Enter a username:"));
        JTextField createAccountUsernameField = new JTextField();
        createAccountPanel.add(createAccountUsernameField);

        createAccountPanel.add(new JLabel("Enter a password:"));
        JPasswordField createAccountPasswordField = new JPasswordField();
        createAccountPanel.add(createAccountPasswordField);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        JButton createAccountButton = new JButton("Create Account");
        createAccountButton.addActionListener(e -> {
            handleCreateAccount(createAccountUsernameField.getText(), new String(createAccountPasswordField.getPassword()));
            dialog.dispose();
        });
        buttonPanel.add(createAccountButton);

        // Add panels to the dialog
        dialog.add(createAccountPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void handleCreateAccount(String username, String password) {
        try {
            writer.println("Create Account");
            writer.println(username);
            writer.println(password);

            String condition = reader.readLine();
            if (condition.equals("User created!")) {
                JOptionPane.showMessageDialog(this, "Welcome, " + username,
                        condition, JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Try again with a different username",
                        condition, JOptionPane.ERROR_MESSAGE);
                homeScreen();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error during account creation", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Client client = new Client();
                client.setVisible(true);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}