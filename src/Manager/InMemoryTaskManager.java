package Manager;

import Exceptions.ManagerExceptions;
import Model.*;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final InMemoryHistoryManager historyManager;

    protected final TreeSet<Integer> prioritizedTasks;
    final Comparator<Integer> timeComparator = (o1, o2) -> {
        if (tasks.get(o1).getStartTime() == null) return 1;
        if (tasks.get(o2).getStartTime() == null) return -1;
        return tasks.get(o1).getStartTime().compareTo(tasks.get(o2).getStartTime());
    };

    /**
     * Конструктор менеджера задач, в который необходимо передавать объект менеджер историй просмотра
     *
     * @param history - объект класса менеджер историй просмотра
     */
    public InMemoryTaskManager(InMemoryHistoryManager history) {
        historyManager = history;
        prioritizedTasks = new TreeSet<>(timeComparator);
    }

    @Override
    public void newSimpleTask(NewTask task) {
        SimpleTask newTask = new SimpleTask(new NewTask(task.getTaskTitle(), task.getTaskDescription(), task.getStartTime(), task.getDuration()));
        tasks.put(newTask.getTaskIdNumber(), newTask);
        addTaskToPrioritizedTasks(newTask);
    }

    @Override
    public void newEpic(NewTask task) {
        EpicTask newEpic = new EpicTask(new NewTask(task.getTaskTitle(), task.getTaskDescription()));
        tasks.put(newEpic.getTaskIdNumber(), newEpic);
    }

    @Override
    public void newSubtask(NewTask task, int epicId)
            throws ManagerExceptions.NoSuchEpicException, ManagerExceptions.TaskIsNotEpicException {
        if (tasks.containsKey(epicId) && isEpic(epicId)) {
            Subtask newSubtask = new Subtask(new NewTask(task.getTaskTitle(), task.getTaskDescription(), task.getStartTime(), task.getDuration()), epicId);
            tasks.put(newSubtask.getTaskIdNumber(), newSubtask);
            addTaskToPrioritizedTasks(newSubtask);
            getEpicByEpicId(epicId).addSubTask(newSubtask.getTaskIdNumber(), newSubtask.getTaskStatus(), newSubtask.getStartTime(), newSubtask.getDuration());
        } else if (!tasks.containsKey(epicId)) {
            throw new ManagerExceptions.NoSuchEpicException("Эпика с номером " + epicId + " не существует.");
        } else if (!isEpic(epicId)) {
            throw new ManagerExceptions.TaskIsNotEpicException("Задача " + epicId + " не является эпиком.");
        }
    }

    @Override
    public void updateTask(int taskId, TaskStatus taskStatus) throws ManagerExceptions.NoSuchTasksException {
        if (tasks.containsKey(taskId) && isSimpleTask(taskId)) {
            tasks.put(taskId, new SimpleTask(tasks.get(taskId), taskStatus));
        } else if (tasks.containsKey(taskId) && isSubTask(taskId)) {
            tasks.put(taskId, new Subtask(tasks.get(taskId), taskStatus));
            getEpicBySubtaskId(taskId).addSubTask(taskId, taskStatus, tasks.get(taskId).getStartTime(), tasks.get(taskId).getDuration());
        } else if (!tasks.containsKey(taskId)) {
            throw new ManagerExceptions.NoSuchTasksException
                    ("При обновлении задача с номером " + taskId + " не найдена.");
        } else if (isEpic(taskId)) {
            throw new ManagerExceptions.NoSuchTasksException("Невозможно обновить статус задачи " + taskId +
                    ", т.к. данная задача является эпиком.");
        }
    }

    @Override
    public void updateTask(int epicId, boolean saveSubTasks) {
        if (saveSubTasks) {
            if (isEpic(epicId)) {
                EpicTask newEpic = new EpicTask(tasks.get(epicId));
                for (int subTaskId : getEpicByEpicId(epicId).getSubTasksIds()) {
                    newEpic.addSubTask(subTaskId, tasks.get(subTaskId).getTaskStatus(), tasks.get(subTaskId).getStartTime(), tasks.get(subTaskId).getDuration());
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
        prioritizedTasks.clear();
        tasks.clear();
    }

    @Override
    public Task getTaskById(int taskId) {
        if (tasks.containsKey(taskId)) {
            historyManager.addHistory(taskId);
            return tasks.get(taskId);
        }
        return null; // maybe null
    }

    @Override
    public Task removeTaskById(int taskId) {
        if (tasks.containsKey(taskId)) {
            if (isSubTask(taskId)) {
                prioritizedTasks.remove(taskId);
                getEpicBySubtaskId(taskId).removeSubTask(taskId);
                return tasks.remove(taskId);
            } else if (isEpic(taskId)) {
                for (int subTaskId : getEpicByEpicId(taskId).getSubTasksIds()) {
                    tasks.remove(subTaskId);
                }
                return tasks.remove(taskId);
            } else {
                prioritizedTasks.remove(taskId);
                return tasks.remove(taskId);
            }
        }
        return null;
    }

    @Override
    public Set<Integer> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    private void addTaskToPrioritizedTasks(Task task) throws ManagerExceptions.TaskTimeOverlayException {
        LocalDateTime start = task.getStartTime();
        LocalDateTime end = task.getEndTime();
        if (start == null || end == null) prioritizedTasks.add(task.getTaskIdNumber());
        else if (!prioritizedTasks.isEmpty()) {
            TreeSet<Integer> testingSet = new TreeSet<>(prioritizedTasks);
            for (int taskId : testingSet) {
                LocalDateTime start2 = tasks.get(taskId).getStartTime();
                LocalDateTime end2 = tasks.get(taskId).getEndTime();
                if (start2 == null || end2 == null) continue;
                if ((start.isBefore(start2) || start.isAfter(end2)) && (end.isBefore(start2) || end.isAfter(end2)))
                    prioritizedTasks.add(task.getTaskIdNumber());
                else throw new ManagerExceptions.TaskTimeOverlayException(
                        "Время выполнения задачи " + task.getTaskIdNumber() +
                                " пересекается со сроками задачи " + taskId + ".");
            }
        } else prioritizedTasks.add(task.getTaskIdNumber());
    }

    public List<Integer> getSubTasksOfEpicById(int epicId) {
        if (isEpic(epicId)) {
            return getEpicByEpicId(epicId).getSubTasksIds();
        } else return null; // maybe null
    }

    public Integer getTaskIdByName(String name) {
        if (!tasks.isEmpty()) {
            for (int taskID : tasks.keySet()) {
                if (tasks.get(taskID).getTaskTitle().equals(name)) {
//                    historyManager.addHistory(taskID);
                    return taskID;
                }
            }
        }
        return null; // maybe null
    }

    @Override
    public Collection<Integer> getHistory() {
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