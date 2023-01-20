package Manager;
import Model.Task;
import java.util.ArrayDeque;
import java.util.Collection;

public class InMemoryHistoryManager implements HistoryManager {
    private final int historyLength; // желаемый объем хранимой истории
    private final ArrayDeque<Task> history;

    public InMemoryHistoryManager(int historyLength) {
        history = new ArrayDeque<>(historyLength);
        this.historyLength = historyLength;
    }

    private int historyItemsCounter = 0; // техническая величина для проверки заполнения истории

    @Override
    public void addHistory(Task task) {

        if (historyItemsCounter < historyLength) {
            history.add(task);
            historyItemsCounter++;
        } else if (historyItemsCounter == historyLength) {
            history.removeFirst();
            history.add(task);
        }
    }

    @Override
    public Collection<Task> getHistory() {
        return history;
    }
}