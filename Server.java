import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
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

    @Override
    public void run() {
        while (socket.isConnected()) {

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
