package Manager;

import Model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class InMemoryTaskManagerTest extends TaskManagerTest<TaskManager> {

    @BeforeEach
    public void createTaskManager() {
        setManager(new InMemoryTaskManager(new InMemoryHistoryManager()));
    }

    @AfterEach
    public void resetIdCounter() {
        Task.resetCounterForTest();
    }
}