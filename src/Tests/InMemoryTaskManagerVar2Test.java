package Tests;

import Manager.InMemoryHistoryManager;
import Manager.InMemoryTaskManagerVar2;
import Manager.TaskManager;
import Model.Task;
import org.junit.jupiter.api.BeforeEach;

public class InMemoryTaskManagerVar2Test extends TaskManagerTest<TaskManager> {

    @BeforeEach
    public void createTaskManager() {
        resetIdCounter();
        setManager(new InMemoryTaskManagerVar2(new InMemoryHistoryManager()));
    }

    private void resetIdCounter() {
        Task.resetCounterForTest();
    }
}