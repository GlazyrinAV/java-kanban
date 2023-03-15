package Manager;

import Model.*;
import Server.KVTaskClient;
import Utils.DateAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Objects;

public class HttpTaskManager extends FileBackedTasksManager {
    private final Gson gson = new Gson();
    private KVTaskClient kvTaskClient;

    public HttpTaskManager(InMemoryHistoryManager history, String url) {
        super(history, url);
    }

    @Override
    protected void save() {
        JsonObject tempData = new JsonObject();
        tempData.addProperty("SavedTasks", getTasksForSave());
        tempData.addProperty("SavedHistory", getHistoryForSave());
        String data = gson.toJson(tempData);
        kvTaskClient.put("Alex", data);
    }

    @Override
    protected void load() {
        kvTaskClient = new KVTaskClient(path);
        String dataFromKVServer = kvTaskClient.load("Alex");
        if (!dataFromKVServer.isEmpty()) {
            JsonElement jsonElement = JsonParser.parseString(dataFromKVServer);
            if (jsonElement.isJsonObject()) {
                JsonObject tempData = jsonElement.getAsJsonObject();
                String taskData = tempData.get("SavedTasks").getAsString();
                String historyData = tempData.get("SavedHistory").getAsString();
                if (!taskData.isEmpty()) {
                    restoreTaskFromData(taskData);
                }
                if (!historyData.isEmpty()) {
                    restoreHistoryFromData(historyData);
                }
            }
        }
    }

    private String getTasksForSave() {
        Gson gsonBuilder = gson.newBuilder()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new DateAdapter())
                .create();
        return gsonBuilder.toJson(getAllTasks().values());
    }

    private String getHistoryForSave() {
        return gson.toJson(getHistory());
    }

    private void restoreTaskFromData(String data) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        JsonElement element = JsonParser.parseString(data);
        for (JsonElement line : element.getAsJsonArray()) {
            JsonObject jsonObject = line.getAsJsonObject();
            int taskId = jsonObject.get("taskIdNumber").getAsInt();
            String title = jsonObject.get("taskTitle").getAsString();
            String description = jsonObject.get("taskDescription").getAsString();
            TaskStatus status = TaskStatus.valueOf(jsonObject.get("taskStatus").getAsString());
            LocalDateTime start;
            if (!jsonObject.get("startTime").isJsonNull()) {
                start = LocalDateTime.parse(jsonObject.get("startTime").getAsString(), formatter);
            } else {
                start = null;
            }
            long duration = jsonObject.get("duration").getAsLong();
            TaskType taskType = TaskType.valueOf(jsonObject.get("taskType").getAsString());
            if (Objects.requireNonNull(taskType) == TaskType.TASK) {
                tasks.put(taskId, new SimpleTask(title, description, status, taskId, start, duration, taskType));
                prioritizedTasks.add(taskId);
            } else if (taskType == TaskType.EPIC) {
                tasks.put(taskId, new EpicTask(title, description, status, taskId, start, duration, taskType));
            } else if (taskType == TaskType.SUBTASK) {
                int epicId = jsonObject.get("epicId").getAsInt();
                tasks.put(taskId, new Subtask(title, description, status, taskId, epicId, start, duration, taskType));
                ((EpicTask) tasks.get(epicId)).addSubTask(taskId, status, start, duration);
                prioritizedTasks.add(taskId);
            }
        }
    }

    private void restoreHistoryFromData(String data) {
        if (!data.equals("[]")) {
            String[] historyData = data.split(",");
            for (String line : historyData) {
                historyManager.addHistory(Integer.parseInt(line));
            }
        }
    }

    public HashMap<Integer, Task> getTasksForTests() {
        return super.tasks;
    }
}