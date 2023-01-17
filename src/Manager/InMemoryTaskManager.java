package Manager;
import java.util.Collection;
import java.util.HashMap;
import Model.*;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final InMemoryHistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public HashMap<Integer, Task> getAllTasks() {
        HashMap<Integer, Task> allTasks = new HashMap<>(tasks);
        for (int taskID : tasks.keySet()) {
            if (tasks.get(taskID) instanceof EpicTask) {
                EpicTask task = (EpicTask) tasks.get(taskID);
                allTasks.putAll(task.getSubTasks());
            }
        }
        return allTasks; // maybe null
    }

    @Override
    public void clearAllTasks() {
        tasks.clear();
    }

    @Override
    public Task getTaskById (int taskId) {
        if (tasks.containsKey(taskId)) {
            historyManager.addHistory(tasks.get(taskId));
            return tasks.get(taskId);
        } else {
            for (int taskID : tasks.keySet()) {
                if (tasks.get(taskID) instanceof EpicTask) {
                    EpicTask task = (EpicTask) tasks.get(taskID);
                    if (task.getSubTasks().containsKey(taskId)) {
                        historyManager.addHistory(task.getSubTasks().get(taskId));
                        return task.getSubTasks().get(taskId);
                    }
                }
            }
        }
        return null; // maybe null
    }

    @Override
    public void newTask(String taskTitle, String taskDescription) {
        SimpleTask newTask = new SimpleTask(taskTitle, taskDescription);
        tasks.put(newTask.getTaskIdNumber(), newTask);
    }

    @Override
    public void newEpic(String taskTitle, String taskDescription) {
        EpicTask newEpic = new EpicTask(taskTitle, taskDescription);
        tasks.put(newEpic.getTaskIdNumber(), newEpic);
    }

    @Override
    public void newSubtask(int epicId, String taskTitle, String taskDescription) {
        if (tasks.containsKey(epicId) && tasks.get(epicId).getClass().equals(EpicTask.class)) {
            EpicTask task = (EpicTask) tasks.get(epicId);
            task.addSubTask(new Subtask(taskTitle, taskDescription));
        }
    }

    @Override
    public void updateTask(int taskId, String taskTitle, String taskDescription, TaskStatus taskStatus) {
        if (tasks.containsKey(taskId) && tasks.get(taskId) instanceof SimpleTask) {
            tasks.put(taskId, new SimpleTask(taskTitle, taskDescription, taskId, taskStatus));
        } else if (!tasks.containsKey(taskId)) {
            for (int taskID : tasks.keySet()) {
                if (tasks.get(taskID) instanceof EpicTask) {
                    EpicTask task = (EpicTask) tasks.get(taskID);
                    if (task.getSubTasks().containsKey(taskId)) {
                        task.addSubTask(new Subtask(taskTitle, taskDescription, taskId, taskStatus));
                    }
                }
            }
        }
    }

    @Override
    public void updateTask(int epicId, String taskTitle, String taskDescription, boolean saveSubTasks) {
        if (saveSubTasks) {
            if (tasks.get(epicId) instanceof EpicTask) {
                EpicTask newEpic = new EpicTask(taskTitle, taskDescription, epicId);
                EpicTask task = (EpicTask) tasks.get(epicId);
                tasks.put(epicId, newEpic);
                for (int subTaskID : task.getSubTasks().keySet()) {
                    newEpic.addSubTask(task.getSubTasks().get(subTaskID));
                }
            }
        } else if (tasks.get(epicId) instanceof EpicTask) {
            tasks.put(epicId, new EpicTask(taskTitle, taskDescription, epicId));
        }
    }

    @Override
    public void removeTaskById(int taskId) {
        tasks.remove(taskId);
        if (tasks.remove(taskId) == null) {
            for (int epicID : tasks.keySet()) {
                if (tasks.get(epicID) instanceof EpicTask) {
                    EpicTask task = (EpicTask) tasks.get(epicID);
                    task.removeSubTask(taskId);
                }
            }
        }
    }

    @Override
    public HashMap<Integer, Subtask> getSubTasksOfEpicById(int epicId) {
        if (tasks.get(epicId) instanceof EpicTask) {
            EpicTask task = (EpicTask) tasks.get(epicId);
            return task.getSubTasks();
        } else return null; // maybe null
    }

    @Override
    public Integer getTaskIdByName(String name) {
        if (!tasks.isEmpty()) {
            for (int taskID : tasks.keySet()) {
                if (tasks.get(taskID).getTaskTitle().equals(name)) {
                    return taskID;
                } else if (tasks.get(taskID) instanceof EpicTask) {
                    EpicTask task = (EpicTask) tasks.get(taskID);
                    for (int subTaskID : task.getSubTasks().keySet()) {
                        if (task.getSubTasks().get(subTaskID).getTaskTitle().equals(name)) {
                            return subTaskID;
                        }
                    }
                }
            }
        }
        return null; // maybe null
    }

    @Override
    public Collection<Task> getHistory() {
        return historyManager.getHistory();
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks; // maybe null
    }
}