package Tests;

import Exceptions.HttpExceptions;
import Manager.HttpTaskManager;
import Manager.InMemoryHistoryManager;
import Manager.TaskManager;
import Model.Task;
import Server.KVServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;

public class HttpTaskManagerTest extends TaskManagerTest<TaskManager> {

    KVServer kvServer;

    @BeforeEach
    public void createTaskManager() throws IOException {
        resetIdCounter();
        kvServer = new KVServer();
        kvServer.start();
        String path = "http://localhost";
        setManager(new HttpTaskManager(new InMemoryHistoryManager(), path));
    }

    @AfterEach
    public void closeKVServer() {
        kvServer.stop();
    }

    @DisplayName("Запись обычная с историей")
    @Test
    public void dataWriteBase() {
        createSimpleTask();

    }

    @DisplayName("Запись при пустом списке")
    @Test
    public void dataWriteWithNoTasks() {

    }

    @DisplayName("Запись эпика без подзадач")
    @Test
    public void dataWriteWithEpicWithNoSubTasks() {

    }

    @DisplayName("Запись без истории")
    @Test
    public void dataWriteWithNoHistory() {
    }

    @DisplayName("Чтение пустого файла")
    @Test
    public void dataReadFromFileWithNoData() {
    }

    @DisplayName("Чтение файла с данными и историей")
    @Test
    public void dataReadFromFileWithDataAndHistory() {
    }

    @DisplayName("Чтение данных без истории")
    @Test
    public void dataReadFromFileWithDataAndNoHistory() {
    }

    @DisplayName("Чтение файла с эпиком без подзадач")
    @Test
    public void dataReadFromFileWithEpicWithNoSubTasks() {
    }

    @DisplayName("Чтение файла с эпиком без подзадач")
    @Test
    public void newTaskIdAfterReadingDataFromFile() {
    }

    @DisplayName("Время выполнения задач Запись в файл задач со временем")
    @Test
    public void dataWriteWithTimeData() {
    }

    @DisplayName("Время выполнения задач Запись в файл задач без времени")
    @Test
    public void dataWriteWithNoTimeData() {
    }

    @DisplayName("Время выполнения задач Чтение из файла задач со временем")
    @Test
    public void dataReadWithTimeData() {
    }

    @DisplayName("Время выполнения задач Чтение из файла задач без времени")
    @Test
    public void dataReadWithNoTimeData() {

    }

    private void resetIdCounter() {
        Task.resetCounterForTest();
    }

    private String createSimpleTask() {
        try {
            Path path = Path.of("./ResourcesForTest/Test7.csv");
            URI registerUri = URI.create("localhost:8080/tasks/task");
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest.BodyPublisher publisher = HttpRequest.BodyPublishers.ofFile(path);
            HttpRequest registerRequest = HttpRequest.newBuilder()
                    .uri(registerUri)
                    .POST(publisher)
                    .build();
            HttpResponse<String> response = httpClient.send(registerRequest, handler);
            return response.body();
        } catch (IOException | InterruptedException exception) {
            throw new HttpExceptions.ErrorInTestManager("Ошибка при отправке запроса на созданиие новой задачи.");
        }
    }
}