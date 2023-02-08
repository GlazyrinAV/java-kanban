package Manager;

import Model.*;
import Utils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {

    public FileBackedTasksManager(InMemoryHistoryManager history) {
        super(history);
        read();
    }

    @Override
    public void newSimpleTask(String taskTitle, String taskDescription) {
        super.newSimpleTask(taskTitle, taskDescription);
        save();
    }

    @Override
    public void newEpic(String taskTitle, String taskDescription) {
        super.newEpic(taskTitle, taskDescription);
        save();
    }

    @Override
    public void newSubtask(String taskTitle, String taskDescription, int epicId) {
        super.newSubtask(taskTitle, taskDescription, epicId);
        save();
    }

    @Override
    public void updateTask(int taskId, TaskStatus taskStatus) {
        super.updateTask(taskId, taskStatus);
        save();
    }

    @Override
    public void updateTask(int epicId, boolean saveSubTasks) {
        super.updateTask(epicId, saveSubTasks);
        save();
    }

    @Override
    public void removeTaskById(int taskId) {
        super.removeTaskById(taskId);
        save();
    }

    private void save() throws IOException {
        List<String> dataToBeSaved = new ArrayList<>();
        for (Task task : getAllTasks().values()) {
            dataToBeSaved.add(taskToString(task));
        }
        dataToBeSaved.add("");
        for (Task task : getHistory()) {
            dataToBeSaved.add(String.valueOf(task.getTaskIdNumber()));
        }
        new Writer().writeDataToFile(dataToBeSaved);
    }

    private void read() throws IOException {
        List<String> dataFromStorage = new Reader().readDataFromFile();
        List<String[]> dataSeparated = new ArrayList<>();
        for (int i = 1; i<dataFromStorage.size(); i++) {
            dataSeparated.add(dataFromStorage.get(i).split(","));
        }
    }

    private String taskToString(Task task) {
        String id = String.valueOf(task.getTaskIdNumber());
        String taskType = getTaskTypeInString(task);
        String name = String.valueOf(task.getTaskTitle());
        String status = String.valueOf(task.getTaskStatus());
        String description = String.valueOf(task.getTaskDescription());
        String connectedIds = getEpicIdOfSubtask(task);
        return String.join(id, taskType, name, status, description, connectedIds);
    }

    private String getTaskTypeInString(Task task) {
        if (task instanceof EpicTask) {
            return TaskType.EPIC.toString();
        } else if (task instanceof Subtask) {
            return TaskType.SUBTASK.toString();
        }
        return TaskType.TASK.toString();
    }

    private String getEpicIdOfSubtask(Task task) {
        if (task instanceof Subtask) {
            return String.valueOf(((Subtask) task).getEpicId());
        }
        return " ";
    }

    private void loadTaskFromStorage(List<String[]> list) {
        list.sort(comparator);
        for (String[] line : list) {
            if (line[1].equals("TASK")) {
                newSimpleTask(line[2], line[4]);
                updateTask(getTaskIdByName(line[2]), TaskStatus.valueOf(line[3]));
            }
        }
    }

    private final Comparator<String[]> comparator = new Comparator<String[]>() {
        @Override
        public int compare(String[] o1, String[] o2) {
            return Integer.parseInt(o1[0]) - Integer.parseInt(o2[0]);
        }
    }
}