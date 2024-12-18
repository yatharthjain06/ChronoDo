import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
        this.users = readUsers();
    }

    public ArrayList<User> readUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("users.txt"))) {
            return (ArrayList<User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    public void writeUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("users.txt"))) {
            oos.writeObject(users);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public User findUser(String username) {
        users = readUsers();
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public boolean login(String username, String password) {
        users = readUsers();
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    public String createUser(String username, String password) {
        if (findUser(username) != null) {
            return "User already exists";
        } else {
            users.add(new User(username, password));
            writeUsers();
            return "User created!";
        }
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());

            while (socket.isConnected()) {
                if (reader.ready()) {
                    String option = reader.readLine();
                    switch (option) {
                        case "Login" -> {
                            String username = reader.readLine();
                            String password = reader.readLine();
                            writer.write(String.valueOf(login(username, password)));
                            writer.println();
                            writer.flush();
                        }
                        case "Create Account" -> {
                            String username = reader.readLine();
                            String password = reader.readLine();
                            writer.write(createUser(username, password));
                            writer.println();
                            writer.flush();
                        }
                        case "View Tasks" -> {
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
                        }
                        case "Add Task" -> {
                            User user = findUser(reader.readLine());
                            String task = reader.readLine();
                            if (!task.equals("null")) {
                                user.addTask(task);
                                writeUsers();
                            }
                        }
                        case "Remove Task" -> {
                            User user = findUser(reader.readLine());
                            ArrayList<String> tasks = user.getTasks();
                            writer.write(String.valueOf(tasks.size()));
                            writer.println();
                            writer.flush();
                            if (!(tasks.isEmpty())) {
                                try {
                                    int indexToRemove = Integer.parseInt(reader.readLine()) - 1;
                                    user.removeTask(indexToRemove);
                                    writeUsers();
                                } catch (NumberFormatException _) {
                                }
                            }
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
