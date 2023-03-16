package Tests;

import Exceptions.HttpExceptions;
import Manager.HttpTaskManager;
import Manager.TaskManager;
import Model.NewTask;
import Model.Task;
import Model.TaskStatus;
import Model.TaskType;
import Server.HttpTaskServer;
import Server.KVServer;
import Utils.DateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskManagerTest extends TaskManagerTest<TaskManager> {

    private final HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new DateAdapter())
            .create();
    private final Gson gson2 = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new DateAdapter())
            .create();
    private HttpTaskManager httpTaskManager;
    private KVServer kvServer;
    private HttpTaskServer server;


    @BeforeEach
    public void createTaskManager() throws IOException {
        resetIdCounter();
        kvServer = new KVServer();
        kvServer.start();
        server = new HttpTaskServer();
        server.startTasksServer();
        setManager(server.getManager());
        httpTaskManager = (HttpTaskManager) server.getManager();
    }

    @AfterEach
    public void closeKVServer() {
        kvServer.stop();
        server.stopTaskServer();
    }

    @DisplayName("Запрос на создание обычной задачи")
    @Test
    public void requestForSimpleTask() {
        createTask(TaskType.TASK);
        Task task = httpTaskManager.getTasksForTests().get(1);
        Assertions.assertTrue(checkTask(task, "3", "3", 1, TaskStatus.NEW,
                        LocalDateTime.of(2023, Month.FEBRUARY, 28, 8, 0), 30),
                "Ошибка при обработке запроса на создание простой задачи.");
    }

    @DisplayName("Запрос на создание пустого эпика")
    @Test
    public void requestForEpicTask() {
        createTask(TaskType.EPIC);
        Task task = httpTaskManager.getTasksForTests().get(1);
        Assertions.assertTrue(checkTask(task, "1", "1", 1, TaskStatus.NEW,
                        null, 0),
                "Ошибка при обработке запроса на создание пустого эпика.");
    }

    @DisplayName("Запрос на создание новой подзадачи")
    @Test
    public void requestForSubTask() {
        createTask(TaskType.EPIC);
        createTask(TaskType.SUBTASK);
        Task task = httpTaskManager.getTasksForTests().get(2);
        Assertions.assertTrue(checkTask(task, "2", "2", 2, TaskStatus.NEW,
                        LocalDateTime.of(2023, Month.FEBRUARY, 28, 10, 0), 30),
                "Ошибка при обработке запроса на создание новой подзадачи.");
    }

    @DisplayName("Запрос на обновление задачи")
    @Test
    public void requestUpdateTask() {
        createTask(TaskType.TASK);
        URI uri = URI.create("http://localhost:8080/tasks/task/");
        String body = "{\"taskId\":1,\"taskStatus\":\"DONE\"}";
        HttpRequest.BodyPublisher publisher = HttpRequest.BodyPublishers.ofString(body);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(publisher)
                .build();
        try {
            httpClient.send(request, handler);
        } catch (IOException | InterruptedException exception) {
            throw new HttpExceptions.ErrorInTestManager
                    ("Ошибка при отправке запроса на обновление задачи.");
        }
        Assertions.assertEquals(httpTaskManager.getTasksForTests().get(1).getTaskStatus(), TaskStatus.DONE,
                "Ошибка при обновлении статуса задачи.");
    }

    @DisplayName("Запрос на обновление эпика")
    @Test
    public void requestUpdateEpic() {
        createTask(TaskType.EPIC);
        createTask(TaskType.SUBTASK);
        URI uri = URI.create("http://localhost:8080/tasks/task/");
        String body = "{\"taskId\":2,\"taskStatus\":\"DONE\"}";
        HttpRequest.BodyPublisher publisher = HttpRequest.BodyPublishers.ofString(body);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(publisher)
                .build();
        try {
            httpClient.send(request, handler);
        } catch (IOException | InterruptedException exception) {
            throw new HttpExceptions.ErrorInTestManager
                    ("Ошибка при отправке запроса на обновление подзадачи.");
        }
        Assertions.assertEquals(httpTaskManager.getTasksForTests().get(1).getTaskStatus(), TaskStatus.DONE,
                "Ошибка при обновлении статуса эпика.");
    }

    @DisplayName("Запрос всех задач")
    @Test
    public void requestGetAllTasks() {
        createTask(TaskType.TASK);
        createTaskWithoutTime();
        String data1 = gson2.toJson(httpTaskManager.getAllTasks());
        URI uri = URI.create("http://localhost:8080/tasks/tasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        String data2;
        try {
            HttpResponse<String> response = httpClient.send(request, handler);
            data2 = response.body();
        } catch (IOException | InterruptedException exception) {
            throw new HttpExceptions.ErrorInTestManager
                    ("Ошибка при отправке запроса на получение всех задач.");
        }
        Assertions.assertEquals(data1, data2,
                "Ошибка при получении всех задач.");
    }

    @DisplayName("Запрос удаление всех задач")
    @Test
    public void requestClearAllTasks() {
        createTask(TaskType.TASK);
        createTaskWithoutTime();
        URI uri = URI.create("http://localhost:8080/tasks/tasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        try {
            httpClient.send(request, handler);
        } catch (IOException | InterruptedException exception) {
            throw new HttpExceptions.ErrorInTestManager
                    ("Ошибка при отправке запроса на удаление всех задач.");
        }
        Assertions.assertTrue(httpTaskManager.getTasksForTests().isEmpty(),
                "Ошибка при удалении всех задач.");
    }

    @DisplayName("Запрос на удаление задачи по номеру")
    @Test
    public void requestRemoveTaskById() {
        createTask(TaskType.TASK);
        createTaskWithoutTime();
        URI uri = URI.create("http://localhost:8080/tasks/task?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        try {
            httpClient.send(request, handler);
        } catch (IOException | InterruptedException exception) {
            throw new HttpExceptions.ErrorInTestManager
                    ("Ошибка при отправке запроса на удаление задачи по номеру.");
        }
        String data1 = "{\"2\":{\"taskType\":\"TASK\",\"taskTitle\":\"4\",\"taskDescription\":\"4\",\"taskIdNumber\":2,\"taskStatus\":\"NEW\",\"startTime\":null,\"duration\":0}}";
        String data2 = gson2.toJson(httpTaskManager.getAllTasks());
        Assertions.assertEquals(data1, data2,
                "Ошибка при удалении задачи по номеру.");
    }

    @DisplayName("Запрос на получение приоритета")
    @Test
    public void requestGetPriority() {
        createTask(TaskType.EPIC);
        createTask(TaskType.SUBTASK);
        createTask(TaskType.TASK);
        createTaskWithoutTime();
        URI uri = URI.create("http://localhost:8080/tasks/priority");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        try {
            httpClient.send(request, handler);
        } catch (IOException | InterruptedException exception) {
            throw new HttpExceptions.ErrorInTestManager
                    ("Ошибка при отправке запроса на получение приоритетов.");
        }
        String data1 = "[3,2,4]";
        String data2 = gson2.toJson(httpTaskManager.getPrioritizedTasks());
        Assertions.assertEquals(data1, data2,
                "Ошибка при получении приоритетов.");
    }

    @DisplayName("Запись при пустом списке")
    @Test
    public void dataWriteWithNoTasks() throws IOException {
        restartTaskServer();
        Assertions.assertTrue(httpTaskManager.getAllTasks().isEmpty(), "Ошибка при сохранении пустого списка.");
    }

    @DisplayName("Запись при наличии задач и истории")
    @Test
    public void dataWriteWithTasksAndHistory() throws IOException {
        createTask(TaskType.TASK);
        requestTaskById(1);
        restartTaskServer();
        Task task = httpTaskManager.getTasksForTests().get(1);
        boolean isTaskPresent = checkTask(task, "3", "3", 1, TaskStatus.NEW,
                LocalDateTime.of(2023, Month.FEBRUARY, 28, 8, 0), 30);
        boolean isHistoryPresent = httpTaskManager.getHistory().equals(new ArrayList<>(List.of(1)));
        Assertions.assertTrue(isTaskPresent && isHistoryPresent, "Ошибка при загрузке задач и истории.");
    }

    @DisplayName("Запись без истории")
    @Test
    public void dataWriteWithNoHistory() throws IOException {
        createTask(TaskType.TASK);
        restartTaskServer();
        Task task = httpTaskManager.getTasksForTests().get(1);
        boolean isTaskPresent = checkTask(task, "3", "3", 1, TaskStatus.NEW,
                LocalDateTime.of(2023, Month.FEBRUARY, 28, 8, 0), 30);
        boolean isHistoryPresent = httpTaskManager.getHistory().isEmpty();
        Assertions.assertTrue(isTaskPresent && isHistoryPresent, "Ошибка при загрузке задач и истории.");
    }

    @DisplayName("Запрос сохраненного эпика без подзадач")
    @Test
    public void dataWriteWithEpicWithNoSubTasks() throws IOException {
        createTask(TaskType.EPIC);
        String task = gson2.toJson(httpTaskManager.getTasksForTests().get(1));
        restartTaskServer();
        String task2 = requestTaskById(1);
        Assertions.assertEquals(task, task2, "Ошибка при сохранении пустого эпика.");
    }

    @DisplayName("Запрос сохраненного эпика с подзадачами")
    @Test
    public void dataReadFromFileWithEpicWithNoSubTasks() throws IOException {
        createTask(TaskType.EPIC);
        createTask(TaskType.SUBTASK);
        String task = gson.toJson(httpTaskManager.getTasksForTests().get(1));
        restartTaskServer();
        String task2 = requestTaskById(1);
        Assertions.assertEquals(task, task2, "Ошибка при сохранении эпика с подзадачами.");
    }

    @DisplayName("Время выполнения задач. Сохранение задачи со временем")
    @Test
    public void dataWriteWithTimeData() throws IOException {
        createTask(TaskType.TASK);
        String task = gson.toJson(httpTaskManager.getTasksForTests().get(1));
        restartTaskServer();
        String task2 = requestTaskById(1);
        Assertions.assertEquals(task, task2, "Ошибка при сохранении задачи со временем.");
    }

    @DisplayName("Время выполнения задач. Сохранение задачи без времени")
    @Test
    public void dataWriteWithNoTimeData() throws IOException {
        createTaskWithoutTime();
        String task = gson2.toJson(httpTaskManager.getTasksForTests().get(1));
        restartTaskServer();
        String task2 = requestTaskById(1);
        Assertions.assertEquals(task, task2, "Ошибка при сохранении задачи со временем.");
    }

    @DisplayName("Время выполнения задач. Сохранение задачи со временем")
    @Test
    public void TimeOverlayWithTwoTasks() {
        createTask(TaskType.TASK);
        String response = createTask(TaskType.TASK);
        String answer = "Задача пересекается по времени с другими задачами.";
        Assertions.assertEquals(response, answer, "Ошибка при создании задач с пересечением по времени.");
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

    private String requestTaskById(int taskId) {
        URI uri = URI.create("http://localhost:8080/tasks/task?id=" + taskId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, handler);
            return response.body();
        } catch (IOException | InterruptedException exception) {
            throw new HttpExceptions.ErrorInTestManager("Ошибка при отправке запроса на получение задачи по номеру.");
        }
    }

    private String createTask(TaskType type) {
        String body = null;
        NewTask newTask;
        URI uri = null;
        switch (type) {
            case TASK -> {
                uri = URI.create("http://localhost:8080/tasks/task/");
                newTask = new NewTask("3", "3",
                        LocalDateTime.of(2023, Month.FEBRUARY, 28, 8, 0), 30);
                body = gson.toJson(newTask);
            }
            case EPIC -> {
                uri = URI.create("http://localhost:8080/tasks/epic/");
                newTask = new NewTask("1", "1",
                        LocalDateTime.of(2023, Month.FEBRUARY, 28, 9, 0), 30);
                body = gson.toJson(newTask);
            }
            case SUBTASK -> {
                uri = URI.create("http://localhost:8080/tasks/subtask/");
                newTask = new NewTask("2", "2",
                        LocalDateTime.of(2023, Month.FEBRUARY, 28, 10, 0), 30);
                JsonObject jsonObject = JsonParser.parseString(gson.toJson(newTask)).getAsJsonObject();
                jsonObject.addProperty("epicId", 1);
                body = gson.toJson(jsonObject);
            }
        }
        HttpRequest.BodyPublisher publisher = HttpRequest.BodyPublishers.ofString(body);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(publisher)
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, handler);
            return response.body();
        } catch (IOException | InterruptedException exception) {
            throw new HttpExceptions.ErrorInTestManager("Ошибка при отправке запроса на создание новой задачи.");
        }
    }

    private void createTaskWithoutTime() {
        URI uri = URI.create("http://localhost:8080/tasks/task/");
        NewTask newTask = new NewTask("4", "4", null, 0);
        String body = gson.toJson(newTask);
        HttpRequest.BodyPublisher publisher = HttpRequest.BodyPublishers.ofString(body);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(publisher)
                .build();
        try {
            httpClient.send(request, handler);
        } catch (IOException | InterruptedException exception) {
            throw new HttpExceptions.ErrorInTestManager
                    ("Ошибка при отправке запроса на создание новой задачи без времени.");
        }
    }

    private void restartTaskServer() throws IOException {
        server.stopTaskServer();
        resetIdCounter();
        server = new HttpTaskServer();
        server.startTasksServer();
    }
}