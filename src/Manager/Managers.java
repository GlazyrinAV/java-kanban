package Manager;

public final class Managers {

    public static InMemoryTaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory(10));
    }

    public static InMemoryHistoryManager getDefaultHistory(int historyLength) {
        return new InMemoryHistoryManager(historyLength);
    }
}