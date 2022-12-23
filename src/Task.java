public class Task {
    private String taskTitle;
    private String taskDescription;
    private int taskIdNumber;
    private static int idSequence = 1;
    private int taskStatus;
    private final String[] statusArray = {"NEW", "IN_PROGRESS", "DONE"};

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
        this.taskStatus = 0;
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
    public Task(String taskTitle, String taskDescription, int taskIdNumber, int taskStatus) {
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

    public int getTaskStatus() {
        return taskStatus;
    }

    public String getStatusName(int taskStatus) {
        return statusArray[taskStatus];
    }

    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }

    @Override
    public String toString() {
        String result = "";
        result= "№" + taskIdNumber + ". Задача" +
                ". Название задачи - " + taskTitle +
                ". Описание задачи: " + taskDescription +
                ". Статус задачи: " + getStatusName(taskStatus);
        return result;
    }
}