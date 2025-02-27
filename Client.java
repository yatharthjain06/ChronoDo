import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class Client extends JFrame implements ActionListener {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Timer pomodoro = new Timer();
    private boolean timerRunning = false;
    JLabel timerLabel = new JLabel("25:00");
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
            getContentPane().removeAll();
            this.setVisible(false);
            login();
        });

        JButton create = new JButton("Create account");
        homeScreen.add(create);
        create.addActionListener(e -> {
            getContentPane().removeAll();
            this.setVisible(false);
            createAccount();
        });

        homeScreen.setBackground(new Color(216, 219, 222));
        getContentPane().add(homeScreen);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void login() {
//        writer.println("Login");

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
                mainMenu(username);
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
            writer.println(username);
            writer.println(password);

            String condition = reader.readLine();
            if (condition.equals("User created!")) {
                JOptionPane.showMessageDialog(this, "Welcome, " + username,
                        condition, JOptionPane.INFORMATION_MESSAGE);
                mainMenu(username);
            } else {
                JOptionPane.showMessageDialog(this, "Try again with a different username",
                        condition, JOptionPane.ERROR_MESSAGE);
                homeScreen();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error during account creation", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mainMenu(String username) {
        JTabbedPane tabbedPane = new JTabbedPane();

        // Tasks Panel
        JPanel tasks = new JPanel(new BorderLayout());
        JTextArea taskList = viewTasks(username);
        taskList.setLineWrap(true); // Optional: Enables line wrapping for readability
        taskList.setWrapStyleWord(true);

        // ScrollPane for the task list
        JScrollPane scrollPane = new JScrollPane(taskList); // Scrollable feed
        tasks.add(scrollPane, BorderLayout.CENTER); // Add scrollPane to tasks panel

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Buttons panel
        JButton addTask = new JButton("Add Task");
        addTask.addActionListener(e -> {
            addTask(username);
        });
        JButton removeTask = new JButton("Remove Task");
        removeTask.addActionListener(e -> {
            removeTask(username);
        });
        buttonPanel.add(addTask);
        buttonPanel.add(removeTask);
        tasks.add(buttonPanel, BorderLayout.SOUTH); // Add buttons at the bottom

        // Add panels to the tabbedPane
        tabbedPane.addTab("To-Do", tasks);


        JPanel pomodoro = new JPanel();
        tabbedPane.addTab("Pomodoro", pomodoro);
        pomodoro.setLayout(new BoxLayout(pomodoro, BoxLayout.Y_AXIS));

        // Image
        JLabel label = new JLabel();
        ImageIcon icon = new ImageIcon("Pomodoro-Timer.jpg");
        Image scaledImage = icon.getImage().getScaledInstance(500, 500, Image.SCALE_SMOOTH);
        label.setIcon(new ImageIcon(scaledImage));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setAlignmentY(Component.CENTER_ALIGNMENT);
        pomodoro.add(label);

        // Timer
        timerLabel.setFont(new Font("Arial", Font.BOLD, 100));
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        pomodoro.add(timerLabel);

        // Button
        JButton startButton = new JButton("Start Work Session");
        JButton breakButton = new JButton("Start Break Session");
        JButton pauseButton = new JButton("Pause");
        startButton.addActionListener(e -> {
            startTimer(25 * 60 * 1000);
            pauseButton.setText("Pause");
        });
        breakButton.addActionListener(e -> {
            startTimer(5 * 60 * 1000);
            pauseButton.setText("Pause");
        });
        pauseButton.addActionListener(e -> {
            if (pauseButton.getText().equals("Pause")) {
                pauseTimer();
                pauseButton.setText("Unpause");
            } else if (pauseButton.getText().equals("Unpause")) {
                unpauseTimer();
                pauseButton.setText("Pause");
            }

        });
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setSize(20, 8);
        breakButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        breakButton.setSize(20, 8);
        pauseButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        pauseButton.setSize(20, 8);
        pomodoro.add(startButton);
        pomodoro.add(breakButton);
        pomodoro.add(pauseButton);


        Container content = this.getContentPane();
        content.setLayout(new BorderLayout());
        content.add(tabbedPane, BorderLayout.CENTER);

        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }

    public void pauseTimer() {
        if (timerRunning) {
            timerRunning = false;
            pomodoro.cancel();
        }
    }

    public void unpauseTimer() {
        if (!timerRunning) {
            String[] time = timerLabel.getText().split(":");
            long minutes = Long.parseLong(time[0]) * 60 * 1000;
            long seconds = Long.parseLong(time[1]) * 1000;
            startTimer(minutes + seconds);
            timerRunning = true;
        }
    }

    public void startTimer(long duration) {
        if (timerRunning) {
            return;
        }
        timerRunning = true;
        AtomicLong timeRemaining = new AtomicLong(duration);
        pomodoro = new Timer();
        TimerTask start = new TimerTask() {
            @Override
            public void run() {
                if (timeRemaining.get() > 0) {
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(timeRemaining.get());
                    long seconds = (TimeUnit.MILLISECONDS.toSeconds(timeRemaining.get()) % 60);
                    SwingUtilities.invokeLater(() -> {
                        if (seconds < 10) {
                            timerLabel.setText(minutes + ":0" + seconds);
                        } else {
                            timerLabel.setText(minutes + ":" + seconds);
                        }
                    });

                    timeRemaining.addAndGet(-1000);
                } else {
                    pomodoro.cancel();
                    timerRunning = false;
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                            null, "Time's up!", "Timer Complete", JOptionPane.INFORMATION_MESSAGE
                    ));
                }
            }
        };
        pomodoro.scheduleAtFixedRate(start, 0, 1000);
    }

    private void addTask(String username) {
        writer.println("Add Task");
        writer.println(username);
        String task = JOptionPane.showInputDialog(null, "Enter task");
        writer.println(task);
        this.getContentPane().removeAll();
        mainMenu(username);
    }

    private void removeTask(String username) {
        try {
            writer.println("Remove Task");
            writer.println(username);


            int size = Integer.parseInt(reader.readLine());
            if (size == 0) {
                JOptionPane.showMessageDialog(this, "No tasks to remove",
                        "Remove Task", JOptionPane.ERROR_MESSAGE);
            } else {
                int[] options = new int[size];
                for (int i = 0; i < options.length; i++) {
                    options[i] = i + 1;
                }
                Integer[] dropdownOptions = new Integer[size];
                for (int i = 0; i < size; i++) {
                    dropdownOptions[i] = options[i];
                }
                Integer selectedOption = (Integer) JOptionPane.showInputDialog(null,
                        "Select a task to remove:", "Remove Task", JOptionPane.QUESTION_MESSAGE, null,
                        dropdownOptions, dropdownOptions[0]
                );
                writer.println(selectedOption);
                this.getContentPane().removeAll();
                mainMenu(username);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private JTextArea viewTasks(String username) {
        JTextArea messageFeedArea = null;
        try {
            writer.println("View Tasks");
            writer.println(username);

            messageFeedArea = new JTextArea();
            messageFeedArea.setEditable(false);
            messageFeedArea.setFont(new Font("Calibri", Font.BOLD, 30));

            String tasks = "";
            int i = 1;
            while (!((tasks = reader.readLine()).equals("END"))) {
                messageFeedArea.append(i + ". " + tasks + "\n");
                i++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return messageFeedArea;
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