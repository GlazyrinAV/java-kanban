package Manager;

import Model.Task;
import Nodes.TimeLineNodes;

import java.time.LocalDateTime;
import java.util.HashMap;

public class InMemoryTaskManagerWithTimeLimit extends InMemoryTaskManager {

    HashMap<LocalDateTime, TimeLineNodes<LocalDateTime>> freeTime;
    HashMap<LocalDateTime, TimeLineNodes<LocalDateTime>> busyTime;

    int timeLimit = 5;
    private TimeLineNodes<LocalDateTime> head;
    private TimeLineNodes<LocalDateTime> tail;

    /**
     * Конструктор менеджера задач, в который необходимо передавать объект менеджер историй просмотра
     * @param history - объект класса менеджер историй просмотра
     */
    public InMemoryTaskManagerWithTimeLimit(InMemoryHistoryManager history) {
        super(history);
        freeTime = new HashMap<>();
        busyTime = new HashMap<>();
        head = null;
        tail = null;
    }

    public InMemoryTaskManagerWithTimeLimit(InMemoryHistoryManager history, int timeLimit) {
        super(history);
        freeTime = new HashMap<>();
        busyTime = new HashMap<>();
        head = null;
        tail = null;
        this.timeLimit = timeLimit;
    }
    @Override
    public void addTaskToPrioritizedTasks(Task task) {
        final TimeLineNodes<LocalDateTime> oldHead = head;
        final TimeLineNodes<LocalDateTime> newNode = new TimeLineNodes<>(task, null, oldHead, null, null);
        head = newNode;
        if (oldHead == null) {
            tail = newNode;
        } else {
            oldHead.setNextNode(newNode);
        }
//
    }

    private void addTaskToBeginning(LocalDateTime time) {

    }

    private void addTaskToEnd(Task task) {
        final TimeLineNodes<LocalDateTime> oldTail = tail;
        final TimeLineNodes<LocalDateTime> newNode = new TimeLineNodes<>(task, null, oldTail, null, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.setNextNode(newNode);
        }
//
    }

    private void addTaskToMiddle(Task task) {

    }

    private void removeLinksToNode(LocalDateTime time) {
        TimeLineNodes<LocalDateTime> node = freeTime.get(time);
        if (node.equals(head)) {
            if (node.getNextNode() == null) {
                head = null;
                tail = null;
            } else {
                node.getNextNode().setPrevNode(null);
                head = node.getNextNode();
            }
        } else if (node.equals(tail)) {
            node.getPrevNode().setNextNode(null);
            tail = node.getPrevNode();
        } else {
            node.getNextNode().setPrevNode(node.getPrevNode());
            node.getPrevNode().setNextNode(node.getNextNode());
        }
    }
}