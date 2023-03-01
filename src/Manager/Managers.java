package Manager;

public final class Managers {
    private Managers() {
    }

    public static InMemoryTaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static InMemoryHistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager getWithAutoSave() {
        return new FileBackedTasksManager(getDefaultHistory());
    }

    public static InMemoryTaskManagerWithTimeLimit getDefaultWithTimeLimit() {
        return new InMemoryTaskManagerWithTimeLimit(new InMemoryHistoryManager(), 10);
    }
}