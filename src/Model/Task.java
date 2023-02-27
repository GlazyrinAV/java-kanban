package Model;

import java.time.LocalDateTime;

public abstract class Task {
    private final String taskTitle;
    private final String taskDescription;
    private final int taskIdNumber;
    private static int idSequence = 1;
    private TaskStatus taskStatus;

    protected LocalDateTime startTime;

    protected long duration;

    /**
     * Конструктор для новой задачи.
     * Присваивается новый порядковый номер
     * Для новых задач статус NEW
     *
     * @param task - объект класса NewTask для создания новых задач
     */
    public Task(NewTask task) {
        this.taskTitle = task.getTaskTitle();
        this.taskDescription = task.getTaskDescription();
        this.taskIdNumber = idSequence++;
        setTaskStatus(TaskStatus.NEW);
        this.startTime = task.getStartTime();
        this.duration = task.getDuration();
    }

    /**
     * Конструктор для обновления эпиков.
     * Номер остается прежним и указывается при обновлении
     */
    public Task(Task epicTask) {
        this.taskTitle = epicTask.taskTitle;
        this.taskDescription = epicTask.taskDescription;
        this.taskIdNumber = epicTask.taskIdNumber;
        this.taskStatus = epicTask.taskStatus;
        this.startTime = epicTask.getStartTime();
        this.duration = epicTask.getDuration();
    }

    /**
     * Конструктор для обновления простых задач и подзадач эпиков.
     * Номер остается прежним и указывается при обновлении
     * Статус указывается при обновлении
     * @param taskStatus      - статус обновляемой задачи
     */
    public Task(Task task, TaskStatus taskStatus) {
        this.taskTitle = task.taskTitle;
        this.taskDescription = task.taskDescription;
        this.taskIdNumber = task.getTaskIdNumber();
        setTaskStatus(taskStatus);
        this.startTime = task.getStartTime();
        this.duration = task.getDuration();
    }

    /**
     * Конструктор для загрузки задач из файла данных
     *
     * @param taskTitle       - название задачи
     * @param taskDescription - описание задачи
     * @param taskStatus      - статус задачи
     * @param taskIdNumber    - номер задачи
     */
    public Task(String taskTitle, String taskDescription, TaskStatus taskStatus, int taskIdNumber, LocalDateTime startTime, long duration) {
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.taskIdNumber = taskIdNumber;
        this.taskStatus = taskStatus;
        idSequence = taskIdNumber + 1;
        this.startTime = startTime;
        this.duration = duration;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public int getTaskIdNumber() {
        return taskIdNumber;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public static void resetCounterForTest() {
        idSequence = 1;
    }

    protected void setTaskStatus(TaskStatus newStatus) {
        taskStatus = newStatus;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public long getDuration() {
        return duration;
    }

    public LocalDateTime getEndTime() {
        return calculateEndTime();
    }

    protected LocalDateTime calculateEndTime() {
        if (startTime == null) return null;
        return startTime.plusMinutes(duration);
    }

    @Override
    public String toString() {
        return "№" + taskIdNumber + ". Задача" +
                ". Название задачи - " + taskTitle +
                ". Описание задачи: " + taskDescription +
                ". Статус задачи: " + taskStatus +
                ". Время начала: " + startTime +
                ". Продолжительность: " + duration +
                ". Время окончания: " + calculateEndTime();
    }
}