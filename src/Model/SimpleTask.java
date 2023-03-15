package Model;

import java.time.LocalDateTime;

public class SimpleTask extends Task {

    /**
     * Конструктор для новой простой задачи.
     * Присваивается новый порядковый номер
     * Для новых задач статус NEW
     *
     * @param task - объект класса NewTask для создания новых задач
     */
    public SimpleTask(NewTask task) {
        super(task);
        taskType = TaskType.TASK;
    }

    /**
     * Конструктор для обновления простой задачи.
     * Номер остается прежним и указывается при обновлении
     * Статус указывается при обновлении
     *
     * @param taskStatus - статус обновляемой задачи
     */
    public SimpleTask(Task simpleTask, TaskStatus taskStatus) {
        super(simpleTask, taskStatus);
        taskType = TaskType.TASK;
    }

    public SimpleTask(String taskTitle, String taskDescription, TaskStatus taskStatus, int taskIdNumber,
                      LocalDateTime startTime, long duration, TaskType taskType) {
        super(taskTitle, taskDescription, taskStatus, taskIdNumber, startTime, duration, taskType);
    }
}