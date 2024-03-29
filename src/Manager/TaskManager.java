package Manager;

import Model.NewTask;
import Model.Task;
import Model.TaskStatus;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public interface TaskManager {

    /**
     * Создание новой простой задачи
     */
    void newSimpleTask(NewTask task);

    /**
     * Создание нового эпика
     */
    void newEpic(NewTask task);

    /**
     * Создание подзадачи для эпика
     * @param epicId - номер Эпика
     */
    void newSubtask(NewTask task, int epicId);

    /**
     * Обновление простой задачи и подзадачи эпика по объявленному номеру с возможностью установить новый статус
     * @param taskId          - номер задачи
     * @param taskStatus      - статус задачи
     */
    void updateTask(int taskId, TaskStatus taskStatus);

    /**
     * Перегруженный метод обновления для эпика по объявленному номеру.
     * При обновлении эпика вложенные подзадачи могут сохраняться или удаляться.
     * @param taskId          - номер эпика
     * @param saveSubTasks    - определяет необходимость сохранения подзадач в обновленном эпике
     * (true - сохранить, false - удалить).
     */
    void updateTask(int taskId, boolean saveSubTasks);

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
    Task getTaskById(int taskId);

    /**
     * Метод проверяет все задачи и подзадачи и удаляет задачу с объявленным номером
     *
     * @param taskId - номер задачи, которую необходимо удалить
     */
    Task removeTaskById(int taskId);

    /**
     * Метод возвращает список задач в приоритетном характере
     * @return - упорядоченный список задач
     */
    Set<Integer> getPrioritizedTasks();

    /**
     * Получение истории вызовов задач
     * @return - список вызванных задач
     */
    Collection<Integer> getHistory();
}