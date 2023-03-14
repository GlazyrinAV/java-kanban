package Model;

import java.time.LocalDateTime;

public class NewTask {

    private final String taskTitle;
    private final String taskDescription;

    private final LocalDateTime startTime;

    private final long duration;

    public NewTask(String taskTitle, String taskDescription) {
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.startTime = null;
        this.duration = 0;
    }

    public NewTask(String taskTitle, String taskDescription, LocalDateTime startTime, long duration) {
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.startTime = startTime;
        this.duration = duration;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public long getDuration() {
        return duration;
    }
}