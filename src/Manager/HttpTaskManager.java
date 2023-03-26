package Manager;

import Model.*;
import Server.KVTaskClient;
import Utils.DateAdapter;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class HttpTaskManager extends FileBackedTasksManager {
    private final Gson gson = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new DateAdapter())
            .create();
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
        return gson.toJson(getAllTasks().values());
    }

    private String getHistoryForSave() {
        return gson.toJson(getHistory());
    }

    private void restoreTaskFromData(String data) {
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new DateAdapter())
                .create();
        JsonElement element = JsonParser.parseString(data);
        for (JsonElement line : element.getAsJsonArray()) {
            JsonObject jsonObject = line.getAsJsonObject();
            int taskId = jsonObject.get("taskIdNumber").getAsInt();
            TaskType taskType = TaskType.valueOf(jsonObject.get("taskType").getAsString());
            if (Objects.requireNonNull(taskType) == TaskType.TASK) {
                tasks.put(taskId, gson.fromJson(jsonObject, SimpleTask.class));
                prioritizedTasks.add(taskId);
            } else if (taskType == TaskType.EPIC) {
                tasks.put(taskId, gson.fromJson(jsonObject, EpicTask.class));
            } else if (taskType == TaskType.SUBTASK) {
                int epicId = jsonObject.get("epicId").getAsInt();
                Subtask newTask = gson.fromJson(jsonObject, Subtask.class);
                        tasks.put(taskId, newTask);
                ((EpicTask) tasks.get(epicId)).addSubTask(taskId, newTask);
                prioritizedTasks.add(taskId);
            }
        }
    }

    private void restoreHistoryFromData(String data) {
        Gson gson = new Gson();
        Type dataList = new TypeToken<ArrayList<Integer>>(){}.getType();
        List<Integer> historyFromData = gson.fromJson(data, dataList);
        for (int taskId : historyFromData) {
            historyManager.addHistory(taskId);
        }
    }

    public HashMap<Integer, Task> getTasksForTests() {
        return super.tasks;
    }
}