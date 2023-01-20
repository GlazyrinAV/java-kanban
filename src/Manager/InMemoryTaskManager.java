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
        if (tasks.containsKey(epicId) && tasks.get(epicId).getClass().equals(EpicTask.class)) {
            Subtask newSubtask = new Subtask(taskTitle, taskDescription, epicId);
            tasks.put(newSubtask.getTaskIdNumber(), newSubtask);
            ((EpicTask) tasks.get(epicId)).addSubTask(newSubtask.getTaskIdNumber());
            ((EpicTask) tasks.get(epicId)).updateStatus(this, epicId);
        }
    }

    @Override
    public void updateTask(int taskId, String taskTitle, String taskDescription, TaskStatus taskStatus) {
        if (tasks.containsKey(taskId) && tasks.get(taskId) instanceof SimpleTask) {
            tasks.put(taskId, new SimpleTask(taskTitle, taskDescription, taskId, taskStatus));
        } else if (tasks.containsKey(taskId) && tasks.get(taskId) instanceof Subtask) {
            int epicId = ((Subtask) tasks.get(taskId)).getEpicId();
            tasks.put(taskId, new Subtask(taskTitle, taskDescription, taskId, taskStatus, epicId));
            ((EpicTask) tasks.get(epicId)).updateStatus(this, epicId);
        }
    }

    @Override
    public void updateTask(int epicId, String taskTitle, String taskDescription, boolean saveSubTasks) {
        if (saveSubTasks) {
            if (tasks.get(epicId) instanceof EpicTask) {
                EpicTask newEpic = new EpicTask(taskTitle, taskDescription, epicId);
                for (int subTaskId : ((EpicTask) tasks.get(epicId)).getSubTasks()) {
                    newEpic.addSubTask(subTaskId);
                }
                newEpic.updateStatus(this, epicId);
                tasks.put(epicId, newEpic);
            }
        } else if (tasks.get(epicId) instanceof EpicTask) {
            tasks.put(epicId, new EpicTask(taskTitle, taskDescription, epicId));
            ((EpicTask) tasks.get(epicId)).updateStatus(this, epicId);
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
        if (tasks.get(taskId) instanceof Subtask) {
            EpicTask task = (EpicTask) tasks.get(((Subtask) tasks.get(taskId)).getEpicId());
            task.removeSubTask(taskId);
            task.updateStatus(this, ((Subtask) tasks.get(taskId)).getEpicId());
            tasks.remove(taskId);
        } else if (tasks.get(taskId) instanceof EpicTask) {
            for (int subTaskId : ((EpicTask) tasks.get(taskId)).getSubTasks()) {
                tasks.remove(subTaskId);
            }
            tasks.remove(taskId);
        } else {
            tasks.remove(taskId);
        }
    }

    @Override
    public List<Integer> getSubTasksOfEpicById(int epicId) {
        if (tasks.get(epicId) instanceof EpicTask) {
            return ((EpicTask) tasks.get(epicId)).getSubTasks();
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
        if (tasks.get(epicID) instanceof EpicTask) {
            if (((EpicTask) tasks.get(epicID)).getSubTasks().isEmpty()) {
                return TaskStatus.NEW;
            } else {
                Set<TaskStatus> epicStatuses = new LinkedHashSet<>();
                for (Integer subTaskId : ((EpicTask) tasks.get(epicID)).getSubTasks()) {
                    epicStatuses.add(tasks.get(subTaskId).getTaskStatus());
                }
                if (epicStatuses.size() == 1) {
                    for (TaskStatus status : epicStatuses) return status;
                } else return TaskStatus.IN_PROGRESS;
            }
        }
        return null;
    }

    @Override
    public Collection<Task> getHistory() {
        return historyManager.getHistory();
    }
}