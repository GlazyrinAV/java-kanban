package Utils;

import Model.Task;
import Nodes.TimeLineNodes;
import Nodes.TimeNodePlace;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

public class TimeLineChecker {

    HashMap<LocalDateTime, TimeLineNodes<LocalDateTime>> freeTime;
    HashMap<LocalDateTime, TimeLineNodes<LocalDateTime>> busyTime;
    int timeLimitInMinutes = 5;
    private TimeLineNodes<LocalDateTime> head;
    private TimeLineNodes<LocalDateTime> tail;

    public TimeLineChecker(int timeLimit) {
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
        if (task.getStartTime() != null && task.getEndTime() != null) {
            if (busyTime.isEmpty() && freeTime.isEmpty()) {
                addTimeNodeToBeginning(task);
                return true;
            } else if (head == null || (start.isBefore(head.getStart()))) {
                if (checkForOverlay(task)) {
                    addTimeNodeToBeginning(task);
                    return true;
                } else {
                    return false;
                }
            } else if (tail == null || (start.isAfter(tail.getData().getEndTime()))) {
                if (checkForOverlay(task)) {
                    addTimeNodeToEnd(task);
                    return true;
                } else {
                    return false;
                }
            } else if (freeTime.containsKey(start)) {
                if (checkForOverlay(task)) {
                    addTimeNodeToMiddle(task);
                    return true;
                } else {
                    return false;
                }
            } else {
                if (checkForOverlay(task)) {
                    addTimeNodeToBeginning(task);
                    return true;
                } else {
                    return false;
                }
            }
        } else return true;
    }

    private boolean checkForOverlay(Task task) {
        LocalDateTime start = getStartOfPeriod(task.getStartTime());
        LocalDateTime end = getStartOfPeriod(task.getEndTime());
        if (busyTime.containsKey(start) || busyTime.containsKey(end)) {
            return false;
        }
        if ((freeTime.containsKey(start) && !freeTime.containsKey(end)) ||
                ((!freeTime.containsKey(start)) && (freeTime.containsKey(end)))) {
            return false;
        }
        if (!busyTime.isEmpty() && !(freeTime.containsKey(start) || freeTime.containsKey(end))) {
            return start.isAfter(getStartOfPeriod(tail.getData().getEndTime())) || end.isBefore(getStartOfPeriod(head.getStart()));
        }
        return freeTime.get(start).getNextStart().isAfter(end);
    }

    private void addTimeNodeToBeginning(Task task) {
        long busyDuration = getNumberOfPeriodsInTask(task.getDuration());
        long freeDuration;
        if (head == null) {
            freeDuration = 0;
        } else {
            freeDuration = getNumberOfFreePeriods(task.getEndTime(), head.getStart());
        }
        LocalDateTime currentPeriod = getStartOfPeriod(task.getStartTime());
        if (freeDuration <= 0) {
            for (long i = 1; i <= busyDuration; i++) {
                addNewTimeNode(TimeNodePlace.BEGINNING, task, currentPeriod, busyTime);
                currentPeriod = currentPeriod.minusMinutes(timeLimitInMinutes);
            }
        } else {
            currentPeriod = getStartOfPeriod(head.getStart().minusMinutes(timeLimitInMinutes));
            LocalDateTime nextStart = getStartOfPeriod(head.getStart());
            LocalDateTime prevEnd = getStartOfPeriod(task.getEndTime());
            for (long i = 1; i <= freeDuration; i++) {
                addNewTimeNode(TimeNodePlace.BEGINNING, task, currentPeriod, freeTime);
                freeTime.get(currentPeriod).setNextStart(nextStart);
                freeTime.get(currentPeriod).setPrevEnd(prevEnd);
                currentPeriod = currentPeriod.minusMinutes(timeLimitInMinutes);
            }
            for (long i = 1; i <= busyDuration; i++) {
                addNewTimeNode(TimeNodePlace.BEGINNING, task, currentPeriod, busyTime);
                currentPeriod = currentPeriod.minusMinutes(timeLimitInMinutes);
            }
        }
    }

