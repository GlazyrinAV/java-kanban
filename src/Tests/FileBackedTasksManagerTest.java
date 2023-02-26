package Tests;

import Manager.FileBackedTasksManager;
import Manager.InMemoryHistoryManager;
import Manager.TaskManager;
import Model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileBackedTasksManagerTest extends TaskManagerTest<TaskManager>{
    @BeforeEach

    public void createTaskManager() {
        setManager(new FileBackedTasksManager(new InMemoryHistoryManager()));
    }

    @AfterEach
    public void resetIdCounter() throws IOException {
        deleteDataForTest();
        Task.resetCounterForTest();
    }

    private void deleteDataForTest() throws IOException {
        Path dataFile = Path.of("./Resources/Data.csv");
        Files.delete(dataFile);
    }

}