package Nodes;

import Model.Task;

import java.time.LocalDateTime;

public class TimeLineNodes<T extends LocalDateTime> {

    private final Task data;
    private TimeLineNodes<T> nextNode;
    private TimeLineNodes<T> prevNode;
    private TimeLineNodes<T> nextStart;
    private TimeLineNodes<T> prevEnd;

    public TimeLineNodes(Task data, TimeLineNodes<T> nextNode, TimeLineNodes<T> prevNode,
                         TimeLineNodes<T> nextStart, TimeLineNodes<T> prevEnd) {
        this.data = data;
        this.nextNode = nextNode;
        this.prevNode = prevNode;
        this.nextStart = nextStart;
        this.prevEnd = prevEnd;
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

    public TimeLineNodes<T> getNextStart() {
        return nextStart;
    }

    public void setNextStart(TimeLineNodes<T> nextStart) {
        this.nextStart = nextStart;
    }

    public TimeLineNodes<T> getPrevEnd() {
        return prevEnd;
    }

    public void setPrevEnd(TimeLineNodes<T> prevEnd) {
        this.prevEnd = prevEnd;
    }
}