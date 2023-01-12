package Manager;
import Model.Task;
import java.util.ArrayDeque;


public class InMemoryHistoryManager implements HistoryManager {
    static private final int historyDefaultLength = 10;
    static private final int historyLength = historyDefaultLength;
    static private final ArrayDeque<Task> history = new ArrayDeque<>(historyLength);

    static private int count = 0;

    public static void addHistory(Task task) {
        if (count < historyLength) {
            history.add(task);
            count++;
        } else if (count == historyLength) {
            history.removeFirst();
            history.add(task);
        }
    }

    public static ArrayDeque<Task> getHistory() {
        return history;
    }
}