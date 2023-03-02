package Nodes;

import Model.Task;

import java.time.LocalDateTime;

public class TimeLineNodes<T extends LocalDateTime> {

    private Task data;
    private TimeLineNodes<T> nextNode;
    private TimeLineNodes<T> prevNode;
    private LocalDateTime nextStart;
    private LocalDateTime prevEnd;
    private LocalDateTime start;

    public TimeLineNodes(Task data, TimeLineNodes<T> nextNode, TimeLineNodes<T> prevNode) {
        this.data = data;
        this.nextNode = nextNode;
        this.prevNode = prevNode;
        this.nextStart = null;
        this.prevEnd = null;
        this.start = data.getStartTime();
    }

    public Task getData() {
        return data;
    }

    public TimeLineNodes<T> getNextNode() {
        return nextNode;
    }

    public void setNextNode(TimeLineNodes<T> nextNode) {
        this.nextNode = nextNode;
    }

    public TimeLineNodes<T> getPrevNode() {
        return prevNode;
    }

    public void setPrevNode(TimeLineNodes<T> prevNode) {
        this.prevNode = prevNode;
    }

    public LocalDateTime getNextStart() {
        return nextStart;
    }

    public void setNextStart(LocalDateTime nextStart) {
        this.nextStart = nextStart;
    }

    public LocalDateTime getPrevEnd() {
        return prevEnd;
    }

    public void setPrevEnd(LocalDateTime prevEnd) {
        this.prevEnd = prevEnd;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setData(Task task) {
        data = task;
    }
}