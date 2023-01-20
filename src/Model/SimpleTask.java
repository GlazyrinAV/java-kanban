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
     * @param taskStatus      - статус обновляемой задачи
     */
    public SimpleTask(Task simpleTask, TaskStatus taskStatus) {
        super(simpleTask, taskStatus);
    }
}