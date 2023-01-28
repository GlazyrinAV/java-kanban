package Manager;

import History.HistoryBuffer;
import Model.Task;

import java.util.Collection;

public class InMemoryHistoryManager implements HistoryManager {

    private final History.HistoryBuffer historyBufferMap;

    public InMemoryHistoryManager() {
        this.historyBufferMap = new HistoryBuffer();
    }

    @Override
    public void addHistory(Task task) {
        historyBufferMap.addHistoryToBuffer(task);
    }

    @Override
    public void removeHistory(int id) {
        historyBufferMap.removeHistoryFromBuffer(id);
    }

    @Override
    public Collection<Task> getHistory() {
        return historyBufferMap.getHistoryFromBuffer();
    }
}