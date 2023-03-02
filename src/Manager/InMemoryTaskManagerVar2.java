package Manager;

import Exceptions.ManagerExceptions;
import Model.Task;
import Utils.TimeLineChecker;

import java.time.LocalDateTime;

public class InMemoryTaskManagerVar2 extends InMemoryTaskManager {
    TimeLineChecker checker;

    /**
     * Конструктор менеджера задач, в который необходимо передавать объект менеджер историй просмотра
     *
     * @param history - объект класса менеджер историй просмотра
     */
    public InMemoryTaskManagerVar2(InMemoryHistoryManager history) {
        super(history);
        checker = new TimeLineChecker(30);
    }

    @Override
    protected void addTaskToPrioritizedTasks(Task task) throws ManagerExceptions.TaskTimeOverlayException {
        LocalDateTime start = task.getStartTime();
        LocalDateTime end = task.getEndTime();
        if (start == null || end == null || prioritizedTasks.isEmpty()) {
            checker.checkTask(task);
            taskTimeAdder(task);
        } else if (checker.checkTask(task)) {
            taskTimeAdder(task);
        } else {
            throw new ManagerExceptions.TaskTimeOverlayException(
                    "Время выполнения задачи " + task.getTaskIdNumber() +
                            " пересекается со сроками других задач.");
        }
    }
}