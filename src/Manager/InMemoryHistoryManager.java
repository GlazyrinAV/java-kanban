package Manager;
import Model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final int historyLength; // желаемый объем хранимой истории
    private final List<Task> history;
    private final Map<Integer, Node> temporaryMapForHistory = new HashMap<>();

    public InMemoryHistoryManager(int historyLength) {
        history = new ArrayList<>(historyLength);
        this.historyLength = historyLength;
    }

    private int historyItemsCounter = 0; // техническая величина для проверки заполнения истории

    @Override
    public void addHistory(Task task) {
        if (isPresentInHistory(task)) {
            removeHistoryNote(task.getTaskIdNumber());
            linkLast(task);
        } else {
            if (historyItemsCounter <= historyLength) {
                linkLast(task);
            } else {
                removeLink(tail);
                linkLast(task);
            }
        }
    }

    @Override
    public Collection<Task> getHistory() {
        Node<Task> currentNode = head;
        while (currentNode.next != null) {
            history.add(currentNode.data);
            currentNode = currentNode.next;
        }
        return history;
    }

    @Override
    public void removeHistoryNote(int id) {
        removeLink(temporaryMapForHistory.get(id));
        temporaryMapForHistory.remove(id);
        historyItemsCounter--;
    }

    private Node<Task> head = null;
    private Node<Task> tail = null;

    private void linkLast(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) head = newNode;
        else oldTail.prev = newNode;
        temporaryMapForHistory.put(task.getTaskIdNumber(), newNode);
    }

    private void removeLink(Node node) {
        if (node.equals(head)) {
            head = node.next;
            head.prev = null;
        } else if (node.equals(tail)) {
            tail = node.prev;
            tail.next = null;
        } else {
            Node<Task> newHead = node.prev;
            Node<Task> newTail = node.next;
            newHead.next = node.next;
            newTail.prev = node.prev;
        }
    }

    private boolean isPresentInHistory(Task task) {
        return temporaryMapForHistory.containsKey(task.getTaskIdNumber());
    }

    static class Node<T extends Task> {
        public T data;
        public Node<T> next;
        public Node<T> prev;

        public Node(Node<T> prev, T data, Node<T> next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }

    @Override
    public String toString() {
        return Arrays.toString(history.toArray());
    }
}