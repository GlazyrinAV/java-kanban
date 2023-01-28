package History;

import Model.Task;

import java.util.HashMap;

public class HistoryBuffer {
    private final HashMap<Integer, Node<Task>> bufferHistoryMap = new HashMap<>();
    private Node<Task> head = null;
    private Node<Task> tail = null;

    public void addLink(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) head = newNode;
        else oldTail.setNext(newNode);
        bufferHistoryMap.put(task.getTaskIdNumber(), newNode);
    }

    public void removeLink(Node<Task> node) {
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

    public Node<Task> getHead() {
        return head;
    }

    public HashMap<Integer, Node<Task>> getBufferHistoryMap() {
        return bufferHistoryMap;
    }
}