package Manager;
import Model.Task;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    static private int historyLength = 10;

    static protected List<Task> history = new ArrayList<>(historyLength);

    /**
     * Конструктор истории посещений
     * @param historyLength - максимальная длинна истории
     */
    public InMemoryHistoryManager(int historyLength) {
        InMemoryHistoryManager.historyLength = historyLength;
    }

    static private int count = 0;
    @Override
    public void addHistory(Task task) {
        if (count < historyLength) {
            history.add(task);
            count++;
        } else if (count == historyLength) {
            history.remove(0);
            List<Task> swap = new ArrayList<>(history);
            history.clear();
            history.addAll(swap);
            history.add(task);
        }
    }

    public List<Task> getHistory() {
        return history;
    }
}