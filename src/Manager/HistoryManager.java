package Manager;
import Model.*;
import java.util.ArrayDeque;

public interface HistoryManager {
    /**
     * Метод добавления последней вызванной такски в историю
     * @param task - последняя вызванная такска
     */
    void addHistory(Task task);

    /**
     * Метод получения истории о последних вызванных тасках
     * @return - возвращает лист с последними вызванными тасками
     */
    ArrayDeque<Task> getHistory();
}