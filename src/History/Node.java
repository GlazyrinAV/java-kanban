package History;

import Model.Task;

public class Node<T extends Task> {

    private static Node<Task> head = null;
    private static Node<Task> tail = null;
    public T data;
    public Node<T> next;
    public Node<T> prev;
    public Node(Node<T> prev, T data, Node<T> next) {
        this.data = data;
        this.next = next;
        this.prev = prev;
    }

    public static Node<Task> getHead() {
        return head;
    }

    public static void setHead(Node<Task> head) {
        Node.head = head;
    }

    public static Node<Task> getTail() {
        return tail;
    }

    public static void setTail(Node<Task> tail) {
        Node.tail = tail;
    }
}

