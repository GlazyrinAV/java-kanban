public class Task {
    private String taskTitle;
    private String taskDescription;

    private int taskIdNumber;
    protected static int idSequence = 1;
    private int taskStatus;
    private final String[] statusArray = {"NEW", "IN_PROGRESS", "DONE"};

    public Task(String taskTitle, String taskDescription) {
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.taskIdNumber = idSequence++;
        this.taskStatus = 0;
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
        return  "№" + taskIdNumber + ". Задача" +
                ". Название задачи - " + taskTitle +
                ". Описание задачи: " + taskDescription +
                ". Статус задачи: " + getStatusName(taskStatus);
    }
}
