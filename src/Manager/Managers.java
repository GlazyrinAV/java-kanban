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

    public static FileBackedTasksManager getWithAutosave() {
        return new FileBackedTasksManager(getDefaultHistory());
    }
}