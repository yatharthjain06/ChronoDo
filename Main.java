import java.util.Scanner;
import java.util.TimerTask;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter username");
        String username = scanner.nextLine();
        System.out.println("Enter password");
        String password = scanner.nextLine();
        User user = new User(username, password);
        user.startTimer();
    }
}
