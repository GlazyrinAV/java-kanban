package Tests;

import Manager.InMemoryHistoryManager;
import Manager.InMemoryTaskManager;
import Manager.TaskManager;
import Model.Task;
import org.junit.jupiter.api.BeforeEach;

public class InMemoryTaskManagerVar2Test extends TaskManagerTest<TaskManager> {

    @BeforeEach
    public void createTaskManager() {
        resetIdCounter();
        setManager(new InMemoryTaskManager(new InMemoryHistoryManager()));
    }

    private void resetIdCounter() {
        Task.resetCounterForTest();
    }
}