package Manager;

import Exceptions.ManagerExceptions;
import Model.NewTask;
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

    protected final String path;

    public FileBackedTasksManager(InMemoryHistoryManager history, String path) {
        super(history);
        this.path = path;
        load();
    }

    @Override
    public void newSimpleTask(NewTask task) {
        super.newSimpleTask(task);
        save();
    }

    @Override
    public void newEpic(NewTask task) {
        super.newEpic(task);
        save();
    }

    @Override
    public void newSubtask(NewTask task, int epicId) {
        super.newSubtask(task, epicId);
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
    public Task removeTaskById(int taskId) {
        Task task = super.removeTaskById(taskId);
        save();
        return task;
    }

    @Override
    public void clearAllTasks() {
        super.clearAllTasks();
        save();
    }

    @Override
    public Task getTaskById(int taskId) {
        Task task = super.getTaskById(taskId);
        save();
        return task;
    }

    /**
     * Сохраняет задачи и историю просмотров в файл-хранилище
     *
     * @throws ManagerExceptions.ManagerSaveException - ошибка при сохранении данных
     */
    protected void save() throws ManagerExceptions.ManagerSaveException {
        List<String> dataToBeSaved = new ArrayList<>();
        for (Task task : getAllTasks().values()) {
            dataToBeSaved.add(new SupportFunctions().taskToString(task));
        }
        dataToBeSaved.add("\n");
        StringJoiner history = new StringJoiner(",");
        if (historyManager.getHistory().isEmpty())
            history.add(" ");
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
    protected void load() throws ManagerExceptions.ManagerLoadException {
        List<String> dataFromStorage;
        try {
            dataFromStorage = new Reader().readDataFromFile();
        } catch (IOException e) {
            throw new ManagerExceptions.ManagerLoadException("Ошибка при чтении данных из файла.");
        }
        if (!dataFromStorage.isEmpty()) {
            Loader loader = new Loader();
            loader.loadTaskFromStorage(loader.getTasksFromDataFile(dataFromStorage), tasks, prioritizedTasks);
            loader.loadHistoryFromStorage(loader.getHistoryFromDataFile(dataFromStorage), historyManager);
        }
    }
}