package Model;

import Manager.InMemoryTaskManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EpicTask extends Task {
    private final List<Integer> subTasks = new ArrayList<>();

    /**
     * Конструктор для создания новых эпиков
     * Присваивается новый порядковый номер
     * @param taskTitle       - название эпика
     * @param taskDescription - описание эпика
     */
    public EpicTask(String taskTitle, String taskDescription) {
        super(taskTitle, taskDescription);
    }

    /**
     * Конструктор для обновления эпика
     * Номер остается прежним и указывается при обновлении
     * @param taskTitle       - название эпика
     * @param taskDescription - описание эпика
     * @param taskIdNumber    - номер обновляемого эпика
     */
    public EpicTask(String taskTitle, String taskDescription, int taskIdNumber) {
        super(taskTitle, taskDescription, taskIdNumber);
    }

    public void addSubTask(int subTaskId) {
        subTasks.add(subTaskId);
    }

    public void removeSubTask(int subTaskId) {
        subTasks.remove((Integer) subTaskId);
    }

    /**
     * Возвращает лист с сабтасками входящими в эпик
     * @return - копия листа с сабтасками
     */
    public List<Integer> getSubTasks() {
        return new ArrayList<>(subTasks);
    }

    /**
     * определяет статус эпика через проверку статусов сабтасков,
     * хранящихся в таскмэнеджере
     * @param epicID - номер эпика, который подлежит обновлению
     */
    public void updateStatus(InMemoryTaskManager inMemoryTaskManager, int epicID) {
        taskStatus = inMemoryTaskManager.defineStatus(epicID);
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
            result = result + ". Эпик содержит следующие подзадачи: \n" + Arrays.toString(subTasks.toArray());
        }
        return  result;
    }
}