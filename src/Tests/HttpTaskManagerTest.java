package Tests;

import Exceptions.HttpExceptions;
import Manager.HttpTaskManager;
import Manager.TaskManager;
import Model.*;
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

    HttpTaskManager httpTaskManager;
    HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
    HttpClient httpClient = HttpClient.newHttpClient();
    private KVServer kvServer;
    ;
    private HttpTaskServer server;

    @BeforeEach
    public void createTaskManager() throws IOException {
        resetIdCounter();
        kvServer = new KVServer();
        kvServer.start();
        server = new HttpTaskServer();
        server.startTasksServer();
        httpTaskManager = (HttpTaskManager) server.getManager();
    }

    @AfterEach
    public void closeKVServer() {
        kvServer.stop();
    }

    @DisplayName("запрос на создание обычной задачи")
    @Test
    public void requestForSimpleTask() {
        createTask(TaskType.TASK);
        Task task = server.getManager().getTaskById(1);
        Assertions.assertTrue(checkTask(task, "1", "1", 1, TaskStatus.NEW,
                        LocalDateTime.of(2023, Month.FEBRUARY, 28, 8, 22), 30),
                "Ошибка при обработке запроса на создание простой задачи.");
    }

    @DisplayName("запрос на создание пустого эпика")
    @Test
    public void requestForEpicTask() {
        createTask(TaskType.EPIC);
        Task task = server.getManager().getTaskById(1);
        Assertions.assertTrue(checkTask(task, "1", "1", 1, TaskStatus.NEW,
                        null, 0),
                "Ошибка при обработке запроса на создание пустого эпика.");
    }

    @DisplayName("запрос на создание новой подзадачи")
    @Test
    public void requestForSubTask() {
        createTask(TaskType.EPIC);
        createTask(TaskType.SUBTASK);
        Task task = httpTaskManager.getTasksForTests().get(2);
        Assertions.assertTrue(checkTask(task, "1", "1", 2, TaskStatus.NEW,
                        LocalDateTime.of(2023, Month.FEBRUARY, 28, 10, 22), 30),
                "Ошибка при обработке запроса на создание новой подзадачи.");
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

    private void createTask(TaskType type) {
        Gson gsonBuilder = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new DateAdapter())
                .create();
        Task task = null;
        URI uri = null;
        NewTask newTask;
        switch (type) {
            case TASK:
                uri = URI.create("http://localhost:8080/tasks/task/");
                newTask = new NewTask("3", "3",
                        LocalDateTime.of(2023, Month.FEBRUARY, 28, 8, 0), 30);
                task = new SimpleTask(newTask);
                break;
            case EPIC:
                uri = URI.create("http://localhost:8080/tasks/epic/");
                newTask = new NewTask("1", "1",
                        LocalDateTime.of(2023, Month.FEBRUARY, 28, 9, 0), 30);
                task = new EpicTask(newTask);
                break;
            case SUBTASK:
                uri = URI.create("http://localhost:8080/tasks/subtask/");
                newTask = new NewTask("2", "2",
                        LocalDateTime.of(2023, Month.FEBRUARY, 28, 10, 0), 30);
                task = new Subtask(newTask, 1);
                break;
        }
        HttpRequest.BodyPublisher publisher = HttpRequest.BodyPublishers.ofString(gsonBuilder.toJson(task));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(publisher)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, handler);
            System.out.println(response.statusCode());
        } catch (IOException | InterruptedException exception) {
            throw new HttpExceptions.ErrorInTestManager("Ошибка при отправке запроса на созданиие новой задачи.");
        }

    }
}
