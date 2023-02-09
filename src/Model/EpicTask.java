package Model;

import java.util.*;

public class EpicTask extends Task {
    private final HashMap<Integer, TaskStatus> subTasks = new HashMap<>();

    /**
     * Конструктор для создания новых эпиков
     * Присваивается новый порядковый номер
     * @param taskTitle       - название эпика
     * @param taskDescription - описание эпика
     */
    public EpicTask(String taskTitle, String taskDescription) {
        super(taskTitle, taskDescription);
        updateStatus();
    }

    /**
     * Конструктор для обновления эпика
     * Номер остается прежним и указывается при обновлении
     */
    public EpicTask(Task epic) {
        super(epic);
        updateStatus();
    }

    public EpicTask(String taskTitle, String taskDescription, TaskStatus taskStatus, int taskIdNumber) {
        super(taskTitle, taskDescription, taskStatus, taskIdNumber);
    }

    public void addSubTask(int subTaskId, TaskStatus status) {
        subTasks.put(subTaskId, status);
        updateStatus();
    }

    public void removeSubTask(int subTaskId) {
        subTasks.remove(subTaskId);
        updateStatus();
    }

    /**
     * Возвращает лист с сабтасками входящими в эпик
     * @return - копия листа с сабтасками
     */
    public List<Integer> getSubTasksIds() {
        return new ArrayList<>(subTasks.keySet());
    }

    /**
     * Возвращает лист с уникальными статусами подзадач, входящих в эпик
     * @return - лист с уникальными статусами подзадач
     */
    private LinkedHashSet<TaskStatus> getSubTasksStatuses() {
        return new LinkedHashSet<>(subTasks.values());
    }

    /**
     * определяет статус эпика через проверку статусов сабтасков, хранящихся в таскмэнеджере
     */
    private void updateStatus() {
        if (getSubTasksIds().isEmpty()) {
            setTaskStatus(TaskStatus.NEW);
        } else {

            if (getSubTasksStatuses().size() == 1) {
                for (TaskStatus status : getSubTasksStatuses()) setTaskStatus(status);
            } else setTaskStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public String toString() {
        String result = "\n№" + getTaskIdNumber() + ". Эпик" +
                ". Название задачи - " + getTaskTitle() +
                ". Описание задачи: " + getTaskDescription() +
                ". Статус задачи: " + getTaskStatus();
        if (subTasks.isEmpty()) {
            result = result + ". Подзадачи отсутствуют.";
        } else {
            result = result + ". Эпик содержит следующие подзадачи: \n" + Arrays.toString(getSubTasksIds().toArray());
        }
        return  result;
    }
}