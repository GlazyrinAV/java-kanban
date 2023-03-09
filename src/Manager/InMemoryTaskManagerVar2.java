package Manager;

import Exceptions.ManagerExceptions;
import Model.Task;
import Utils.TimeLineChecker;

import java.time.LocalDateTime;

public class InMemoryTaskManagerVar2 extends InMemoryTaskManager {
    TimeLineChecker checker;

    /**
     * Конструктор менеджера задач, в который необходимо передавать объект менеджер историй просмотра
     * @param history - объект класса менеджер историй просмотра
     */
    public InMemoryTaskManagerVar2(InMemoryHistoryManager history) {
        super(history);
        checker = new TimeLineChecker(15);
    }

    @Override
    protected void addTaskToPrioritizedTasks(Task task) throws ManagerExceptions.TaskTimeOverlayException {
        LocalDateTime start = task.getStartTime();
        LocalDateTime end = task.getEndTime();
        if (start == null || end == null) {
            prioritizedTasks.add(task.getTaskIdNumber());
        } else if (prioritizedTasks.isEmpty()) {
            checker.checkTask(task);
            prioritizedTasks.add(task.getTaskIdNumber());
        } else if (checker.checkTask(task)) {
            prioritizedTasks.add(task.getTaskIdNumber());
        } else {
            throw new ManagerExceptions.TaskTimeOverlayException(
                    "Время выполнения задачи " + task.getTaskIdNumber() +
                            " пересекается со сроками других задач.");
        }
    }

    @Override
    public Task removeTaskById(int taskId) {
        if (tasks.containsKey(taskId)) {
            if (isSubTask(taskId)) {
                prioritizedTasks.remove(taskId);
                getEpicBySubtaskId(taskId).removeSubTask(taskId);
                checker.removeTimeNode(tasks.get(taskId));
                return tasks.remove(taskId);
            } else if (isEpic(taskId)) {
                for (int subTaskId : getEpicByEpicId(taskId).getSubTasksIds()) {
                    prioritizedTasks.remove(subTaskId);
                    checker.removeTimeNode(tasks.get(subTaskId));
                    tasks.remove(subTaskId);
                }
                return tasks.remove(taskId);
            } else {
                prioritizedTasks.remove(taskId);
                checker.removeTimeNode(tasks.get(taskId));
                return tasks.remove(taskId);
            }
        }
        return null;
    }
}