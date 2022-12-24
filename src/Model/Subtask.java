package  Model;
public class Subtask extends Task {
    /**
     * Конструктор для создания новых подзадач
     * Присваивается новый порядковый номер
     * Статус для новых задач NEW
     * @param taskTitle - название подзадачи
     * @param taskDescription - описание подзадачи
     */
    public Subtask(String taskTitle, String taskDescription) {
        super(taskTitle, taskDescription);
        setTaskStatus(TaskStatus.Status.NEW);
    }

    /**
     * Конструктор для обновления подзадач
     * Номер остается прежним и указывается при обновлении
     * Статус указывается при обновлении
     * @param taskTitle - название подзадачи
     * @param taskDescription - описание подзадачи
     * @param taskIdNumber - номер подзадачи
     * @param taskStatus - статус подзадачи
     */
    public Subtask(String taskTitle, String taskDescription, int taskIdNumber, TaskStatus.Status taskStatus) {
        super(taskTitle, taskDescription, taskIdNumber, taskStatus);
    }

    @Override
    public String toString() {
        return  "\n №" + getTaskIdNumber() + ". Название подзадачи: " + getTaskTitle() +
                ". Описание подзадачи: " + getTaskDescription() +
                ". Статус подзадачи: " + getTaskStatus();
    }
}