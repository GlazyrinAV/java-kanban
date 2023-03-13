package Server;

import Manager.Managers;
import Manager.TaskManager;
import Model.NewTask;
import Model.Task;
import Model.TaskStatus;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    static class DateAdapter extends TypeAdapter<LocalDateTime> {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        @Override
        public void write(JsonWriter jsonWriter, LocalDateTime time) throws IOException {
            jsonWriter.value(time.format(formatter));
        }

        @Override
        public LocalDateTime read(JsonReader jsonReader) {
            return LocalDateTime.parse(jsonReader.toString(), formatter);
        }
    }

    class TaskHandler implements HttpHandler {
        Gson gson = new Gson();
        Gson gsonBuilder = gson.newBuilder()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new DateAdapter())
                .create();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String[] request = exchange.getRequestURI().getPath().split("/");
            String requestType = request[2];
            switch (method) {
                case "GET" -> {
                    System.out.println("Началась обработка GET");
                    switch (requestType) {
                        case "task" -> {
                            String response = getTaskById(exchange.getRequestURI().getRawQuery());
                            exchange.sendResponseHeaders(200, 0);
                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write(response.getBytes(DEFAULT_CHARSET));
                            }
                        }
                        case "tasks" -> {
                            String response = getAllTasks();
                            exchange.sendResponseHeaders(200, 0);
                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write(response.getBytes(DEFAULT_CHARSET));
                            }
                        }
                        case "history" -> {
                            String response = getHistory();
                            exchange.sendResponseHeaders(200, 0);
                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write(response.getBytes(DEFAULT_CHARSET));
                            }
                        }
                    }
                }
                case "POST" -> {
                    System.out.println("Началась обработка POST");
                    String response = createTask(exchange);
                    exchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes(DEFAULT_CHARSET));
                    }
                }
                case "DELETE" -> {
                    System.out.println("Началась обработка DELETE");
                    if (requestType.equals("task")) {
                        String response = removeTaskById(exchange.getRequestURI().getRawQuery());
                        exchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(response.getBytes(DEFAULT_CHARSET));
                        }
                    } else if (requestType.equals("tasks")) {
                        String response = clearAllTasks();
                        exchange.sendResponseHeaders(200, 0);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(response.getBytes(DEFAULT_CHARSET));
                        }
                    }
                }
                default -> {
                }
            }
        }

        private String getTaskById(String request) {
            String[] splitRequest = request.split("=");
            int taskId = Integer.parseInt(splitRequest[1]);
            Task task = manager.getTaskById(taskId);
            return gsonBuilder.toJson(task);
        }

        private String getAllTasks() {
            return gsonBuilder.toJson(manager.getAllTasks());
        }

        private String getHistory() {
            return gson.toJson(manager.getHistory());
        }

        private String removeTaskById(String request) {
            String[] splitRequest = request.split("=");
            int taskId = Integer.parseInt(splitRequest[1]);
            manager.removeTaskById(taskId);
            return "Задача " + taskId + " удалена.";
        }

        private String clearAllTasks() {
            manager.clearAllTasks();
            return "Список задач очищен.";
        }

        private String createTask(HttpExchange exchange) throws IOException {
            String[] request = exchange.getRequestURI().getPath().split("/");
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            JsonElement je = JsonParser.parseString(body);
            String newTaskType = request[2];
            if (je.isJsonObject()) {
                JsonObject jo = je.getAsJsonObject();
                if (body.contains("taskId") && !body.contains("saveSubTasks")) {
                    int taskId = jo.get("taskId").getAsInt();
                    String status = jo.get("taskStatus").getAsString();
                    switch (status) {
                        case "NEW" -> manager.updateTask(taskId, TaskStatus.NEW);
                        case "IN_PROGRESS" -> manager.updateTask(taskId, TaskStatus.IN_PROGRESS);
                        case "DONE" -> manager.updateTask(taskId, TaskStatus.DONE);
                        default -> {
                        }
                    }
                    return "Задача обновлена.";
                } else if (body.contains("taskId") && body.contains("saveSubTasks")) {
                    int taskId = jo.get("taskId").getAsInt();
                    boolean saveSubTasks = jo.get("saveSubTasks").getAsBoolean();
                    manager.updateTask(taskId, saveSubTasks);
                    return "Эпик обновлен.";
                } else {
                    String taskTitle = jo.get("taskTitle").getAsString();
                    String taskDescription = jo.get("taskDescription").getAsString();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
                    LocalDateTime taskStart = LocalDateTime.parse(jo.get("startTime").getAsString(), formatter);
                    long taskDuration = jo.get("duration").getAsLong();
                    NewTask newTask = new NewTask(taskTitle, taskDescription, taskStart, taskDuration);
                    switch (newTaskType) {
                        case "task" -> manager.newSimpleTask(newTask);
                        case "epic" -> manager.newEpic(newTask);
                        case "subtask" -> {
                            if (je.isJsonObject()) {
                                int epicId = jo.get("epicId").getAsInt();
                                manager.newSubtask(newTask, epicId);
                            }
                        }
                        default -> {
                        }
                    }
                    return "Задача создана.";
                }
            }
            return "Задача не добавлена";
        }
    }
}