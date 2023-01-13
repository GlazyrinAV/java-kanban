package Manager;
import Model.*;

import java.util.Collection;
import java.util.HashMap;

public interface TaskManager {
    /**
     * Получение списка всех задач в виде единого хранилища
     * @return - единое хранилище, которое состоит из всех задач
     */
    HashMap<Integer, Task> getAllTasks();

    /**
     * Удаляет все виды задач
     */
    void clearAllTasks();

    /**
     * Получение задачи по объявленному номеру
     * @param taskId - номер задачи
     * @return       - искомый объект или null, если он не найден
     */
    Task getTaskById (int taskId);

    /**
     * Создание новой простой задачи
     * @param taskTitle       - название задачи
     * @param taskDescription - описание задачи
     */
    void newTask(String taskTitle, String taskDescription);

    /**
     * Создание нового эпика
     * @param taskTitle       - название эпика
     * @param taskDescription - описание эпика
     */
    void newEpic(String taskTitle, String taskDescription);

    /**
     * Создание подзадачи для эпика
     * @param epicId          - номер Эпика
     * @param taskTitle       - название подзадачи
     * @param taskDescription - описание подзадачи
     */
    void newSubtask (int epicId, String taskTitle, String taskDescription);

    /**
     * Обновление простой задачи и подзадачи эпика по объявленному номеру с возможностью установить новый статус
     * @param taskId          - номер задачи
     * @param taskTitle       - название задачи
     * @param taskDescription - описание задачи
     * @param taskStatus      - статус задачи
     */
    void updateTask(int taskId, String taskTitle, String taskDescription, TaskStatus taskStatus);

    /**
     * Перегруженный метод обновления для эпика по объявленному номеру.
     * При обновлении эпика вложенные подзадачи могут сохраняться или удаляться.
     * @param taskId          - номер эпика
     * @param taskTitle       - название эпика
     * @param taskDescription - описание эпика
     * @param saveSubTasks    - опредеяет необходимость сохранения подзадач в обновленном эпике
     * (true - сохранить, false - удалить).
     */
    void updateTask(int taskId, String taskTitle, String taskDescription, boolean saveSubTasks);

    /**
     * Метод проверяет все задачи и подзадачи и удаляет задачу с объявленным номером
     * @param taskId - номер задачи, которую необходимо удалить
     */
    void removeTaskById(int taskId);

    /**
     * Получение списка всех подзадач определённого эпика по номеру данного эпика
     * Возвращает HashMap, состоящий из подзадач или null, если не найден эпик или у него отсутствуют подзадачи
     * @param epicId - номер задачи
     * @return       - список подзадач выбранного эпика
     */
    HashMap<Integer, Subtask> getSubTasksOfEpicById(int epicId);

    /**
     * Метод возвращает номер задачи по имени данной задачи
     * @param name - имя искомой задачи
     * @return     - номер искомой задачи
     */
    Integer getTaskIdByName(String name);

    /**
     * Получение истории вызовозов задач
     * @return - список вызванных задач
     */
    Collection<Task> getHistory();

    HashMap<Integer, Task> getTasks();
}