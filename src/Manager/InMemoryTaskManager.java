package Manager;

import Exceptions.ManagerExceptions;
import Model.*;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final InMemoryHistoryManager historyManager;
    protected final TreeSet<Integer> prioritizedTasks;
    private final TreeMap<LocalDateTime, Task> taskTimeLine;

    final Comparator<Integer> timeComparator = (o1, o2) -> {
        if (tasks.get(o1).getStartTime() == null) {
            return 1;
        }
        if (tasks.get(o2).getStartTime() == null) {
            return -1;
        } else {
            return tasks.get(o1).getStartTime().compareTo(tasks.get(o2).getStartTime());
        }
    };

    /**
     * Конструктор менеджера задач, в который необходимо передавать объект менеджер историй просмотра
     * @param history - объект класса менеджер историй просмотра
     */
    public InMemoryTaskManager(InMemoryHistoryManager history) {
        historyManager = history;
        prioritizedTasks = new TreeSet<>(timeComparator);
        taskTimeLine = new TreeMap<>(Comparator.nullsLast(Comparator.naturalOrder()));
    }

    @Override
    public void newSimpleTask(NewTask task) {
        String title = task.getTaskTitle();
        String description = task.getTaskDescription();
        LocalDateTime start = task.getStartTime();
        long duration = task.getDuration();
        SimpleTask newTask = new SimpleTask(new NewTask(title, description, start, duration));
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
            String title = task.getTaskTitle();
            String description = task.getTaskDescription();
            LocalDateTime start = task.getStartTime();
            long duration = task.getDuration();
            Subtask newSubtask = new Subtask(new NewTask(title, description, start, duration), epicId);
            tasks.put(newSubtask.getTaskIdNumber(), newSubtask);
            addTaskToPrioritizedTasks(newSubtask);
            getEpicByEpicId(epicId).addSubTask(newSubtask.getTaskIdNumber(), newSubtask.getTaskStatus(), start, duration);
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
            Task task = tasks.get(taskId);
            getEpicBySubtaskId(taskId).addSubTask(taskId, taskStatus, task.getStartTime(), task.getDuration());
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
                    Task subTask = tasks.get(subTaskId);
                    newEpic.addSubTask(subTaskId, subTask.getTaskStatus(), subTask.getStartTime(), subTask.getDuration());
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
                removeTaskTime(taskId);
                getEpicBySubtaskId(taskId).removeSubTask(taskId);
                return tasks.remove(taskId);
            } else if (isEpic(taskId)) {
                for (int subTaskId : getEpicByEpicId(taskId).getSubTasksIds()) {
                    removeTaskTime(taskId);
                    tasks.remove(subTaskId);
                }
                return tasks.remove(taskId);
            } else {
                removeTaskTime(taskId);
                return tasks.remove(taskId);
            }
        }
        return null;
    }

    @Override
    public Set<Integer> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    /**
     * Добавление задачи в отсортированный список задач
     *
     * @param task - добавляемая задача
     * @throws ManagerExceptions.TaskTimeOverlayException - исключение при наличии пересечений добавляемой задачи
     */
    protected void addTaskToPrioritizedTasks(Task task) throws ManagerExceptions.TaskTimeOverlayException {
        LocalDateTime start = task.getStartTime();
        LocalDateTime end = task.getEndTime();
        if (start == null || end == null || prioritizedTasks.isEmpty()) {
            addTaskTime(task);
        } else if (checkTimeOverlay(task)) {
            addTaskTime(task);
        } else {
            throw new ManagerExceptions.TaskTimeOverlayException(
                    "Время выполнения задачи " + task.getTaskIdNumber() +
                            " пересекается со сроками других задач.");
        }
    }

    private void addTaskTime(Task task) {
        prioritizedTasks.add(task.getTaskIdNumber());
        taskTimeLine.put(task.getStartTime(), task);
        taskTimeLine.put(task.getEndTime(), task);
    }

    private void removeTaskTime(int taskId) {
        prioritizedTasks.remove(taskId);
        taskTimeLine.remove(tasks.get(taskId).getStartTime());
        taskTimeLine.remove(tasks.get(taskId).getEndTime());
    }

    public List<Integer> getSubTasksOfEpicById(int epicId) {
        if (isEpic(epicId)) {
            return getEpicByEpicId(epicId).getSubTasksIds();
        } else {
            return null; // maybe null
        }
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

    private boolean checkTimeOverlay(Task task) {
        LocalDateTime start = task.getStartTime();
        LocalDateTime end = task.getEndTime();
        boolean start2;
        if (start == null || end == null) {
            return true;
        }
        if ((taskTimeLine.lowerKey(start) == null)) {
            start2 = true;
        } else {
            Task taskBefore = taskTimeLine.get(taskTimeLine.lowerKey(start));
            start2 = !taskBefore.getStartTime().equals(taskTimeLine.lowerKey(start));
        }
        boolean end2;
        if ((taskTimeLine.higherKey(start) == null)) {
            end2 = true;
        } else {
            end2 = end.isBefore(taskTimeLine.higherKey(start));
        }
        return start2 && end2;
    }

    @Override
    public Collection<Integer> getHistory() {
        return historyManager.getHistory();
    }

    protected boolean isEpic(int taskID) {
        return tasks.get(taskID) instanceof EpicTask;
    }

    protected boolean isSimpleTask(int taskID) {
        return tasks.get(taskID) instanceof SimpleTask;
    }

    protected boolean isSubTask(int taskID) {
        return tasks.get(taskID) instanceof Subtask;
    }

    protected EpicTask getEpicBySubtaskId(int subTaskId) {
        return (EpicTask) tasks.get(((Subtask) tasks.get(subTaskId)).getEpicId());
    }
    protected EpicTask getEpicByEpicId(int epicTaskId) {
        return (EpicTask) tasks.get(epicTaskId);
    }
}