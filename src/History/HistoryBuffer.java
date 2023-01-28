package History;

import Model.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class HistoryBuffer {
    private final HashMap<Integer, Node<Task>> bufferHistoryMap = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;

    public HistoryBuffer() {
        this.head = null;
        this.tail = null;
    }

    public void addHistoryToBuffer(Task task){
        if (isPresentInHistory(task)) {
            removeHistoryFromBuffer(task.getTaskIdNumber());
            addNode(task);
        } else {
            addNode(task);
        }
    }

    public void removeHistoryFromBuffer(int id) {
        removeNode(bufferHistoryMap.get(id));
        bufferHistoryMap.remove(id);
    }

    public Collection<Task> getHistoryFromBuffer() {
        final ArrayList<Task> history = new ArrayList<>();
        Node<Task> currentNode = head;
        while (currentNode != null) {
            history.add(currentNode.getData());
            currentNode = currentNode.getNext();
        }
        return history;
    }

    private void addNode(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) head = newNode;
        else oldTail.setNext(newNode);
        bufferHistoryMap.put(task.getTaskIdNumber(), newNode);
    }

    private void removeNode(Node<Task> node) {
        if (node.equals(head)) {
            node.getNext().setPrev(null);
            head = node.getNext();
        } else if (node.equals(tail)) {
            node.getPrev().setNext(null);
            tail = node.getPrev();
        } else {
            node.getNext().setPrev(node.getPrev());
            node.getPrev().setNext(node.getNext());
        }
    }

    private boolean isPresentInHistory(Task task) {
        return bufferHistoryMap.containsKey(task.getTaskIdNumber());
    }
}