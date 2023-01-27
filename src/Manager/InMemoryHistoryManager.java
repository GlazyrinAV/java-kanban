package Manager;

import History.Node;
import Model.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;


public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Integer, Node<Task>> bufferHistoryMap = new HashMap<>();

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
    public void removeHistoryNote(int id) {
        removeLink(bufferHistoryMap.get(id));
        bufferHistoryMap.remove(id);
    }

    @Override
    public Collection<Task> getHistory() {
        final ArrayList<Task> history = new ArrayList<>();
        Node<Task> currentNode = Node.getHead();
        while (currentNode != null) {
            history.add(currentNode.data);
            currentNode = currentNode.next;
        }
        return history;
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