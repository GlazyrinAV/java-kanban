package Manager;

import History.Node;
import Model.Task;

import java.util.ArrayList;
import java.util.Collection;

public class InMemoryHistoryManager implements HistoryManager {

    History.HistoryBuffer historyManager = new History.HistoryBuffer();

    @Override
    public void addHistory(Task task) {
        if (isPresentInHistory(task)) {
            removeHistoryNote(task.getTaskIdNumber());
            historyManager.addLink(task);
        } else {
            historyManager.addLink(task);
        }
    }

    @Override
    public void removeHistoryNote(int id) {
        historyManager.removeLink(historyManager.getBufferHistoryMap().get(id));
        historyManager.getBufferHistoryMap().remove(id);
    }

    @Override
    public Collection<Task> getHistory() {
        final ArrayList<Task> history = new ArrayList<>();
        Node<Task> currentNode = historyManager.getHead();
        while (currentNode != null) {
            history.add(currentNode.getData());
            currentNode = currentNode.getNext();
        }
        return history;
    }

    private boolean isPresentInHistory(Task task) {
        return historyManager.getBufferHistoryMap().containsKey(task.getTaskIdNumber());
    }
}