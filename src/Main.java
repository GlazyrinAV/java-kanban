import Manager.Managers;
import Server.HttpTaskServer;
import Server.KVServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        new KVServer().start();
        new HttpTaskServer(Managers.getWithHttpManager()).startTasksServer();
    }
}