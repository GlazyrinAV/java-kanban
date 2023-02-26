package Tests;

import Manager.FileBackedTasksManager;
import Manager.InMemoryHistoryManager;
import Manager.TaskManager;
import Model.NewTask;
import Model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileBackedTasksManagerTest extends TaskManagerTest<TaskManager> {

    private final Path testFile1 = Path.of("./ResourcesForTest/Test1.csv");
    private final Path dataFile = Path.of("./Resources/Data.csv");

    @BeforeEach
    public void createTaskManager() throws IOException {
        resetIdCounter();
        deleteDataForTest();
        setManager(new FileBackedTasksManager(new InMemoryHistoryManager()));
    }

    // Запись
    // 1. Запись обычная с историей
    @Test
    public void dataWriteBase() throws IOException {
        getTestManager().newSimpleTask(new NewTask("1", "1"));
        getTestManager().getTaskById(1);
        Assertions.assertTrue(twoFilesAreEqual(dataFile, testFile1));
    }

    // 2. Запись при пустом списке
    @Test
    public void dataWriteWithNoTasks() {
    }

    // 3. Запись эпика без подзадач
    @Test
    public void dataWriteWithEpicWithNoSubTasks() {
    }

    // 4. Запись без истории

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

    private boolean twoFilesAreEqual(Path path1, Path path2) throws IOException {
        final File file1 = path1.toFile();
        final File file2 = path2.toFile();
        try (BufferedReader reader1 = new BufferedReader(new FileReader(file1)); BufferedReader reader2 = new BufferedReader(new FileReader(file2))) {
            while (reader1.ready() || reader2.ready()) {
                if (reader1.read() != reader2.read()) return false;
            }
        }
        return true;
    }
}