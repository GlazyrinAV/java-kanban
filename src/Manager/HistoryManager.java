package Manager;
import Model.*;
import java.util.ArrayDeque;

public interface HistoryManager {
    void addHistory(Task task);
    ArrayDeque<Task> getHistory();

    static ArrayDeque<Task> getDefaultHistory() {
        return InMemoryHistoryManager.history;
    }
}