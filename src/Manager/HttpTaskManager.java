package Manager;

import Exceptions.HttpExceptions;
import Model.Task;
import Server.HttpTaskServer;
import Server.KVTaskClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

public class HttpTaskManager extends FileBackedTasksManager {

    private final KVTaskClient kvTaskClient;
    private final int PORT;
    private final Gson gson = new Gson();
    private final String url;

    public HttpTaskManager(InMemoryHistoryManager history, String url) {
        super(history);
        this.url = url;
        kvTaskClient = new KVTaskClient(url);
        HttpTaskServer httpTaskServer = new HttpTaskServer(this);
        this.PORT = httpTaskServer.getPORT();
        load();
    }

    @Override
    protected void save() {
        String data = getTasksForSave() + "//" + getHistoryForSave();
        kvTaskClient.put("1", data);
    }

    @Override
    protected void load() {
        String[] data = kvTaskClient.load("1").split("//");
        String taskData = data[0];
        String historyData = data[1];
        restoreTaskFromData(taskData);
        restoreHistoryFromData(historyData);
    }

    private String getTasksForSave() {
        URI uriGetAllTasks = URI.create(url + ":" + PORT + "/tasks/tasks/");
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpRequest registerRequest = HttpRequest.newBuilder()
                .GET()
                .uri(uriGetAllTasks)
                .build();
        try {
            HttpResponse<String> response = httpClient.send(registerRequest, handler);
            return response.body();
        } catch (IOException | InterruptedException exception) {
            throw new HttpExceptions.ErrorInHttpTaskManager("Ошибка при выгрузке задач.");
        }
    }

    private String getHistoryForSave() {
        URI uriGetHistory = URI.create(url + ":" + PORT + "/tasks/history/");
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpRequest registerRequest = HttpRequest.newBuilder()
                .GET()
                .uri(uriGetHistory)
                .build();
        try {
            HttpResponse<String> response = httpClient.send(registerRequest, handler);
            return response.body();
        } catch (IOException | InterruptedException exception) {
            throw new HttpExceptions.ErrorInHttpTaskManager("Ошибка при выгрузке истории.");
        }
    }

    private void restoreTaskFromData(String data) {
        Type type = new TypeToken<HashMap<Integer, Task>>() {
        }.getType();
        HashMap<Integer, Task> tasksFromData = gson.fromJson(data, type);
        for (int taskId : tasksFromData.keySet()) {
            Task task = tasksFromData.get(taskId);
            switch (tasksFromData.get(taskId).getTaskType()) {
                case TASK:
                    newSimpleTask();
                    break;
                case EPIC:
                    newEpic();
                    break;
                case SUBTASK:
                    newSubtask();
                    break;
                default:
            }
        }
    }

    private void restoreHistoryFromData(String data) {

    }
}