package Manager;
import Model.Task;

import java.util.ArrayDeque;
import java.util.List;

public class Managers {

    public static InMemoryTaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static List<Task> getDefaultHistory() {
       return HistoryManager.getDefaultHistory();
    }
}
