package Server;

import Manager.Managers;
import Manager.TaskManager;
import Model.NewTask;
import Model.Task;
import Model.TaskStatus;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    TaskManager manager = Managers.getWithAutoSave();

    public void startTasksServer() throws IOException {
        HttpServer httpServer = HttpServer.create();
        int PORT = 8080;
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler());
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    class TaskHandler implements HttpHandler {
        Gson gson = new Gson();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String[] request = exchange.getRequestURI().getPath().split("/");
            String requestType = request[2];
            String response = null;
            switch (method) {
                case ("GET") -> {
                    if (requestType.equals("task") && !request[3].contains("?id=")) {
                        response = getTaskById(request);
                    } else if (requestType.equals("tasks")) {
                        response = getAllTasks();
                    } else if (requestType.equals("history")) {
                        response = getHistory();
                    }
                    exchange.sendResponseHeaders(200, 0);
                }
                case ("POST") -> {
                    createTask(exchange);
                    exchange.sendResponseHeaders(201, 0);
                }
                case ("DELETE") -> {
                    if (requestType.equals("task") && !request[3].contains("?id=")) {
                        response = removeTaskById(request);
                    } else if (requestType.equals("tasks")) {
                        response = "Список задач очищен.";
                    }
                    exchange.sendResponseHeaders(204, 0);
                }
                default -> {
                }
            }
        }

        private String getTaskById(String[] request) {
            int taskId = 0;
            String[] splitRequest = request[3].split("=");
            taskId = Integer.parseInt(splitRequest[1]);
            Task task = manager.getTaskById(taskId);
            return gson.toJson(task);
        }

        private String getAllTasks() {
            return gson.toJson(manager.getAllTasks());
        }

        private String getHistory() {
            return gson.toJson(manager.getHistory());
        }

        private String removeTaskById(String[] request) {
            int taskId = 0;
            String[] splitRequest = request[3].split("=");
            taskId = Integer.parseInt(splitRequest[1]);
            manager.removeTaskById(taskId);
            return "Задача " + taskId + " удалена.";
        }

        private void createTask(HttpExchange exchange) throws IOException {
            String[] request = exchange.getRequestURI().getPath().split("/");
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            JsonElement je = JsonParser.parseString(body);
            String newTaskType = request[2];
            Integer epicId = null;
            if (je.isJsonObject()) {
                JsonObject jo = je.getAsJsonObject();
                if (jo.get("taskId").getAsBigInteger() != null && jo.get("saveSubTasks").getAsString() == null) {
                    int taskId = jo.get("taskId").getAsInt();
                    String status = jo.get("taskStatus").getAsString();
                    switch (status) {
                        case ("NEW") -> manager.updateTask(taskId, TaskStatus.NEW);
                        case ("IN_PROGRESS") -> manager.updateTask(taskId, TaskStatus.IN_PROGRESS);
                        case ("DONE") -> manager.updateTask(taskId, TaskStatus.DONE);
                        default -> {
                        }
                    }
                } else if (jo.get("taskId").getAsBigInteger() != null && jo.get("saveSubTasks").getAsString() != null) {
                    int taskId = jo.get("taskId").getAsInt();
                    boolean saveSubTasks = jo.get("saveSubTasks").getAsBoolean();
                    manager.updateTask(taskId, saveSubTasks);
                } else {
                    String taskTitle = jo.get("taskTitle").getAsString();
                    String taskDescription = jo.get("taskDescription").getAsString();
                    LocalDateTime taskStart = LocalDateTime.parse(jo.get("startTime").getAsString());
                    long taskDuration = jo.get("duration").getAsLong();
                    switch (newTaskType) {
                        case "task" ->
                                manager.newSimpleTask(new NewTask(taskTitle, taskDescription, taskStart, taskDuration));
                        case "epic" ->
                                manager.newEpic(new NewTask(taskTitle, taskDescription, taskStart, taskDuration));
                        case "subtask" -> {
                            if (je.isJsonObject()) {
                                epicId = jo.get("epicId").getAsInt();
                                manager.newSubtask(new NewTask(taskTitle, taskDescription, taskStart, taskDuration), epicId);
                            }
                        }
                        default -> {
                        }
                    }
                }
            }
        }
    }
}