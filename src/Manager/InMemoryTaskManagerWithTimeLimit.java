package Manager;

import Model.Task;
import Nodes.TimeLineNodes;

import java.time.LocalDateTime;
import java.util.HashMap;

public class InMemoryTaskManagerWithTimeLimit extends InMemoryTaskManager {

    HashMap<LocalDateTime, Task> freeTime;
    HashMap<LocalDateTime, Task> busyTime;
    private TimeLineNodes<LocalDateTime> head;
    private TimeLineNodes<LocalDateTime> tail;

    /**
     * Конструктор менеджера задач, в который необходимо передавать объект менеджер историй просмотра
     *
     * @param history - объект класса менеджер историй просмотра
     */
    public InMemoryTaskManagerWithTimeLimit(InMemoryHistoryManager history) {
        super(history);
        freeTime = new HashMap<>();
        busyTime = new HashMap<>();
        head = null;
        tail = null;
    }

    @Override
    public void addTaskToPrioritizedTasks(Task task) {

    }

    private void addTaskToBeginning(Task task) {
        final TimeLineNodes<LocalDateTime> oldTail = tail;
        final TimeLineNodes<LocalDateTime> newNode = new TimeLineNodes<>(task, null, oldTail, null, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.setNextNode(newNode);
        }

    }

    private void addTaskToEnd(Task task) {

    }

    private void addTaskToMiddle(Task task) {

    }
}