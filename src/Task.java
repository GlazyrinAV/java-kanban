public class Task {
    private String taskTitle;
    private String taskDescription;
    private int taskIdNumber;
    private static int idSequence = 1;
    protected enum TaskStatus {NEW, IN_PROGRESS, DONE};
    TaskStatus taskStatus;

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
        taskStatus = TaskStatus.NEW;
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
    public Task(String taskTitle, String taskDescription, int taskIdNumber, TaskStatus taskStatus) {
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.taskIdNumber = taskIdNumber;
        this.taskStatus = taskStatus;
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

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    @Override
    public String toString() {
        String result = "";
        result= "№" + taskIdNumber + ". Задача" +
                ". Название задачи - " + taskTitle +
                ". Описание задачи: " + taskDescription +
                ". Статус задачи: " + taskStatus;
        return result;
    }
}