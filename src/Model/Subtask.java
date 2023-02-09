package  Model;
public class Subtask extends Task {

    private final int epicId;

    /**
     * Конструктор для создания новых подзадач
     * Присваивается новый порядковый номер
     * Статус для новых задач NEW
     * @param taskTitle       - название подзадачи
     * @param taskDescription - описание подзадачи
     */
    public Subtask(String taskTitle, String taskDescription, int epicId) {
        super(taskTitle, taskDescription);
        this.epicId = epicId;
        setTaskStatus(TaskStatus.NEW);
    }

    /**
     * Конструктор для обновления подзадач
     * Номер остается прежним и указывается при обновлении
     * Статус указывается при обновлении
     * @param taskStatus - статус подзадачи
     */
    public Subtask(Task subTask, TaskStatus taskStatus) {
        super(subTask, taskStatus);
        this.epicId = ((Subtask) subTask).getEpicId();
    }

    public Subtask(String taskTitle, String taskDescription, TaskStatus taskStatus, int taskIdNumber, int epicId) {
        super(taskTitle, taskDescription, taskStatus, taskIdNumber);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "\n №" + getTaskIdNumber() + ". Название подзадачи: " + getTaskTitle() +
                ". Описание подзадачи: " + getTaskDescription() +
                ". эпик задачи " + getEpicId() +
                ". Статус подзадачи: " + getTaskStatus();
    }
}