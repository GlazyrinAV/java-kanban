package Manager;

import Exceptions.ManagerExceptions;
import Exceptions.UtilsExceptions;
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
        load();
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
     * @throws ManagerExceptions.ManagerSaveException - ошибка при сохранении данных
     */
    private void save() throws ManagerExceptions.ManagerSaveException {
        List<String> dataToBeSaved = new ArrayList<>();
        for (Task task : getAllTasks().values()) {
            dataToBeSaved.add(new SupportFunctions().taskToString(task));
        }
        dataToBeSaved.add("\n");
        StringJoiner history = new StringJoiner(",");
        for (Integer taskId : historyManager.getHistory()) {
            history.add(String.valueOf(taskId));
        }
        dataToBeSaved.add(history.toString());
        try {
            new Writer().writeDataToFile(dataToBeSaved);
        } catch (IOException e) {
            throw new ManagerExceptions.ManagerSaveException("Ошибка при записи данных в файл.");
        }
    }

    /**
     * Загружает задачи и историю просмотров из файла-хранилища
     *
     * @throws ManagerExceptions.ManagerLoadException - ошибка при загрузке данных
     */
    private void load() throws ManagerExceptions.ManagerLoadException, UtilsExceptions.NoHistoryDataInStorageException {
        List<String> dataFromStorage;
        try {
            dataFromStorage = new Reader().readDataFromFile();
        } catch (IOException e) {
            throw new ManagerExceptions.ManagerLoadException("Ошибка при сохранении данных в файл.");
        }
        if (!dataFromStorage.isEmpty()) {
            Loader loader = new Loader();
            loader.loadTaskFromStorage(loader.getTasksFromDataFile(dataFromStorage), tasks);
            loader.loadHistoryFromStorage(loader.getHistoryFromDataFile(dataFromStorage), historyManager);
        }
    }
}