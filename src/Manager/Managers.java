package Manager;

import java.io.IOException;

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

    public static InMemoryTaskManagerWithTimePeriods getDefaultWithTimePeriods() {
        return new InMemoryTaskManagerWithTimePeriods(getDefaultHistory());
    }

    public static HttpTaskManager getWithHttpManager() throws IOException, InterruptedException {
        return new HttpTaskManager(getDefaultHistory(), "http://localhost");
    }
}