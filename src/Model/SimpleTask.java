package Model;

public class SimpleTask extends Task{

    /**
     * Конструктор для новой простой задачи.
     * Присваивается новый порядковый номер
     * Для новых задач статус NEW
     * @param taskTitle       - название задачи
     * @param taskDescription - описание задачи
     */
    public SimpleTask(String taskTitle, String taskDescription) {
        super(taskTitle, taskDescription);
    }

    /**
     * Конструктор для обновления простой задачи.
     * Номер остается прежним и указывается при обновлении
     * Статус указывается при обновлении
     * @param taskTitle       - название задачи
     * @param taskDescription - описание задачи
     * @param taskIdNumber    - номер обновляемой задачи
     * @param taskStatus      - статус обновляемой задачи
     */
    public SimpleTask(String taskTitle, String taskDescription, int taskIdNumber, TaskStatus taskStatus) {
        super(taskTitle, taskDescription, taskIdNumber, taskStatus);
    }
}