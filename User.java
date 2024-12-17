import java.io.Serializable;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class User implements Serializable {
    private String username;
    private String password;
    private ArrayList<String> tasks;
    private Timer pomodoro;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.tasks = new ArrayList<>();
        this.pomodoro = new Timer(username);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void addTask(String task) {
        tasks.add(task);
    }

    public void removeTask(String task) {
        tasks.remove(task);
    }

    public void startTimer() {
        TimerTask start = new TimerTask() {
            int duration = 25 * 60 * 1000;
            @Override
            public void run() {
                //System.out.println(duration);
                int minutes = duration / 1000 / 60;
                int seconds = duration / 1000;
                System.out.println(minutes + ":" + seconds);


                duration--;
            }
        };

        pomodoro.scheduleAtFixedRate(start, 0, 1000);
    }


}
