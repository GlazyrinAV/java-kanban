package Model;

import java.time.LocalDateTime;

public class Subtask extends Task {

    private final int epicId;

    /**
     * Конструктор для создания новых подзадач.
     * Присваивается новый порядковый номер
     * Статус для новых задач NEW
     * @param task - объект класса NewTask для создания новых задач
     * @param epicId - номер эпика, в который входит подзадача
     */
    public Subtask(NewTask task, int epicId) {
        super(task);
        this.taskType = TaskType.SUBTASK;
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
        this.taskType = TaskType.SUBTASK;
    }

    public Subtask(String taskTitle, String taskDescription, TaskStatus taskStatus, int taskIdNumber, int epicId,
                   LocalDateTime startTime, long duration) {
        super(taskTitle, taskDescription, taskStatus, taskIdNumber, startTime, duration);
        this.epicId = epicId;
        this.taskType = TaskType.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "\n №" + getTaskIdNumber() + ". Название подзадачи: " + getTaskTitle() +
                ". Описание подзадачи: " + getTaskDescription() +
                ". эпик задачи " + getEpicId() +
                ". Статус подзадачи: " + getTaskStatus() +
                ". Время начала: " + getStartTime() +
                ". Продолжительность: " + getDuration() +
                ". Время окончания: " + calculateEndTime() + "\n";
    }
}