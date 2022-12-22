public class Subtask extends Epic {
    public Subtask(String taskTitle, String taskDescription) {
        super(taskTitle, taskDescription);
        setTaskStatus(0);
    }

    @Override
    public String toString() {
        return  "\n №" + getTaskIdNumber() + ". Название подзадачи: " + getTaskTitle() +
                ". Описание подзадачи: " + getTaskDescription() +
                ". Статус подзадачи: " + getStatusName(getTaskStatus());
    }
}
