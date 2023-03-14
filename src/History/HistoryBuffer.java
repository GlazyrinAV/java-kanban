package History;

import Nodes.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class HistoryBuffer {
    private final HashMap<Integer, Node<Integer>> bufferHistoryMap = new HashMap<>();
    private Node<Integer> head;
    private Node<Integer> tail;

    public HistoryBuffer() {
        this.head = null;
        this.tail = null;
    }

    /**
     * Удаление задачи по его номеру из промежуточного хранилища задач, которое обеспечивает работу связанного списка
     *
     * @param id - номер задачи к удалению
     */
    public void removeNodeFromHistoryBuffer(int id) {
        bufferHistoryMap.remove(id);
    }

    public Collection<Integer> getHistoryListFromBuffer() {
        final ArrayList<Integer> history = new ArrayList<>();
        Node<Integer> currentNode = head;
        while (currentNode != null) {
            history.add(currentNode.getData());
            currentNode = currentNode.getNext();
        }
        return history;
    }

    /**
     * Добавление просмотренной задачи в связанный список
     *
     * @param id - номер задачи
     */
    public void addLinkToLastNode(int id) {
        final Node<Integer> oldTail = tail;
        final Node<Integer> newNode = new Node<>(oldTail, id, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        }
        else {
            oldTail.setNext(newNode);
        }
        bufferHistoryMap.put(id, newNode);
    }

    /**
     * Удаление просмотренной задачи из связанного списка
     *
     * @param id - номер задачи для удаления
     */
    public void removeLinksToNode(int id) {
        Node<Integer> node = bufferHistoryMap.get(id);
        if (node.equals(head)) {
            if (node.getNext() == null) {
                head = null;
                tail = null;
            } else {
                node.getNext().setPrev(null);
                head = node.getNext();
            }
        } else if (node.equals(tail)) {
            node.getPrev().setNext(null);
            tail = node.getPrev();
        } else {
            node.getNext().setPrev(node.getPrev());
            node.getPrev().setNext(node.getNext());
        }
    }

    public boolean isPresentInHistory(int id) {
        return bufferHistoryMap.containsKey(id);
    }
}