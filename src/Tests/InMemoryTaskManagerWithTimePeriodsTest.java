package Tests;

import Manager.InMemoryHistoryManager;
import Manager.InMemoryTaskManagerWithTimePeriods;
import Manager.TaskManager;
import Model.NewTask;
import Model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;

public class InMemoryTaskManagerWithTimePeriodsTest extends TaskManagerTest<TaskManager> {

    @BeforeEach
    public void createTaskManager() {
        resetIdCounter();
        setManager(new InMemoryTaskManagerWithTimePeriods(new InMemoryHistoryManager()));
    }

    private void resetIdCounter() {
        Task.resetCounterForTest();
    }


    @Override
    @Test
    public void getEpicTimeDataWithSubTasks() {
        getTestManager().newEpic(new NewTask("1", "1"));
        getTestManager().newSubtask(new NewTask("2", "2",
                LocalDateTime.of(2022, Month.APRIL, 1, 0, 0), 10), 1);
        getTestManager().newSubtask(new NewTask("3", "3", null, 10), 1);
        getTestManager().newSubtask(new NewTask("4", "4",
                LocalDateTime.of(2022, Month.APRIL, 1, 0, 30), 10), 1);
        Assertions.assertTrue((getTestManager().getTaskById(1).getStartTime().equals
                        (LocalDateTime.of(2022, Month.APRIL, 1, 0, 0))) &&
                        (getTestManager().getTaskById(1).getDuration() == 40),
                "Ошибка в информации о времени для эпика c подзадачами.");
    }

    @Override
    @Test
    public void getEpicTimeDataAfterRemovingSubTask() {
        getTestManager().newEpic(new NewTask("1", "1"));
        getTestManager().newSubtask(new NewTask("2", "2",
                LocalDateTime.of(2022, Month.APRIL, 1, 0, 0), 10), 1);
        getTestManager().newSubtask(new NewTask("3", "3", null, 10), 1);
        getTestManager().newSubtask(new NewTask("4", "4",
                LocalDateTime.of(2022, Month.APRIL, 1, 0, 30), 10), 1);
        getTestManager().removeTaskById(4);
        Assertions.assertTrue((getTestManager().getTaskById(1).getStartTime().equals
                        (LocalDateTime.of(2022, Month.APRIL, 1, 0, 0))) &&
                        (getTestManager().getTaskById(1).getDuration() == 10),
                "Ошибка в информации о времени для эпика c подзадачами после удаления подзадачи.");
    }

    @Override
    @Test
    public void getPriorityWithTasks() {
        getTestManager().newSimpleTask(new NewTask("1", "1",
                LocalDateTime.of(2023, Month.APRIL, 1, 0, 0), 30));
        getTestManager().newSimpleTask(new NewTask("2", "2",
                LocalDateTime.of(2023, Month.APRIL, 3, 0, 10), 10));
        getTestManager().newSimpleTask(new NewTask("3", "1",
                LocalDateTime.of(2023, Month.APRIL, 2, 0, 0), 30));
        getTestManager().newSimpleTask(new NewTask("4", "2",
                LocalDateTime.of(2023, Month.APRIL, 4, 0, 10), 10));
        Assertions.assertEquals(getTestManager().getPrioritizedTasks().toString(), ("[1, 3, 2, 4]"),
                "Ошибка при получении приоритетов с задачами.");
    }
}