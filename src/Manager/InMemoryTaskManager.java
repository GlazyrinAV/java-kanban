package Manager;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

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
                EpicTask task = (EpicTask) tasks.get(epicId);
                for (int subTaskId : task.getSubTasks()) {
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
            EpicTask task = (EpicTask) tasks.get(taskId);
            for (int subTaskId : task.getSubTasks()) {
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
                }
            }
        }
        return null; // maybe null
    }

    @Override
    public TaskStatus defineStatus(int epicID) {
        if (tasks.get(epicID) instanceof EpicTask) {
            EpicTask task = (EpicTask) tasks.get(epicID);
            int sum = 0;
            int sumIfAllDone = task.getSubTasks().size() * 2; // при выполнении всех задач, sum равна удвоенному количеству подзадач
            if (task.getSubTasks().isEmpty()) {
                return TaskStatus.NEW;
            } else {
                for (Integer subTaskId : task.getSubTasks()) {
                    TaskStatus taskStatus = tasks.get(subTaskId).getTaskStatus();
                    switch (taskStatus) {
                        case NEW: {
                            sum += 0;
                            break;
                        }
                        case IN_PROGRESS: {
                            sum += 1;
                            break;
                        }
                        case DONE: {
                            sum += 2;
                            break;
                        }
                    }
                }
                if (sum == 0) return TaskStatus.NEW;
                else if (sum == sumIfAllDone) return TaskStatus.DONE;
                else return TaskStatus.IN_PROGRESS;
            }
        }
        return null;
    }

    @Override
    public Collection<Task> getHistory() {
        return historyManager.getHistory();
    }
}