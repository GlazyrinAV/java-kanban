package Manager;
import Model.Task;
import java.util.ArrayDeque;


public class Managers {

    public static InMemoryTaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static ArrayDeque<Task> getDefaultHistory() {
        return HistoryManager.getDefaultHistory();
    }

    public static void addDefaultHistory(Task task) {
        HistoryManager.addDefaultHistory(task);
    }

}