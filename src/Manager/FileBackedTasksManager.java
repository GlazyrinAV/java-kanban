package Manager;

import Model.*;
import Utils.Reader;
import Utils.Writer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.StringJoiner;

public class FileBackedTasksManager extends InMemoryTaskManager {

    public FileBackedTasksManager(InMemoryHistoryManager history) {
        super(history);
        read();
    }

    @Override
    public Task newSimpleTask(String taskTitle, String taskDescription) {
        Task task = super.newSimpleTask(taskTitle, taskDescription);
        save();
        return task;
    }

    @Override
    public Task newEpic(String taskTitle, String taskDescription) {
        Task task = super.newEpic(taskTitle, taskDescription);
        save();
        return task;
    }

    @Override
    public Task newSubtask(String taskTitle, String taskDescription, int epicId) {
        Task task = super.newSubtask(taskTitle, taskDescription, epicId);
        save();
        return task;
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

    private void save() throws ManagerSaveException {
        List<String> dataToBeSaved = new ArrayList<>();
        for (Task task : getAllTasks().values()) {
            dataToBeSaved.add(taskToString(task));
        }
        dataToBeSaved.add("");
        for (Task task : getHistory()) {
            dataToBeSaved.add(String.valueOf(task.getTaskIdNumber()));
        }
        try {
            new Writer().writeDataToFile(dataToBeSaved);
        } catch (RuntimeException | IOException e) {
            throw new ManagerSaveException("Ошибка при записи данных в файл.");
        }
    }

    private void read() throws ManagerLoadException {
        List<String> dataFromStorage;
        try {
            dataFromStorage = new Reader().readDataFromFile();
        } catch (IOException e) {
            throw new ManagerLoadException("Ошибка при сохранении данных в файл.");
        }
        loadTaskFromStorage(getTasksFromDataFile(dataFromStorage));
    }

    private String taskToString(Task task) {
        StringJoiner taskInString = new StringJoiner(",");
        taskInString.add(String.valueOf(task.getTaskIdNumber()));
        taskInString.add(getTaskTypeInString(task));
        taskInString.add(task.getTaskTitle());
        taskInString.add(String.valueOf(task.getTaskStatus()));
        taskInString.add(task.getTaskDescription());
        taskInString.add(getEpicIdOfSubtask(task));
        return String.valueOf(taskInString);
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
        list.sort(new ListComparator());
        String taskType;
        for (String[] line : list) {
            taskType = line[1];
            if (!taskType.equals("SUBTASK")) createTaskFromDataFile(line);
        }
        for (String[] line : list) {
            taskType = line[1];
            if (taskType.equals("SUBTASK")) createTaskFromDataFile(line);
        }
    }

    private List<String[]> getTasksFromDataFile(List<String> list) {
        List<String[]> dataSeparated = new ArrayList<>();
        for (int i = 2; i < list.size(); i += 2) {
            if (list.get(i).isBlank()) break;
            dataSeparated.add(list.get(i).split(","));
        }
        return dataSeparated;
    }

    private List<String[]> getHistoryFromDataFile(List<String> list) {
        List<String[]> dataSeparated = new ArrayList<>();
        boolean isStartOfHistory = false;
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i).isBlank()) isStartOfHistory = true;
            while (!isStartOfHistory) {
                continue;
            }
            dataSeparated.add(list.get(i).split(","));
        }
        return dataSeparated;
    }

    static class ManagerSaveException extends RuntimeException {

        public ManagerSaveException(final String message) {
            super(message);
        }
    }

    static class ManagerLoadException extends RuntimeException {

        public ManagerLoadException(final String message) {
            super(message);
        }
    }

    private void createTaskFromDataFile(String[] line) {
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

class ListComparator implements Comparator<String[]> {
    public int compare(String[] o1, String[] o2) {
        return Integer.parseInt(o1[0]) - Integer.parseInt(o2[0]);
    }
}