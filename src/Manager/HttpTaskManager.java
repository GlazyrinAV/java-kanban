package Manager;

import Exceptions.HttpExceptions;
import Model.*;
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
import java.time.LocalDateTime;
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
            String title = task.getTaskTitle();
            String description = task.getTaskDescription();
            TaskStatus status = task.getTaskStatus();
            LocalDateTime start = task.getStartTime();
            long duration = task.getDuration();
            switch (tasksFromData.get(taskId).getTaskType()) {
                case TASK:
                    tasks.put(taskId, new SimpleTask(title, description, status, taskId, start, duration));
                    prioritizedTasks.add(taskId);
                    break;
                case EPIC:
                    tasks.put(taskId, new EpicTask(title, description, status, taskId, start, duration));
                    break;
                case SUBTASK:
                    int epicId = ((Subtask) task).getEpicId();
                    tasks.put(taskId, new Subtask(title, description, status, taskId, epicId, start, duration));
                    ((EpicTask) tasks.get(epicId)).addSubTask(taskId, status, start, duration);
                    prioritizedTasks.add(taskId);
                    break;
                default:
            }
        }
    }

    private void restoreHistoryFromData(String data) {
        String[] historyData = data.split(",");
        for (String line : historyData) {
            historyManager.addHistory(Integer.parseInt(line));
        }
    }
}