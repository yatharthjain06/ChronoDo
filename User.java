import java.io.Serializable;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

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

    public void removeTask(int index) {
        tasks.remove(index);
    }

    public ArrayList<String> getTasks() {
        return tasks;
    }

    public void startTimer(long duration) {
        AtomicLong timeRemaining = new AtomicLong(duration);
        TimerTask start = new TimerTask() {
            @Override
            public void run() {
                if (timeRemaining.get() > 0) {
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(timeRemaining.get());
                    long seconds = (TimeUnit.MILLISECONDS.toSeconds(timeRemaining.get()) % 60);
                    if (seconds < 10) {
                        System.out.println(minutes + ":0" + seconds);
                    } else {
                        System.out.println(minutes + ":" + seconds);
                    }

                    timeRemaining.addAndGet(-1000);
                } else {
                    System.out.println("Timer finished!");
                    pomodoro.cancel();
                }
            }
        };
        pomodoro.scheduleAtFixedRate(start, 0, 1000);
    }


}
