package Manager;

import Server.KVTaskClient;

import java.io.IOException;

public class HttpTaskManager extends FileBackedTasksManager {

    KVTaskClient kvTaskClient;

    public HttpTaskManager(InMemoryHistoryManager history, String url) throws IOException, InterruptedException {
        super(history);
        kvTaskClient = new KVTaskClient(url);
        kvTaskClient.load("Alex");
    }


}
