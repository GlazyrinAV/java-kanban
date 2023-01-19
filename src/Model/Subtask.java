package  Model;
public class Subtask extends Task {

    int epicId;

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
     * @param taskTitle       - название подзадачи
     * @param taskDescription - описание подзадачи
     * @param taskIdNumber    - номер подзадачи
     * @param taskStatus      - статус подзадачи
     */
    public Subtask(String taskTitle, String taskDescription, int taskIdNumber, TaskStatus taskStatus, int epicId) {
        super(taskTitle, taskDescription, taskIdNumber, taskStatus);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return  "\n №" + getTaskIdNumber() + ". Название подзадачи: " + getTaskTitle() +
                ". Описание подзадачи: " + getTaskDescription() +
                ". Статус подзадачи: " + getTaskStatus();
    }
}