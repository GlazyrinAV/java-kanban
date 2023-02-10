package Manager;

import History.HistoryBuffer;

import java.util.Collection;

public class InMemoryHistoryManager implements HistoryManager {

    private final History.HistoryBuffer historyBuffer;

    public InMemoryHistoryManager() {
        this.historyBuffer = new HistoryBuffer();
    }

    @Override
    public void addHistory(int id) {
        if (historyBuffer.isPresentInHistory(id)) {
            removeHistory(id);
            historyBuffer.addLinkToLastNode(id);
        } else {
            historyBuffer.addLinkToLastNode(id);
        }

    }

    @Override
    public void removeHistory(int id) {
        historyBuffer.removeLinksToNode(id);
        historyBuffer.removeNodeFromHistoryBuffer(id);
    }

    @Override
    public Collection<Integer> getHistory() {
        return historyBuffer.getHistoryListFromBuffer();
    }
}