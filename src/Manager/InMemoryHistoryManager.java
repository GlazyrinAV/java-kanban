package Manager;

import History.HistoryBuffer;
import Model.Task;

import java.util.Collection;

public class InMemoryHistoryManager implements HistoryManager {

    private final History.HistoryBuffer historyBuffer;

    public InMemoryHistoryManager() {
        this.historyBuffer = new HistoryBuffer();
    }

    @Override
    public void addHistory(Task task) {
        historyBuffer.addNoteToHistoryBuffer(task);
    }

    @Override
    public void removeHistory(int id) {
        historyBuffer.removeNoteFromHistoryBuffer(id);
    }

    @Override
    public Collection<Task> getHistory() {
        return historyBuffer.getHistoryListFromBuffer();
    }
}