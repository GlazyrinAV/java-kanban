package Utils;

import Manager.HistoryManager;
import Model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Loader {

    public void loadTaskFromStorage(List<String[]> list, HashMap<Integer, Task> tasks) {
        String taskType;
        for (int i = 0; i < list.size() - 2; i++) {
            taskType = list.get(i)[1];
            if (!taskType.equals("SUBTASK")) createTaskFromDataFile(list.get(i), tasks);
        }
        for (int i = 0; i < list.size() - 2; i++) {
            taskType = list.get(i)[1];
            if (taskType.equals("SUBTASK")) createTaskFromDataFile(list.get(i), tasks);
        }
    }

    public void loadHistoryFromStorage(List<String[]> list, HistoryManager historyManager,
                                       HashMap<Integer, Task> tasks) {
        String[] historyFromStorage = list.get(list.size() - 1);
        for (String taskId : historyFromStorage) {
            historyManager.addHistory(tasks.get(Integer.parseInt(taskId)));
        }
    }

    public List<String[]> getTasksFromDataFile(List<String> list) {
        List<String[]> dataSeparated = new ArrayList<>();
        for (int i = 1; i < list.size(); i++) {
            dataSeparated.add(list.get(i).split(","));
        }
        return dataSeparated;
    }

    public List<String[]> getHistoryFromDataFile(List<String> list) {
        List<String[]> dataSeparated = new ArrayList<>();
        dataSeparated.add(list.get(list.size() - 1).split(","));
        return dataSeparated;
    }

    private void createTaskFromDataFile(String[] line, HashMap<Integer, Task> tasks) {
        String taskTitle = line[2];
        TaskStatus taskStatus = TaskStatus.valueOf(line[3]);
        String taskDescription = line[4];
        int taskId = Integer.parseInt(line[0]);
        boolean isTask = line[1].equals("TASK");
        boolean isEpic = line[1].equals("EPIC");
        boolean isSubTask = line[1].equals("SUBTASK");
        if (isTask) tasks.put(taskId, new SimpleTask(taskTitle, taskDescription, taskStatus, taskId));
        else if (isEpic) tasks.put(taskId, new EpicTask(taskTitle, taskDescription, taskStatus, taskId));
        else if (isSubTask) {
            int subtaskEpicId = Integer.parseInt(line[5]);
            tasks.put(taskId, new Subtask(taskTitle, taskDescription, taskStatus, taskId, subtaskEpicId));
            ((EpicTask) tasks.get(subtaskEpicId)).addSubTask(taskId, taskStatus);
        }
    }
}
