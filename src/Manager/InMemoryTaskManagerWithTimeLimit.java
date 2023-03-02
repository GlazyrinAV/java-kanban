package Manager;

import Model.Task;
import Nodes.TimeLineNodes;
import Nodes.TimeNodePlace;

import java.time.Duration;
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

    public boolean checkTask(Task task) {
        LocalDateTime start = getStartOfPeriod(task.getStartTime());
        LocalDateTime end = getStartOfPeriod(task.getEndTime());
        if (busyTime.isEmpty() && freeTime.isEmpty()) {
            addTimeNodeToBeginning(task);
            return true;
        } else if (freeTime.containsKey(start)) {
            if (checkForOverlay(task)) {
                addTimeNodeToMiddle(task);
                return true;
            } else {
                return false;
            }
        } else if (busyTime.containsKey(start) && start.isBefore(busyTime.get(0).getStart())) {
            if (checkForOverlay(task)) {
                addTimeNodeToBeginning(task);
                return true;
            } else {
                return false;
            }
        } else if (busyTime.containsKey(start) && end.isAfter(busyTime.get(busyTime.size() - 1).getData().getEndTime())) {
            if (checkForOverlay(task)) {
                addTimeNodeToEnd(task);
                return true;
            } else {
                return false;
            }
        } else {
            if (checkForOverlay(task)) {
                addTimeNodeToMiddle(task);
                return true;
            } else {
                return false;
            }
        }
    }

    private boolean checkForOverlay(Task task) {
        LocalDateTime start = getStartOfPeriod(task.getStartTime());
        LocalDateTime end = getStartOfPeriod(task.getEndTime());
        boolean noTaskInTimeLine = (!(busyTime.containsKey(start) && busyTime.containsKey(end)) ||
                !(freeTime.containsKey(start) && freeTime.containsKey(end)));
        if (busyTime.containsKey(start) || busyTime.containsKey(end)) {
            return false;
        }
        if ((freeTime.containsKey(start) && !freeTime.containsKey(end)) ||
                ((!freeTime.containsKey(start)) && (freeTime.containsKey(end)))) {
            return false;
        }
        if (noTaskInTimeLine) {
            return true;
        }
        return freeTime.get(start).getNextStart().isAfter(end);
    }

    private void addTimeNodeToBeginning(Task task) {
        long busyDuration = getNumberOfPeriodsInTask(task.getDuration());
        long freeDuration = getNumberOfFreePeriods(task.getEndTime(), head.getStart());
        LocalDateTime currentPeriod = getStartOfPeriod(task.getStartTime());
        if (freeDuration <= 0) {
            for (long i = 1; i <= busyDuration; i++) {
                addTimeNode(TimeNodePlace.BEGINNING, task, currentPeriod, busyTime);
                currentPeriod = currentPeriod.minusMinutes(timeLimitInMinutes);
            }
        } else {
            currentPeriod = getStartOfPeriod(head.getStart());
            LocalDateTime nextStart = getStartOfPeriod(head.getStart());
            LocalDateTime prevEnd = getStartOfPeriod(task.getEndTime());
            for (long i = 1; i <= freeDuration; i++) {
                addTimeNode(TimeNodePlace.BEGINNING, task, currentPeriod, freeTime);
                freeTime.get(currentPeriod).setNextStart(nextStart);
                freeTime.get(currentPeriod).setPrevEnd(prevEnd);
                currentPeriod = currentPeriod.minusMinutes(timeLimitInMinutes);
            }
            for (long i = 1; i <= busyDuration; i++) {
                addTimeNode(TimeNodePlace.BEGINNING, task, currentPeriod, busyTime);
                currentPeriod = currentPeriod.minusMinutes(timeLimitInMinutes);
            }
        }
    }

    private void addTimeNodeToEnd(Task task) {
        long busyDuration = getNumberOfPeriodsInTask(task.getDuration());
        long freeDuration = getNumberOfFreePeriods(tail.getData().getEndTime(), task.getStartTime());
        LocalDateTime currentPeriod = getStartOfPeriod(task.getStartTime());
        if (freeDuration <= 0) {
            for (long i = 1; i <= busyDuration; i++) {
                addTimeNode(TimeNodePlace.END, task, currentPeriod, busyTime);
                currentPeriod = currentPeriod.plusMinutes(timeLimitInMinutes);
            }
        } else {
            currentPeriod = getStartOfPeriod(tail.getStart());
            LocalDateTime nextStart = getStartOfPeriod(task.getStartTime());
            LocalDateTime prevEnd = getStartOfPeriod(tail.getData().getEndTime());
            for (long i = 0; i <= freeDuration; i++) {
                addTimeNode(TimeNodePlace.END, task, currentPeriod, freeTime);
                freeTime.get(currentPeriod).setNextStart(nextStart);
                freeTime.get(currentPeriod).setPrevEnd(prevEnd);
                currentPeriod = currentPeriod.plusMinutes(timeLimitInMinutes);
            }
            for (long i = 0; i <= busyDuration; i++) {
                addTimeNode(TimeNodePlace.END, task, currentPeriod, busyTime);
                currentPeriod = currentPeriod.plusMinutes(timeLimitInMinutes);
            }
        }
    }

    private void addTimeNodeToMiddle(Task task) {
        long busyDuration = getNumberOfPeriodsInTask(task.getDuration());
        LocalDateTime currentPeriod = getStartOfPeriod(task.getStartTime());
        long freeDurationBefore = getNumberOfFreePeriods(freeTime.get(currentPeriod).getPrevEnd(), task.getStartTime());
        long freeDurationAfter = getNumberOfFreePeriods(task.getEndTime(), freeTime.get(currentPeriod).getNextStart());
        if (freeDurationBefore > 0) {
            currentPeriod = getStartOfPeriod(freeTime.get(currentPeriod).getPrevEnd());
            for (long i = 1; i <= freeDurationBefore; i++) {
                freeTime.get(currentPeriod).setNextStart(getStartOfPeriod(task.getStartTime()));
                currentPeriod = currentPeriod.plusMinutes(timeLimitInMinutes);
            }
        }
        if (freeDurationAfter > 0) {
            currentPeriod = getStartOfPeriod(task.getEndTime());
            for (long i = 1; i <= freeDurationAfter; i++) {
                freeTime.get(currentPeriod).setPrevEnd(getStartOfPeriod(task.getEndTime()));
                currentPeriod = currentPeriod.plusMinutes(timeLimitInMinutes);
            }
        }
        for (long i = 1; i <= busyDuration; i++) {
            addTimeNode(TimeNodePlace.MIDDLE, task, currentPeriod, busyTime);
            busyTime.put(currentPeriod, freeTime.remove(currentPeriod));
            currentPeriod = currentPeriod.plusMinutes(timeLimitInMinutes);
        }
    }

    private void addTimeNode(TimeNodePlace timeNodePlace, Task task, LocalDateTime period, HashMap<LocalDateTime, TimeLineNodes<LocalDateTime>> time) {
        final TimeLineNodes<LocalDateTime> newNode;
        switch (timeNodePlace) {
            case BEGINNING:
                final TimeLineNodes<LocalDateTime> oldHead = head;
                newNode = new TimeLineNodes<>(task, null, oldHead);
                head = newNode;
                if (oldHead == null) {
                    tail = newNode;
                } else {
                    oldHead.setNextNode(newNode);
                }
                time.put(period, newNode);

                break;
            case MIDDLE:
                Task currentTask = freeTime.getOrDefault(timeNodePlace, null).getData();
                currentTask = task;
                break;
            case END:
                final TimeLineNodes<LocalDateTime> oldTail = tail;
                newNode = new TimeLineNodes<>(task, oldTail, null);
                tail = newNode;
                if (oldTail == null) {
                    head = newNode;
                } else {
                    oldTail.setNextNode(newNode);
                }
                break;
        }
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
        if (duration % timeLimitInMinutes == 0) {
            return duration / timeLimitInMinutes;
        } else {
            return duration / timeLimitInMinutes + 1;
        }
    }

    private long getNumberOfFreePeriods(LocalDateTime endOfPrevious, LocalDateTime starOfNew) {
        return getNumberOfPeriodsInTask(Duration.between(endOfPrevious, starOfNew).toMinutes());
    }
}