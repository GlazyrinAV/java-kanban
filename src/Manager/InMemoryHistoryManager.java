package Manager;
import Model.Task;

import java.util.ArrayDeque;

public class InMemoryHistoryManager implements HistoryManager {
    static int historyLength = 10;

    /**
     * Конструктор истории посещений
     * @param historyLength - максимальная длинна истории
     */
    public InMemoryHistoryManager(int historyLength) {
        InMemoryHistoryManager.historyLength = historyLength;
    }

    static ArrayDeque<Task> history = new ArrayDeque<>(historyLength);

    @Override
    public void addHistory(Task task) {
        history.add(task);
    }

    public ArrayDeque<Task> getHistory() {
        return history;
    }

}