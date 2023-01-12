package Manager;
import Model.*;
import java.util.List;

public interface HistoryManager {
    void addHistory(Task task);
    List<Task> getHistory();

    static List<Task> getDefaultHistory() {
        return InMemoryHistoryManager.history;
    }
}