    private void addTimeNodeToEnd(Task task) {
        long busyDuration = getNumberOfPeriodsInTask(task.getDuration());
        long freeDuration;
        if (tail == null) {
            freeDuration = 0;
        } else {
            freeDuration = getNumberOfFreePeriods(tail.getData().getEndTime(), task.getStartTime());
        }
        LocalDateTime currentPeriod = getStartOfPeriod(task.getStartTime());
        if (freeDuration <= 0) {
            for (long i = 1; i <= busyDuration; i++) {
                addNewTimeNode(TimeNodePlace.END, task, currentPeriod, busyTime);
                currentPeriod = currentPeriod.plusMinutes(timeLimitInMinutes);
            }
        } else {
            currentPeriod = getStartOfPeriod(tail.getData().getEndTime()).plusMinutes(timeLimitInMinutes);
            LocalDateTime nextStart = getStartOfPeriod(task.getStartTime());
            LocalDateTime prevEnd = getStartOfPeriod(tail.getData().getEndTime());
            for (long i = 1; i <= freeDuration; i++) {
                addNewTimeNode(TimeNodePlace.END, task, currentPeriod, freeTime);
                freeTime.get(currentPeriod).setNextStart(nextStart);
                freeTime.get(currentPeriod).setPrevEnd(prevEnd);
                currentPeriod = currentPeriod.plusMinutes(timeLimitInMinutes);
            }
            for (long i = 1; i <= busyDuration; i++) {
                addNewTimeNode(TimeNodePlace.END, task, currentPeriod, busyTime);
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
            currentPeriod = getStartOfPeriod(freeTime.get(currentPeriod).getPrevEnd().plusMinutes(timeLimitInMinutes));
            for (long i = 1; i <= freeDurationBefore; i++) {
                freeTime.get(currentPeriod).setNextStart(getStartOfPeriod(task.getStartTime()));
                currentPeriod = currentPeriod.plusMinutes(timeLimitInMinutes);
            }
        }
        if (freeDurationAfter > 0) {
            currentPeriod = getStartOfPeriod(task.getEndTime().plusMinutes(timeLimitInMinutes));
            for (long i = 1; i <= freeDurationAfter; i++) {
                freeTime.get(currentPeriod).setPrevEnd(getStartOfPeriod(task.getEndTime()));
                currentPeriod = currentPeriod.plusMinutes(timeLimitInMinutes);
            }
        }
        for (long i = 1; i <= busyDuration; i++) {
            addNewTimeNode(TimeNodePlace.MIDDLE, task, currentPeriod, busyTime);
            busyTime.put(currentPeriod, freeTime.remove(currentPeriod));
            currentPeriod = currentPeriod.plusMinutes(timeLimitInMinutes);
        }
    }

    private void addNewTimeNode(TimeNodePlace timeNodePlace, Task task, LocalDateTime period, HashMap<LocalDateTime, TimeLineNodes<LocalDateTime>> time) {
        final TimeLineNodes<LocalDateTime> newNode;
        switch (timeNodePlace) {
            case BEGINNING:
                final TimeLineNodes<LocalDateTime> oldHead = head;
                newNode = new TimeLineNodes<>(task, oldHead, null);
                head = newNode;
                if (oldHead == null) {
                    tail = newNode;
                } else {
                    oldHead.setPrevNode(newNode);
                }
                time.put(period, newNode);
                break;
            case MIDDLE:
                freeTime.get(getStartOfPeriod(task.getStartTime())).setData(task);
                break;
            case END:
                final TimeLineNodes<LocalDateTime> oldTail = tail;
                newNode = new TimeLineNodes<>(task, null, oldTail);
                tail = newNode;
                if (oldTail == null) {
                    head = newNode;
                } else {
                    oldTail.setNextNode(newNode);
                }
                time.put(period, newNode);
                break;
        }
    }

    private void removeExcitingNode(TimeLineNodes<LocalDateTime> node) {
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

    public void removeTimeNode(Task task) {
        if (task.getStartTime() != null) {
            LocalDateTime time = getStartOfPeriod(task.getStartTime());
            long duration = getNumberOfPeriodsInTask(task.getDuration());
            LocalDateTime currentPeriod = time;
            LocalDateTime nextTimePeriod = getStartOfPeriod(time.plusMinutes(duration).plusMinutes(timeLimitInMinutes));
            LocalDateTime previousTimePeriod = getStartOfPeriod(time.minusMinutes(timeLimitInMinutes));
            for (long i = 1; i <= duration; i++) {
                LocalDateTime nextStart = null;
                LocalDateTime previousEnd = null;
                if (freeTime.get(nextTimePeriod) != null) {
                    nextStart = freeTime.get(nextTimePeriod).getNextStart();
                }
                if (freeTime.get(previousTimePeriod) != null) {
                    previousEnd = freeTime.get(previousTimePeriod).getPrevEnd();
                }
                busyTime.get(currentPeriod).setPrevEnd(previousEnd);
                busyTime.get(currentPeriod).setNextStart(nextStart);
                freeTime.put(currentPeriod, busyTime.remove(currentPeriod));
                currentPeriod = currentPeriod.plusMinutes(timeLimitInMinutes);
            }
            if (freeTime.containsKey(nextTimePeriod) &&
                    (!freeTime.containsKey(previousTimePeriod) || busyTime.containsKey(previousTimePeriod))) {
                currentPeriod = nextTimePeriod;
                duration = getNumberOfFreePeriods(currentPeriod, freeTime.get(currentPeriod).getNextStart());
                for (long i = 1; i <= duration; i++) {
                    removeExcitingNode(freeTime.get(currentPeriod));
                    freeTime.remove(currentPeriod);
                    currentPeriod = currentPeriod.plusMinutes(timeLimitInMinutes);
                }
            } else if (freeTime.containsKey(previousTimePeriod) &&
                    (!freeTime.containsKey(nextTimePeriod) || busyTime.containsKey(nextTimePeriod))) {
                currentPeriod = previousTimePeriod;
                duration = getNumberOfFreePeriods(freeTime.get(currentPeriod).getPrevEnd(), currentPeriod);
                for (long i = 1; i <= duration; i++) {
                    removeExcitingNode(freeTime.get(currentPeriod));
                    freeTime.remove(currentPeriod);
                    currentPeriod = currentPeriod.minusMinutes(timeLimitInMinutes);
                }
            } else if (freeTime.containsKey(previousTimePeriod) && freeTime.containsKey(nextTimePeriod)) {
                LocalDateTime nextStart = freeTime.get(nextTimePeriod).getNextStart();
                LocalDateTime previousEnd = freeTime.get(previousTimePeriod).getPrevEnd();
                duration = getNumberOfFreePeriods(previousEnd, previousTimePeriod);
                currentPeriod = previousTimePeriod;
                for (long i = 1; i <= duration; i++) {
                    freeTime.get(currentPeriod).setNextStart(nextStart);
                    currentPeriod = currentPeriod.minusMinutes(timeLimitInMinutes);
                }
                duration = getNumberOfFreePeriods(nextTimePeriod, nextStart);
                currentPeriod = nextTimePeriod;
                for (long i = 1; i <= duration; i++) {
                    freeTime.get(currentPeriod).setPrevEnd(previousEnd);
                    currentPeriod = currentPeriod.plusMinutes(timeLimitInMinutes);
                }
            }
        }
    }

    private LocalDateTime getStartOfPeriod(LocalDateTime time) {
        if (time.getMinute() == 0) {
            return time.withMinute(0).withSecond(0);
        } else {
            int minutes = (time.getMinute() / timeLimitInMinutes) * timeLimitInMinutes;
            return time.withMinute(minutes).withSecond(0).withNano(0);
        }
    }

    private long getNumberOfPeriodsInTask(long duration) {
        if (duration % timeLimitInMinutes == 0) {
            return duration / timeLimitInMinutes;
        } else {
            return (duration / timeLimitInMinutes) + 1;
        }
    }

    private long getNumberOfFreePeriods(LocalDateTime endOfPrevious, LocalDateTime starOfNew) {
        return getNumberOfPeriodsInTask(Duration.between(getStartOfPeriod(endOfPrevious).plusMinutes(timeLimitInMinutes), getStartOfPeriod(starOfNew)).toMinutes());
    }
}