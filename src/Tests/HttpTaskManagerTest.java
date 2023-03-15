package Tests;

import Exceptions.HttpExceptions;
import Manager.TaskManager;
import Model.NewTask;
import Model.Task;
import Model.TaskStatus;
import Server.HttpTaskServer;
import Server.KVServer;
import Utils.DateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.Month;

public class HttpTaskManagerTest extends TaskManagerTest<TaskManager> {

    private KVServer kvServer;

    HttpTaskServer server;

    @BeforeEach
    public void createTaskManager() throws IOException {
        resetIdCounter();
        kvServer = new KVServer();
        kvServer.start();
        server = new HttpTaskServer();
        server.startTasksServer();
    }

    @AfterEach
    public void closeKVServer() {
        kvServer.stop();
    }

    @DisplayName("Запись обычная с историей")
    @Test
    public void dataWriteBase() {
        createSimpleTask();
        Task task = server.getManager().getTaskById(1);
        Assertions.assertTrue(checkTask(task, "1", "1", 1, TaskStatus.NEW,
                        LocalDateTime.of(2023, Month.FEBRUARY, 28, 8, 22), 30),
                "Ошибка при обработке запроса на создание новой задачи.");
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

    private boolean checkTask(Task task, String title, String description, int id, TaskStatus status,
                              LocalDateTime starTime, int duration) {
        return (task.getTaskTitle().equals(title)) &&
                (task.getTaskDescription().equals(description)) &&
                (task.getTaskIdNumber() == id) &&
                (task.getTaskStatus().equals(status)) &&
                ((task.getStartTime() == null && starTime == null) || task.getStartTime().equals(starTime)) &&
                (task.getDuration() == duration);

    }

    private void createSimpleTask() {
        Gson gsonBuilder = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new DateAdapter())
                .create();
        NewTask task = new NewTask("1", "1",
                LocalDateTime.of(2023, Month.FEBRUARY, 28, 8, 22), 30);
        try {
            URI uri = URI.create("http://localhost:8080/tasks/task");
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest.BodyPublisher publisher = HttpRequest.BodyPublishers.ofString(gsonBuilder.toJson(task));
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(publisher)
                    .uri(uri)
                    .build();
            httpClient.send(request, handler);
        } catch (IOException | InterruptedException exception) {
            throw new HttpExceptions.ErrorInTestManager("Ошибка при отправке запроса на созданиие новой задачи.");
        }
    }
}