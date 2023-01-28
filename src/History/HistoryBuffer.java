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

    /**
     * Удаление задачи по его номеру из промежуточного хранилища задач, которое обеспечивает работу связанного списка
     * @param id - номер задачи к удалению
     */
    public void removeNodeFromHistoryBuffer(int id) {
        bufferHistoryMap.remove(id);
    }

    public Collection<Task> getHistoryListFromBuffer() {
        final ArrayList<Task> history = new ArrayList<>();
        Node<Task> currentNode = head;
        while (currentNode != null) {
            history.add(currentNode.getData());
            currentNode = currentNode.getNext();
        }
        return history;
    }

    /**
     * добавление просмотренной задачи в связанный список
     * @param task - просмотренная задача
     */
    public void addLinkToLastNode(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) head = newNode;
        else oldTail.setNext(newNode);
        bufferHistoryMap.put(task.getTaskIdNumber(), newNode);
    }

    /**
     * удаление просмотренной задачи из связанного списка
     * @param id - неомер задачи для удаления
     */
    public void removeLinksToNode(int id) {
        Node<Task> node = bufferHistoryMap.get(id);
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

    public boolean isPresentInHistory(Task task) {
        return bufferHistoryMap.containsKey(task.getTaskIdNumber());
    }
}