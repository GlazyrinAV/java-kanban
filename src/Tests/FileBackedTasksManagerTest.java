package Tests;

import Manager.FileBackedTasksManager;
import Manager.InMemoryHistoryManager;
import Manager.TaskManager;
import Model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileBackedTasksManagerTest extends TaskManagerTest<TaskManager> {
    @BeforeEach

    public void createTaskManager() throws IOException {
        resetIdCounter();
        deleteDataForTest();
        setManager(new FileBackedTasksManager(new InMemoryHistoryManager()));
    }

    // Запись
    // 1. Запись обычная
    @Test
    public void dataWriteBase() {

    }
    // 2. Запись при пустом списке
    @Test
    public void dataWriteWithNoTasks() {

    }
    // 3. Запись эпика без подзадач
    @Test
    public void dataWriteWithEpicWithNoSubTasks() {

    }

    // Чтение
    // 1. Чтение пустого файла
    @Test
    public void dataReadFromFileWithNoData() {

    }
    // 2. Чтение файла с данными
    @Test
    public void dataReadFromFileWithData() {

    }
    // 3. Чтение файла с эпиком без подзадач
    @Test
    public void dataReadFromFileWithEpicWithNoSubTasks() {

    }
    // 4. Отсутствие эпика для подзадачи
    @Test
    public void dataReadFromFileWithNoEpicForSubTaskException() {

    }

    private void resetIdCounter() {
        Task.resetCounterForTest();
    }

    private void deleteDataForTest() throws IOException {
        Path dataFile = Path.of("./Resources/Data.csv");
        if (Files.exists(dataFile)) {
            Files.delete(dataFile);
        }
    }

}