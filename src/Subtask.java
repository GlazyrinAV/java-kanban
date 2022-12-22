public class Subtask extends Task {
    public Subtask(String taskTitle, String taskDescription) {
        super(taskTitle, taskDescription);
        setTaskStatus(0);
    }

    public Subtask(String taskTitle, String taskDescription, int taskIdNumber, int taskStatus) {
        super(taskTitle, taskDescription, taskIdNumber, taskStatus);
    }

    @Override
    public String toString() {
        return  "\n №" + getTaskIdNumber() + ". Название подзадачи: " + getTaskTitle() +
                ". Описание подзадачи: " + getTaskDescription() +
                ". Статус подзадачи: " + getStatusName(getTaskStatus());
    }
}