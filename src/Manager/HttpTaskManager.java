package Manager;

import Model.*;
import Server.KVTaskClient;
import Utils.DateAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
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
        String data = getTasksForSave() + "//" + getHistoryForSave();
        kvTaskClient.put("Alex", data);
    }

    @Override
    protected void load() {
        kvTaskClient = new KVTaskClient(path);
        String dataFromKVServer = kvTaskClient.load("1");
        if (!dataFromKVServer.isEmpty()) {
            String[] data = kvTaskClient.load("1").split("//");
            String taskData = data[0];
            String historyData = data[1];
            if (!taskData.isEmpty()) {
                restoreTaskFromData(taskData);
            }
            if (!historyData.isEmpty()) {
                restoreHistoryFromData(historyData);
            }
        }
    }

    private String getTasksForSave() {
        Gson gsonBuilder = gson.newBuilder()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new DateAdapter())
                .create();
        return gsonBuilder.toJson(getAllTasks());
    }

    private String getHistoryForSave() {
        return gson.toJson(getHistory());
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
            TaskType taskType = tasksFromData.get(taskId).getTaskType();
            if (Objects.requireNonNull(taskType) == TaskType.TASK) {
                tasks.put(taskId, new SimpleTask(title, description, status, taskId, start, duration));
                prioritizedTasks.add(taskId);
            } else if (taskType == TaskType.EPIC) {
                tasks.put(taskId, new EpicTask(title, description, status, taskId, start, duration));
            } else if (taskType == TaskType.SUBTASK) {
                int epicId = ((Subtask) task).getEpicId();
                tasks.put(taskId, new Subtask(title, description, status, taskId, epicId, start, duration));
                ((EpicTask) tasks.get(epicId)).addSubTask(taskId, status, start, duration);
                prioritizedTasks.add(taskId);
            }
        }
    }

    private void restoreHistoryFromData(String data) {
        String[] historyData = data.split(",");
        for (String line : historyData) {
            historyManager.addHistory(Integer.parseInt(line));
        }
    }

    public HashMap<Integer, Task> getTasksForTests() {
        return super.tasks;
    }
}