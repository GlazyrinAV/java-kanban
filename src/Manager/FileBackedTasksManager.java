package Manager;

import Model.Task;
import Model.TaskStatus;
import Utils.Loader;
import Utils.Reader;
import Utils.SupportFunctions;
import Utils.Writer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

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

    @Override
    public void clearAllTasks() {
        super.clearAllTasks();
        save();
    }

    /**
     * Сохраняет задачи и историю просмотров в файл-хранилище
     *
     * @throws ManagerSaveException - ошибка при сохранении данных
     */
    private void save() throws ManagerSaveException {
        List<String> dataToBeSaved = new ArrayList<>();
        for (Task task : getAllTasks().values()) {
            dataToBeSaved.add(new SupportFunctions().taskToString(task));
        }
        dataToBeSaved.add("\n");
        StringJoiner history = new StringJoiner(",");
        for (Task task : historyManager.getHistory()) {
            history.add(String.valueOf(task.getTaskIdNumber()));
        }
        dataToBeSaved.add(history.toString());
        try {
            new Writer().writeDataToFile(dataToBeSaved);
        } catch (RuntimeException | IOException e) {
            throw new ManagerSaveException("Ошибка при записи данных в файл.");
        }
    }

    /**
     * Загружает задачи и историю просмотров из файла-хранилища
     *
     * @throws ManagerLoadException - ошибка при загрузке данных
     */
    private void read() throws ManagerLoadException {
        List<String> dataFromStorage;
        try {
            dataFromStorage = new Reader().readDataFromFile();
        } catch (IOException e) {
            throw new ManagerLoadException("Ошибка при сохранении данных в файл.");
        }
        if (!dataFromStorage.isEmpty()) {
            Loader loader = new Loader();
            loader.loadTaskFromStorage(loader.getTasksFromDataFile(dataFromStorage), tasks);
            loader.loadHistoryFromStorage(loader.getHistoryFromDataFile(dataFromStorage), historyManager, tasks);
        }
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
}