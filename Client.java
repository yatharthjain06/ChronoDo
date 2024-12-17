import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends JFrame implements ActionListener {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private String thisUserName;


    JLabel welcomeText = new JLabel();

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
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize.width, screenSize.height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().add(welcomeText);
        homeScreen();
    }

    private void homeScreen() {
        JPanel homePanel = new JPanel(new GridLayout(3, 1,0,30));
        JPanel emptyPanel = new JPanel();
        emptyPanel.setBackground(new Color(100, 100, 100));
        emptyPanel.setPreferredSize(new Dimension(0, 500));
        homePanel.add(emptyPanel);
        homePanel.setBackground(new Color(140, 211, 255));

        // Add welcome message
        JLabel welcomeLabel = new JLabel("ChronoDo", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Calibri", Font.BOLD, 39));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        homePanel.add(welcomeLabel);

        JLabel loginOrCreate = new JLabel("Login or create account", SwingConstants.RIGHT);
        loginOrCreate.setFont(new Font("Calibri", Font.BOLD, 39));
        loginOrCreate.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        homePanel.add(loginOrCreate);


        // Add a proceed button
        JButton proceedButton = new JButton("Proceed to Login");
        proceedButton.setFont(new Font("Calibri", Font.PLAIN, 16));
        proceedButton.addActionListener(e -> {
            getContentPane().remove(homePanel);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(proceedButton);
        buttonPanel.setBackground(new Color(140, 211, 255));
        homePanel.add(buttonPanel);

        // Display the home screen
        getContentPane().add(homePanel);
        setLocationRelativeTo(null);
        setVisible(true);
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