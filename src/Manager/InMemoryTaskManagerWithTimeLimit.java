package Manager;

import Model.Task;
import Nodes.TimeLineNodes;

import java.time.LocalDateTime;
import java.util.HashMap;

public class InMemoryTaskManagerWithTimeLimit extends InMemoryTaskManager {

    HashMap<LocalDateTime, TimeLineNodes<LocalDateTime>> freeTime;
    HashMap<LocalDateTime, TimeLineNodes<LocalDateTime>> busyTime;
    int timeLimitInMinutes = 5;
    private TimeLineNodes<LocalDateTime> head;
    private TimeLineNodes<LocalDateTime> tail;

    public InMemoryTaskManagerWithTimeLimit(InMemoryHistoryManager history, int timeLimit) {
        super(history);
        freeTime = new HashMap<>();
        busyTime = new HashMap<>();
        head = null;
        tail = null;
        if (timeLimit > 0) {
            this.timeLimitInMinutes = timeLimit;
        }
    }

    private void addTaskToBeginning(Task task) {
        final TimeLineNodes<LocalDateTime> oldHead = head;
        final TimeLineNodes<LocalDateTime> newNode = new TimeLineNodes<>(task, null, oldHead);
        head = newNode;
        if (oldHead == null) {
            tail = newNode;
        } else {
            oldHead.setNextNode(newNode);
        }
        long duration = getNumberOfPeriodsInTask(task.getDuration());
        LocalDateTime currentPeriod = getStartOfPeriod(task.getStartTime());
        for (long i = 1; i <= duration; i++) {
            busyTime.put(currentPeriod, newNode);
            currentPeriod = currentPeriod.plusMinutes(timeLimitInMinutes);
        }
    }

    private void addTaskToEnd(Task task) {
        final TimeLineNodes<LocalDateTime> oldTail = tail;
        final TimeLineNodes<LocalDateTime> newNode = new TimeLineNodes<>(task, oldTail, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.setNextNode(newNode);
        }
        long duration = getNumberOfPeriodsInTask(task.getDuration());
        LocalDateTime currentPeriod = getStartOfPeriod(task.getStartTime());
        for (long i = 1; i <= duration; i++) {
            busyTime.put(currentPeriod, newNode);
            currentPeriod = currentPeriod.plusMinutes(timeLimitInMinutes);
        }
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

    private LocalDateTime getStartOfPeriod(LocalDateTime time) {
        if (time.getMinute() == 0) {
            return time.withSecond(0).withMinute(0);
        } else {
            int minutes = (time.getMinute() / timeLimitInMinutes) * timeLimitInMinutes;
            return time.withSecond(0).withMinute(minutes);
        }
    }

    private long getNumberOfPeriodsInTask(long duration) {
        if (duration % timeLimitInMinutes == 0){
            return duration / timeLimitInMinutes;
        } else {
            return duration / timeLimitInMinutes + 1;
        }
    }
}