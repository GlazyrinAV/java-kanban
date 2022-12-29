package Model;

public class Task {
    private final String taskTitle;
    private final String taskDescription;
    private final int taskIdNumber;
    private static int idSequence = 1;
    TaskStatus.Status taskStatus;

    /**
     * Конструктор для новой задачи.
     * Присваивается новый порядковый номер
     * Для новых задач статус NEW
     * @param taskTitle       - название задачи
     * @param taskDescription - описание задачи
     */
    public Task(String taskTitle, String taskDescription) {
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.taskIdNumber = idSequence++;
        setTaskStatus(TaskStatus.Status.NEW);
    }

    /**
     * Конструктор для обновления эпиков.
     * Номер остается прежним и указывается при обновлении
     * @param taskTitle       - название задачи
     * @param taskDescription - описание задачи
     * @param taskIdNumber    - номер обновляемой задачи
     */
    public Task(String taskTitle, String taskDescription, int taskIdNumber) {
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.taskIdNumber = taskIdNumber;
    }

    /**
     * Конструктор для обновления простых задач и подзадач эпиков.
     * Номер остается прежним и указывается при обновлении
     * Статус указывается при обновлении
     * @param taskTitle       - название задачи
     * @param taskDescription - описание задачи
     * @param taskIdNumber    - номер обновляемой задачи
     * @param taskStatus      - статус обновляемой задачи
     */
    public Task(String taskTitle, String taskDescription, int taskIdNumber, TaskStatus.Status taskStatus) {
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.taskIdNumber = taskIdNumber;
        setTaskStatus(taskStatus);
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

    public TaskStatus.Status getTaskStatus() {
        return taskStatus;
    }

    protected void setTaskStatus(TaskStatus.Status newStatus) {
        taskStatus = newStatus;
    }

    @Override
    public String toString() {
        return "№" + taskIdNumber + ". Задача" +
                ". Название задачи - " + taskTitle +
                ". Описание задачи: " + taskDescription +
                ". Статус задачи: " + taskStatus;
    }
}