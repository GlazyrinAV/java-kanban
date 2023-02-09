package Manager;

import Model.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final InMemoryHistoryManager historyManager;

    /**
     * Конструктор менеджера тасков, в который необходимо передавать объект менеджер историй просмотра
     * @param history - объект классса менеджер историй просмотра
     */
    public InMemoryTaskManager(InMemoryHistoryManager history) {
        historyManager = history;
    }

    @Override
    public void newSimpleTask(String taskTitle, String taskDescription) {
        SimpleTask newTask = new SimpleTask(taskTitle, taskDescription);
        tasks.put(newTask.getTaskIdNumber(), newTask);
    }

    @Override
    public void newEpic(String taskTitle, String taskDescription) {
        EpicTask newEpic = new EpicTask(taskTitle, taskDescription);
        tasks.put(newEpic.getTaskIdNumber(), newEpic);
    }

    @Override
    public void newSubtask(String taskTitle, String taskDescription, int epicId) {
        if (tasks.containsKey(epicId) && isEpic(epicId)) {
            Subtask newSubtask = new Subtask(taskTitle, taskDescription, epicId);
            tasks.put(newSubtask.getTaskIdNumber(), newSubtask);
            getEpicByEpicId(epicId).addSubTask(newSubtask.getTaskIdNumber(), newSubtask.getTaskStatus());
        }
    }

    @Override
    public void updateTask(int taskId, TaskStatus taskStatus) {
        if (tasks.containsKey(taskId) && isSimpleTask(taskId)) {
            tasks.put(taskId, new SimpleTask(tasks.get(taskId), taskStatus));
        } else if (tasks.containsKey(taskId) && isSubTask(taskId)) {
            tasks.put(taskId, new Subtask(tasks.get(taskId), taskStatus));
            getEpicBySubtaskId(taskId).addSubTask(taskId, taskStatus);
        }
    }

    @Override
    public void updateTask(int epicId, boolean saveSubTasks) {
        if (saveSubTasks) {
            if (isEpic(epicId)) {
                EpicTask newEpic = new EpicTask(tasks.get(epicId));
                for (int subTaskId : getEpicByEpicId(epicId).getSubTasksIds()) {
                    newEpic.addSubTask(subTaskId, tasks.get(subTaskId).getTaskStatus());
                }
                tasks.put(epicId, newEpic);
            }
        } else if (isEpic(epicId)) {
            for (int subTaskId : getEpicByEpicId(epicId).getSubTasksIds()) {
                tasks.remove(subTaskId);
            }
            tasks.put(epicId, new EpicTask(tasks.get(epicId)));
        }
    }

    @Override
    public HashMap<Integer, Task> getAllTasks() {
        return tasks; // maybe null
    }

    @Override
    public void clearAllTasks() {
        tasks.clear();
    }

    @Override
    public Task getTaskById(int taskId) {
        if (tasks.containsKey(taskId)) {
            historyManager.addHistory(tasks.get(taskId));
            return tasks.get(taskId);
        }
        return null; // maybe null
    }

    @Override
    public void removeTaskById(int taskId) {
        if (isSubTask(taskId)) {
            getEpicBySubtaskId(taskId).removeSubTask(taskId);
            tasks.remove(taskId);
        } else if (isEpic(taskId)) {
            for (int subTaskId : getEpicByEpicId(taskId).getSubTasksIds()) {
                tasks.remove(subTaskId);
            }
            tasks.remove(taskId);
        } else {
            tasks.remove(taskId);
        }
    }

    @Override
    public List<Integer> getSubTasksOfEpicById(int epicId) {
        if (isEpic(epicId)) {
            return getEpicByEpicId(epicId).getSubTasksIds();
        } else return null; // maybe null
    }

    @Override
    public Integer getTaskIdByName(String name) {
        if (!tasks.isEmpty()) {
            for (int taskID : tasks.keySet()) {
                if (tasks.get(taskID).getTaskTitle().equals(name)) {
                    return taskID;
                }
            }
        }
        return null; // maybe null
    }

    @Override
    public Collection<Task> getHistory() {
        return historyManager.getHistory();
    }

    private boolean isEpic(int taskID) {
        return tasks.get(taskID) instanceof EpicTask;
    }

    private boolean isSimpleTask(int taskID) {
        return tasks.get(taskID) instanceof SimpleTask;
    }

    private boolean isSubTask(int taskID) {
        return tasks.get(taskID) instanceof Subtask;
    }

    private EpicTask getEpicBySubtaskId(int subTaskId) {
        return (EpicTask) tasks.get(((Subtask) tasks.get(subTaskId)).getEpicId());
    }

    private EpicTask getEpicByEpicId(int epicTaskId) {
        return (EpicTask) tasks.get(epicTaskId);
    }
}