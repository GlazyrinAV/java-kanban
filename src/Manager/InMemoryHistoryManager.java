package Manager;
import Model.Task;
import java.util.ArrayDeque;
import java.util.Collection;


public class InMemoryHistoryManager implements HistoryManager {
    private final int historyLength;
    private final ArrayDeque<Task> history;

    public InMemoryHistoryManager(int historyLength) {
        history = new ArrayDeque<>(historyLength);
        this.historyLength = historyLength;
    }

    private int count = 0;

    public void addHistory(Task task) {
        if (count < historyLength) {
            history.add(task);
            count++;
        } else if (count == historyLength) {
            history.removeFirst();
            history.add(task);
        }
    }

    public Collection<Task> getHistory() {
        return history;
    }
}