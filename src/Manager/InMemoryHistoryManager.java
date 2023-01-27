package Manager;

import History.Node;
import Model.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;


public class InMemoryHistoryManager implements HistoryManager {

    private final LinkedHashMap<Integer, Node<Task>> bufferHistoryMap = new LinkedHashMap<>();

    @Override
    public void addHistory(Task task) {
        if (isPresentInHistory(task)) {
            removeHistoryNote(task.getTaskIdNumber());
            linkLast(task);
        } else {
            linkLast(task);
        }
    }

    @Override
    public Collection<Task> getHistory() {
        final ArrayList<Task> history = new ArrayList<>();
        for (int taskId : bufferHistoryMap.keySet()) {
            history.add(bufferHistoryMap.get(taskId).data);
        }
        return history;
    }

    @Override
    public void removeHistoryNote(int id) {
        removeLink(bufferHistoryMap.get(id));
        bufferHistoryMap.remove(id);
    }

    private void linkLast(Task task) {
        final Node<Task> oldTail = Node.getTail();
        final Node<Task> newNode = new Node<>(oldTail, task, null);
        Node.setTail(newNode);
        if (oldTail == null) Node.setHead(newNode);
        else oldTail.next = newNode;
        bufferHistoryMap.put(task.getTaskIdNumber(), newNode);
    }

    private void removeLink(Node<Task> node) {
        if (node.equals(Node.getHead())) {
            node.next.prev = null;
            Node.setHead(node.next);
        } else if (node.equals(Node.getTail())) {
            node.prev.next = null;
            Node.setTail(node.prev);
        } else {
            node.next.prev = node.prev;
            node.prev.next = node.next;
        }
    }

    private boolean isPresentInHistory(Task task) {
        return bufferHistoryMap.containsKey(task.getTaskIdNumber());
    }
}