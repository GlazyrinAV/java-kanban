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

    public static InMemoryTaskManagerVar2 getDefaultVer2() {
        return new InMemoryTaskManagerVar2(getDefaultHistory());
    }
}