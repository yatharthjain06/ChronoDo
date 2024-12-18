import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server implements Serializable, Runnable {
    private ArrayList<User> users;
    Socket socket;

    // Testing constructor - delete later
    public Server() {

    }

    public Server(Socket socket) {
        this.socket = socket;
        this.users = new ArrayList<>();
    }

    public static ArrayList<User> readUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("users.txt"))) {
            return (ArrayList<User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    public User findUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public boolean login(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    public String createUser(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return "User already exists";
            }
        }
        users.add(new User(username, password));
        return "User created!";
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());

            while (socket.isConnected()) {
                if (reader.ready()) {
                    String option = reader.readLine();
                    if (option.equals("Login")) {
                        String username = reader.readLine();
                        String password = reader.readLine();
                        writer.write(String.valueOf(login(username, password)));
                        writer.println();
                        writer.flush();
                    } else if (option.equals("Create Account")) {
                        String username = reader.readLine();
                        String password = reader.readLine();
                        writer.write(createUser(username, password));
                        writer.println();
                        writer.flush();
                    } else if (option.equals("View Tasks")) {
                        User user = findUser(reader.readLine());
                        ArrayList<String> tasks = user.getTasks();
                        for (String task : tasks) {
                            writer.write(task);
                            writer.println();
                            writer.flush();
                        }
                        writer.write("END");
                        writer.println();
                        writer.flush();
                    } else if (option.equals("Add Task")) {
                        User user = findUser(reader.readLine());
                        String task = reader.readLine();
                        if (!task.equals("null")) {
                            user.addTask(task);
                        }

                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(4242);
        while (true) {
            Socket socket = serverSocket.accept();
            Server server = new Server(socket);
            new Thread(server).start();
        }
    }
}
