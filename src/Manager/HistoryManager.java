package Manager;
import Model.*;
import java.util.ArrayDeque;

public interface HistoryManager {
    /**
     * Метод добавления последней вызванной такски в историю
     * @param task - последняя вызванная такска
     */
    static void addDefaultHistory(Task task) {
        InMemoryHistoryManager.addHistory(task);
    }

    /**
     * Метод получения истории о последних вызванных тасках
     * @return - возвращает лист с последними вызванными тасками
     */
    static ArrayDeque<Task> getDefaultHistory() {
        return InMemoryHistoryManager.getHistory();
    }
}