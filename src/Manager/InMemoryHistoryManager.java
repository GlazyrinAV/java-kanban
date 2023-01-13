package Manager;
import Model.Task;
import java.util.ArrayDeque;


public class InMemoryHistoryManager implements HistoryManager {
    int historyLength;
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

    public ArrayDeque<Task> getHistory() {
        return history;
    }
}