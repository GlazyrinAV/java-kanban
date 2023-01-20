package Manager;
import java.util.*;

import Model.*;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final InMemoryHistoryManager historyManager;

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
        newEpic.updateStatus(this, newEpic.getTaskIdNumber());
    }

    @Override
    public void newSubtask(String taskTitle, String taskDescription, int epicId) {
        if (tasks.containsKey(epicId) && isEpic(epicId)) {
            Subtask newSubtask = new Subtask(taskTitle, taskDescription, epicId);
            tasks.put(newSubtask.getTaskIdNumber(), newSubtask);
            getEpicByEpicId(epicId).addSubTask(newSubtask.getTaskIdNumber());
            getEpicByEpicId(epicId).updateStatus(this, epicId);
        }
    }

    @Override
    public void updateTask(int taskId, String taskTitle, String taskDescription, TaskStatus taskStatus) {
        if (tasks.containsKey(taskId) && isSimpleTask(taskId)) {
            tasks.put(taskId, new SimpleTask(taskTitle, taskDescription, taskId, taskStatus));
        } else if (tasks.containsKey(taskId) && isSubTask(taskId)) {
            int epicId = getEpicBySubtaskId(taskId).getTaskIdNumber();
            tasks.put(taskId, new Subtask(taskTitle, taskDescription, taskId, taskStatus, epicId));
            getEpicByEpicId(epicId).updateStatus(this, epicId);
        }
    }

    @Override
    public void updateTask(int epicId, String taskTitle, String taskDescription, boolean saveSubTasks) {
        if (saveSubTasks) {
            if (isEpic(epicId)) {
                EpicTask newEpic = new EpicTask(taskTitle, taskDescription, epicId);
                for (int subTaskId : getEpicByEpicId(epicId).getSubTasks()) {
                    newEpic.addSubTask(subTaskId);
                }
                newEpic.updateStatus(this, epicId);
                tasks.put(epicId, newEpic);
            }
        } else if (isEpic(epicId)) {
            tasks.put(epicId, new EpicTask(taskTitle, taskDescription, epicId));
            getEpicByEpicId(epicId).updateStatus(this, epicId);
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
    public Task getTaskById (int taskId) {
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
            getEpicBySubtaskId(taskId).updateStatus(this, ((Subtask) tasks.get(taskId)).getEpicId());
            tasks.remove(taskId);
        } else if (isEpic(taskId)) {
            for (int subTaskId : getEpicByEpicId(taskId).getSubTasks()) {
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
            return getEpicByEpicId(epicId).getSubTasks();
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
    public TaskStatus defineStatus(int epicID) {
        if (isEpic(epicID)) {
            if (getEpicByEpicId(epicID).getSubTasks().isEmpty()) {
                return TaskStatus.NEW;
            } else {
                Set<TaskStatus> epicStatuses = new LinkedHashSet<>();
                for (Integer subTaskId : getEpicByEpicId(epicID).getSubTasks()) {
                    epicStatuses.add(tasks.get(subTaskId).getTaskStatus());
                }
                if (epicStatuses.size() == 1) {
                    for (TaskStatus status : epicStatuses) return status;
                } else return TaskStatus.IN_PROGRESS;
            }
        }
        return null;
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

    private EpicTask getEpicBySubtaskId (int subTaskId) {
        return (EpicTask) tasks.get(((Subtask) tasks.get(subTaskId)).getEpicId());
    }

    private EpicTask getEpicByEpicId (int epicTaskId) {
        return (EpicTask) tasks.get(epicTaskId);
    }

    @Override
    public Collection<Task> getHistory() {
        return historyManager.getHistory();
    }
}