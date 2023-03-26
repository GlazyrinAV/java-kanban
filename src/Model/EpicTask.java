package Model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class EpicTask extends Task {

    private final HashMap<Integer, Subtask> subTasks = new HashMap<>();

    /**
     * Конструктор для создания новых эпиков.
     * Присваивается новый порядковый номер
     *
     * @param task - объект класса NewTask для создания новых задач
     */
    public EpicTask(NewTask task) {
        super(task);
        this.taskType = TaskType.EPIC;
        this.startTime = null;
        this.duration = 0;
        updateStatus();
    }

    /**
     * Конструктор для обновления эпика
     * Номер остается прежним и указывается при обновлении
     */
    public EpicTask(Task epic) {
        super(epic);
        taskType = TaskType.EPIC;
        updateStatus();
        setEpicTimeAndDuration();
    }


    public EpicTask(String taskTitle, String taskDescription, TaskStatus taskStatus, int taskIdNumber,
                    LocalDateTime startTime, long duration, TaskType taskType) {
        super(taskTitle, taskDescription, taskStatus, taskIdNumber, startTime, duration, taskType);
    }

    public void addSubTask(int subTaskId, Subtask task) {
        subTasks.put(subTaskId, task);
        updateStatus();
        setEpicTimeAndDuration();
    }

    public void removeSubTask(int subTaskId) {
        subTasks.remove(subTaskId);
        updateStatus();
        setEpicTimeAndDuration();
    }

    /**
     * Возвращает лист с подзадачами входящими в эпик
     * @return - копия листа с подзадачами
     */
    public List<Integer> getSubTasksIds() {
        return new ArrayList<>(subTasks.keySet());
    }

    /**
     * Возвращает лист с уникальными статусами подзадач, входящих в эпик
     * @return - лист с уникальными статусами подзадач
     */
    private LinkedHashSet<TaskStatus> getSubTasksStatuses() {
        LinkedHashSet<TaskStatus> subTasksStatuses = new LinkedHashSet<>();
        for (Task subTask : subTasks.values()) {
            subTasksStatuses.add(subTask.getTaskStatus());
        }
        return subTasksStatuses;
    }

    /**
     * Определяет статус эпика через проверку статусов подзадач, хранящихся в TaskManager
     */
    private void updateStatus() {
        if (getSubTasksIds().isEmpty()) {
            setTaskStatus(TaskStatus.NEW);
        } else {

            if (getSubTasksStatuses().size() == 1) {
                for (TaskStatus status : getSubTasksStatuses()) setTaskStatus(status);
            } else setTaskStatus(TaskStatus.IN_PROGRESS);
        }
    }

    private void setEpicTimeAndDuration() {
        startTime = calculateStarTime();
        duration = calculateDuration();
    }

    @Override
    protected LocalDateTime calculateEndTime() {
        TreeMap<LocalDateTime, Integer> subTasksStartTime = new TreeMap<>(Comparator.naturalOrder());
        HashMap<Integer, Long> subTasksDuration = new HashMap<>();
        for (Task subTask : subTasks.values()) {
            if (subTask.getStartTime() != null) {
                subTasksStartTime.put(subTask.getStartTime(), subTask.getTaskIdNumber());
                subTasksDuration.put(subTask.getTaskIdNumber(), subTask.getDuration());
            }
        }
        if (subTasksDuration.isEmpty()) {
            return null;
        }
        long duration = subTasksDuration.get(subTasksStartTime.get(subTasksStartTime.lastKey()));
        return subTasksStartTime.lastKey().plusMinutes(duration);
    }

    private LocalDateTime calculateStarTime() {
        TreeMap<LocalDateTime, Integer> subTasksStartTime = new TreeMap<>(Comparator.naturalOrder());
        for (Task subTask : subTasks.values()) {
            if (subTask.getStartTime() != null) {
                subTasksStartTime.put(subTask.getStartTime(), subTask.getTaskIdNumber());
            }
        }
        if (subTasksStartTime.isEmpty()) {
            return null;
        }
        return subTasksStartTime.firstKey();
    }

    @SuppressWarnings("DataFlowIssue")
    private long calculateDuration() {
        if (calculateStarTime() == null || calculateEndTime() == null) {
            return 0;
        }
        return Duration.between(calculateStarTime(), calculateEndTime()).toMinutes();
    }

    @Override
    public String toString() {
        String result = "\n№" + getTaskIdNumber() + ". Эпик" +
                ". Название задачи - " + getTaskTitle() +
                ". Описание задачи: " + getTaskDescription() +
                ". Статус задачи: " + getTaskStatus() +
                ". Время начала: " + getStartTime() +
                ". Продолжительность: " + getDuration() +
                ". Время окончания: " + calculateEndTime();
        if (subTasks.isEmpty()) {
            result = result + ". Подзадачи отсутствуют.";
        } else {
            result = result + ". Эпик содержит следующие подзадачи: \n" + Arrays.toString(getSubTasksIds().toArray());
        }
        return  result;
    }
}