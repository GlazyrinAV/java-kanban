package Manager;

import Server.HttpTaskServer;
import Server.KVTaskClient;
import com.google.gson.Gson;

public class HttpTaskManager extends FileBackedTasksManager {

    KVTaskClient kvTaskClient;
    HttpTaskServer httpTaskServer;
    Gson gson = new Gson();

    public HttpTaskManager(InMemoryHistoryManager history, String url) {
        super(history);
        kvTaskClient = new KVTaskClient(url);
        this.httpTaskServer = new HttpTaskServer(this);
    }

    @Override
    protected void save() {
        String data;
        kvTaskClient.put("1", data);
    }

    @Override
    protected void load() {
        kvTaskClient.load("1");
    }
}
