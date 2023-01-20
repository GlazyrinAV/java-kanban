package Model;

public abstract class Task {
    private final String taskTitle;
    private final String taskDescription;
    private final int taskIdNumber;
    private static int idSequence = 1;
    private TaskStatus taskStatus;

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
        setTaskStatus(TaskStatus.NEW);
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

    protected void setTaskStatus(TaskStatus newStatus) {
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