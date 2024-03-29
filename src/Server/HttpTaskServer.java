package Server;

import Exceptions.ManagerExceptions;
import Manager.TaskManager;
import Model.NewTask;
import Model.Task;
import Model.TaskStatus;
import Utils.DateAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import java.util.regex.Pattern;

public class HttpTaskServer {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final TaskManager manager;
    private HttpServer httpServer;
    public HttpTaskServer(TaskManager manager) {
        this.manager = manager;
    }
    public TaskManager getManager() {
        return manager;
    }

    public void startTasksServer() throws IOException {
        httpServer = HttpServer.create();
        int PORT = 8080;
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler());
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stopTaskServer() {
        System.out.println("TaskServer остановлен.");
        httpServer.stop(0);
    }

    class TaskHandler implements HttpHandler {

        private final Gson gson = new Gson().newBuilder()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new DateAdapter())
                .create();

        @Override
        public void handle(HttpExchange exchange) {
            try (exchange) {
                String method = exchange.getRequestMethod();
                String[] request = exchange.getRequestURI().getPath().split("/");
                String requestType = request[2];
                switch (method) {
                    case "GET" -> {
                        System.out.println("Началась обработка GET");
                        switch (requestType) {
                            case "task" -> {
                                if (Pattern.matches("id=\\d+$", exchange.getRequestURI().getRawQuery())) {
                                    String response = getTaskById(exchange.getRequestURI().getRawQuery());
                                    sendGoodResponse(exchange, response);
                                } else {
                                    sendBadResponse(exchange, "Неправильная форма запроса для поиска задачи.");
                                }
                            }
                            case "tasks" -> {
                                String response = getAllTasks();
                                sendGoodResponse(exchange, response);
                            }
                            case "history" -> {
                                String response = getHistory();
                                sendGoodResponse(exchange, response);
                            }
                            case "priority" -> {
                                String response = getTasksByPriority();
                                sendGoodResponse(exchange, response);
                            }
                            default -> sendBadResponse(exchange,
                                    "Для метода " + exchange.getRequestMethod() + " неправильно указан запрос.");
                        }
                    }
                    case "POST" -> {
                        System.out.println("Началась обработка POST");
                        createTask(exchange);
                    }
                    case "DELETE" -> {
                        System.out.println("Началась обработка DELETE");
                        switch (requestType) {
                            case "task" -> {
                                if (exchange.getRequestURI().getRawQuery().contains("id=")) {
                                    String response = removeTaskById(exchange.getRequestURI().getRawQuery());
                                    sendGoodResponse(exchange, response);
                                } else {
                                    sendBadResponse(exchange, "Неправильная форма запроса для удаления задачи.");
                                }
                            }
                            case "tasks" -> {
                                String response = clearAllTasks();
                                sendGoodResponse(exchange, response);
                            }
                            default -> sendBadResponse(exchange,
                                    "Для метода " + exchange.getRequestMethod() + " неправильно указан запрос.");
                        }
                    }
                    default -> sendBadResponse(exchange, "Метод " + exchange.getRequestMethod() + " не реализован.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void sendGoodResponse(HttpExchange exchange, String response) throws IOException {
            exchange.sendResponseHeaders(200, 0);
            try (exchange; OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes(DEFAULT_CHARSET));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void sendBadResponse(HttpExchange exchange, String result) throws IOException {
            exchange.sendResponseHeaders(400, 0);
            try (exchange; OutputStream os = exchange.getResponseBody()) {
                os.write(result.getBytes(DEFAULT_CHARSET));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private String getTasksByPriority() {
            return gson.toJson(manager.getPrioritizedTasks());
        }

        private String getTaskById(String request) {
            String[] splitRequest = request.split("=");
            int taskId = Integer.parseInt(splitRequest[1]);
            Task task = manager.getTaskById(taskId);
            if (task != null) {
                return gson.toJson(task);
            } else {
                return "Задача с данным номером не найдена.";
            }
        }

        private String getAllTasks() {
            return gson.toJson(manager.getAllTasks());
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

        private void createTask(HttpExchange exchange) {
            try (exchange; exchange) {
                String[] request = exchange.getRequestURI().getPath().split("/");
                InputStream inputStream = exchange.getRequestBody();
                String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                JsonElement je = JsonParser.parseString(body);
                String newTaskType = request[2];
                if (je.isJsonObject()) {
                    JsonObject jo = je.getAsJsonObject();
                    if (body.contains("taskId") && !body.contains("saveSubTasks")) {
                        sendGoodResponse(exchange, updateTask(jo));
                    } else if (body.contains("taskId") && body.contains("saveSubTasks")) {
                        sendGoodResponse(exchange, updateEpic(jo));
                    } else {
                        sendGoodResponse(exchange, createTasks(je, newTaskType));
                    }
                } else {
                    sendBadResponse(exchange, "Полученные данные не позволяют создать задачу.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private String updateTask(JsonObject jo) {
            int taskId = jo.get("taskId").getAsInt();
            String status = jo.get("taskStatus").getAsString();
            switch (status) {
                case "NEW" -> manager.updateTask(taskId, TaskStatus.NEW);
                case "IN_PROGRESS" -> manager.updateTask(taskId, TaskStatus.IN_PROGRESS);
                case "DONE" -> manager.updateTask(taskId, TaskStatus.DONE);
                default -> {
                    return "Ошибка при обновлении задачи";
                }
            }
            return "Задача обновлена.";
        }

        private String updateEpic(JsonObject jo) {
            int taskId = jo.get("taskId").getAsInt();
            boolean saveSubTasks = jo.get("saveSubTasks").getAsBoolean();
            manager.updateTask(taskId, saveSubTasks);
            return "Эпик обновлен.";
        }

        private String createTasks(JsonElement je, String newTaskType) {
            JsonObject jo = je.getAsJsonObject();
            String taskTitle = jo.get("taskTitle").getAsString();
            String taskDescription = jo.get("taskDescription").getAsString();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            LocalDateTime taskStart = null;
            long taskDuration = 0;
            if (!jo.get("startTime").isJsonNull()) {
                taskStart = LocalDateTime.parse(jo.get("startTime").getAsString(), formatter);
            }
            if (!jo.get("duration").isJsonNull()) {
                taskDuration = jo.get("duration").getAsLong();
            }
            NewTask newTask = new NewTask(taskTitle, taskDescription, taskStart, taskDuration);
            switch (newTaskType) {
                case "task" -> {
                    try {
                        manager.newSimpleTask(newTask);
                        return "Задача создана.";
                    } catch (ManagerExceptions.TaskTimeOverlayException e) {
                        return "Задача пересекается по времени с другими задачами.";
                    }
                }
                case "epic" -> {
                    manager.newEpic(newTask);
                    return "Задача создана.";
                }
                case "subtask" -> {
                    try {
                        if (je.isJsonObject()) {
                            int epicId = jo.get("epicId").getAsInt();
                            manager.newSubtask(newTask, epicId);
                            return "Задача создана.";
                        }
                    } catch (ManagerExceptions.TaskTimeOverlayException e) {
                        return "Задача пересекается по времени с другими задачами.";
                    }
                }
            }
            return "При определении типа создаваемой задачи возникла ошибка.";
        }
    }
}