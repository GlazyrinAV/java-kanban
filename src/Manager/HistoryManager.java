package Manager;

import Model.Task;

import java.util.Collection;

public interface HistoryManager {
    /**
     * Добавляет последнюю вызванную задачу в историю
     * @param task - последняя вызванная такска
     */
    void addHistory(Task task);

    /**
     * Удаляет задачу из истории
     */
    void clearHistory();

    /**
     * Метод получения истории о последних вызванных тасках
     * @return - возвращает лист с последними вызванными тасками
     */
    Collection<Task> getHistory();
}