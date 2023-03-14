package Manager;

import Exceptions.HttpExceptions;
import Server.HttpTaskServer;
import Server.KVTaskClient;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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
        String data = getHistoryForSave() + getHistoryForSave();
        kvTaskClient.put("1", data);
    }

    @Override
    protected void load() {
        String data = kvTaskClient.load("1");
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
}