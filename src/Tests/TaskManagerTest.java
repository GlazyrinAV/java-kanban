package Tests;

import Exceptions.ManagerExceptions;
import Manager.TaskManager;
import Model.NewTask;
import Model.Subtask;
import Model.Task;
import Model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;

public abstract class TaskManagerTest<T extends TaskManager> {

    private T testManager;

    public void setManager(T t) {
        this.testManager = t;
    }

    public T getTestManager() {
        return testManager;
    }

    @DisplayName("SimpleTask Статус при создании")
    @Test
    public void simpleTaskStatusOnCreationIsNew() {
        testManager.newSimpleTask(new NewTask("1", "2"));
        Assertions.assertEquals(testManager.getTaskById(1).getTaskStatus(), TaskStatus.NEW,
                "Статус простой задачи установлен неверно.");
    }

    @DisplayName("SimpleTask Статус при обновлении")
    @Test
    public void simpleTaskStatusOnUpdateIsChanged() {
        testManager.newSimpleTask(new NewTask("1", "2"));
        testManager.updateTask(1, TaskStatus.DONE);
        Assertions.assertEquals(testManager.getTaskById(1).getTaskStatus(), TaskStatus.DONE,
                "Статус простой задачи при обновлении задан неверно.");
    }

    @DisplayName("EpicTask Статус при отсутствии подзадач")
    @Test
    public void epicTaskStatusWithNoSubTasks() {
        testManager.newEpic(new NewTask("1", "1"));
        Assertions.assertEquals(testManager.getTaskById(1).getTaskStatus(), TaskStatus.NEW,
                "Статус пустого эпика при создании задан неверно.");
    }

    @DisplayName("EpicTask Статус при новых подзадачах")
    @Test
    public void epicTaskStatusWhenSubTasksIsNew() {
        testManager.newEpic(new NewTask("1", "1"));
        testManager.newSubtask(new NewTask("2", "2"), 1);
        testManager.newSubtask(new NewTask("3", "3"), 1);
        Assertions.assertEquals(testManager.getTaskById(1).getTaskStatus(), TaskStatus.NEW,
                "Статус эпика при добавлении подзадач со статусом NEW задан неверно.");
    }

    @DisplayName("EpicTask Статус при выполненных подзадачах")
    @Test
    public void epicTaskStatusWhenSubTasksIsDone() {
        testManager.newEpic(new NewTask("1", "1"));
        testManager.newSubtask(new NewTask("2", "2"), 1);
        testManager.newSubtask(new NewTask("3", "3"), 1);
        testManager.updateTask(2, TaskStatus.DONE);
        testManager.updateTask(3, TaskStatus.DONE);
        Assertions.assertEquals(testManager.getTaskById(1).getTaskStatus(), TaskStatus.DONE,
                "Статус эпика при добавлении подзадач со статусом DONE задан неверно.");
    }

    @DisplayName("EpicTask Статус при смешенном статусе подзадач")
    @Test
    public void epicTaskStatusWhenSubTasksIsMixed() {
        testManager.newEpic(new NewTask("1", "1"));
        testManager.newSubtask(new NewTask("2", "2"), 1);
        testManager.newSubtask(new NewTask("3", "3"), 1);
        testManager.updateTask(2, TaskStatus.IN_PROGRESS);
        testManager.updateTask(3, TaskStatus.DONE);
        Assertions.assertEquals(testManager.getTaskById(1).getTaskStatus(), TaskStatus.IN_PROGRESS,
                "Статус эпика при добавлении подзадач со смешанным статусом задан неверно.");
    }

    @DisplayName("EpicTask Статус при выполняемых подзадачах")
    @Test
    public void epicTaskStatusWhenSubTasksIsInProgress() {
        testManager.newEpic(new NewTask("1", "1"));
        testManager.newSubtask(new NewTask("2", "2"), 1);
        testManager.newSubtask(new NewTask("3", "3"), 1);
        testManager.updateTask(2, TaskStatus.IN_PROGRESS);
        testManager.updateTask(3, TaskStatus.IN_PROGRESS);
        Assertions.assertEquals(testManager.getTaskById(1).getTaskStatus(), TaskStatus.IN_PROGRESS,
                "Статус эпика при добавлении подзадач со статусом IN_PROGRESS задан неверно.");
    }

    @DisplayName("SubTask Статус при создании")
    @Test
    public void subTasksStatusOnCreation() {
        testManager.newEpic(new NewTask("1", "1"));
        testManager.newSubtask(new NewTask("2", "2"), 1);
        Assertions.assertEquals(testManager.getTaskById(2).getTaskStatus(), TaskStatus.NEW,
                "Статус подзадачи при создании задан неверно.");
    }

    @DisplayName("SubTask Статус при обновлении")
    @Test
    public void subTasksStatusOnUpdate() {
        testManager.newEpic(new NewTask("1", "1"));
        testManager.newSubtask(new NewTask("2", "2"), 1);
        testManager.updateTask(2, TaskStatus.IN_PROGRESS);
        Assertions.assertEquals(testManager.getTaskById(2).getTaskStatus(), TaskStatus.IN_PROGRESS,
                "Статус подзадачи при обновлении задан неверно.");
    }

    @DisplayName("SubTask Наличие эпика")
    @Test
    public void subTaskHaveEpic() {
        testManager.newEpic(new NewTask("1", "1"));
        testManager.newSubtask(new NewTask("2", "2"), 1);
        int epicID = ((Subtask) testManager.getTaskById(2)).getEpicId();
        Assertions.assertEquals(epicID, 1,
                "Ошибка при добавлении подзадачи к существующему эпику.");
    }

    @DisplayName("SubTask Подзадача добавляется к несуществующему эпику")
    @Test
    public void subTaskHaveNoEpicException() {
        final ManagerExceptions.NoSuchEpicException exception =
                Assertions.assertThrows(ManagerExceptions.NoSuchEpicException.class, () -> {
                    testManager.newEpic(new NewTask("1", "1"));
                    testManager.newSubtask(new NewTask("2", "2"), 3);
                });
        Assertions.assertEquals(exception.getMessage(), "Эпика с номером 3 не существует.",
                "Ошибка при указании для подзадачи номера несуществующего эпика.");
    }

    @DisplayName("SubTask Подзадача добавляется к задаче, которая не является эпиком")
    @Test
    public void TaskIsNotEpicWhileAddingSubTaskException() {
        final ManagerExceptions.TaskIsNotEpicException exception =
                Assertions.assertThrows(ManagerExceptions.TaskIsNotEpicException.class, () -> {
                    testManager.newSimpleTask(new NewTask("1", "1"));
                    testManager.newSubtask(new NewTask("2", "2"), 1);
                });
        Assertions.assertEquals(exception.getMessage(), "Задача 1 не является эпиком.",
                "Ошибка при указании для подзадачи номера задачи, которая не является эпиком.");
    }

    @DisplayName("updateTask Обычное обновление задач")
    @Test
    public void updateTaskBase() {
        testManager.newSimpleTask(new NewTask("1", "1"));
        testManager.updateTask(1, TaskStatus.IN_PROGRESS);
        Assertions.assertEquals(testManager.getTaskById(1).getTaskStatus(), TaskStatus.IN_PROGRESS,
                "Неверно обновлен статус простой задачи.");
    }

    @DisplayName("updateTask Обновление задач с несуществующим номером задачи")
    @Test
    public void updateTaskWithNoTasksException() {
        final ManagerExceptions.NoSuchTasksException exception =
                Assertions.assertThrows(ManagerExceptions.NoSuchTasksException.class, () -> {
                    testManager.newSimpleTask(new NewTask("1", "1"));
                    testManager.updateTask(2, TaskStatus.IN_PROGRESS);
                });
        Assertions.assertEquals(exception.getMessage(), "При обновлении задача с номером 2 не найдена.",
                "Ошибка обновлении задачи по несуществующему номеру.");
    }

    @DisplayName("updateTask Обновление задач с неверным номером задачи")
    @Test
    public void updateTasksWithWrongIdException() {
        final ManagerExceptions.NoSuchTasksException exception =
                Assertions.assertThrows(ManagerExceptions.NoSuchTasksException.class, () -> {
                    testManager.newEpic(new NewTask("1", "1"));
                    testManager.updateTask(1, TaskStatus.DONE);
                });
        Assertions.assertEquals(exception.getMessage(),
                "Невозможно обновить статус задачи 1, т.к. данная задача является эпиком.",
                "Ошибка при попытке прямого обновлении статуса эпика.");
    }

    @DisplayName("getAllTasks Обычное поведение")
    @Test
    public void getAllTasksBase() {
        testManager.newSimpleTask(new NewTask("1", "1"));
        Assertions.assertEquals(testManager.getAllTasks().values().toString(),
                "[№1. Задача. Название задачи - 1. Описание задачи: 1. Статус задачи: NEW. " +
                        "Время начала: null. Продолжительность: 0. Время окончания: null]",
                "Ошибка при получении списка всех задач");
    }

    @DisplayName("getAllTasks При пустом списке задач")
    @Test
    public void getAllTasksWithNoTasks() {
        Assertions.assertEquals(testManager.getAllTasks(), new HashMap<>(),
                "Ошибка при получении списка всех задач при отсутствии задач.");
    }

    @DisplayName("clearAllTasks Обычное поведение")
    @Test
    public void clearAllTasksBase() {
        testManager.newSimpleTask(new NewTask("1", "1"));
        testManager.clearAllTasks();
        Assertions.assertEquals(testManager.getAllTasks(), new HashMap<>(),
                "Ошибка при очистке списка существующих задач.");
    }

    @DisplayName("clearAllTasks При пустом списке задач")
    @Test
    public void clearAllTasksWithNoTasks() {
        testManager.clearAllTasks();
        Assertions.assertEquals(testManager.getAllTasks(), new HashMap<>(),
                "Ошибка при очистке пустого списка.");
    }

    @DisplayName("getTaskById Обычное поведение")
    @Test
    public void getTaskByIdBase() {
        testManager.newSimpleTask(new NewTask("1", "1"));
        boolean isEqual = testManager.getTaskById(1).getTaskTitle().equals("1")
                && testManager.getTaskById(1).getTaskDescription().equals("1")
                && testManager.getTaskById(1).getTaskStatus().equals(TaskStatus.NEW);
        Assertions.assertTrue(isEqual, "Ошибка при получении существующей задачи по номеру задачи.");
    }

    @DisplayName("getTaskById Поиск задач с неверным номером задачи")
    @Test
    public void getTaskByIdWithWrongId() {
        testManager.newSimpleTask(new NewTask("1", "1"));
        Assertions.assertNull(testManager.getTaskById(2), "Ошибка при получении несуществующей задачи.");
    }

    @DisplayName("removeTaskById Обычное поведение")
    @Test
    public void removeTaskByIdBase() {
        testManager.newSimpleTask(new NewTask("1", "1"));
        Task task = testManager.removeTaskById(1);
        boolean isEqual = task.getTaskTitle().equals("1")
                && task.getTaskDescription().equals("1")
                && task.getTaskStatus().equals(TaskStatus.NEW);
        Assertions.assertTrue(isEqual, "Ошибка при удалении задачи по номеру.");
    }

    @DisplayName("removeTaskById При пустом списке задач")
    @Test
    public void removeTaskByIdWithNoTasks() {
        Assertions.assertNull(testManager.removeTaskById(1),
                "Ошибка при удалении задачи при пустом списке.");
    }

    @DisplayName("removeTaskById Удаление задач с неверным номером задачи")
    @Test
    public void removeTaskByIdWithWrongId() {
        testManager.newSimpleTask(new NewTask("1", "1"));
        Assertions.assertNull(testManager.removeTaskById(2),
                "Ошибка при удалении несуществующей задачи.");
    }

    @DisplayName("removeTaskById Удаление эпика и его подзадач по нормальному сценарию")
    @Test
    public void removeEpicByIdBase() {
        testManager.newEpic(new NewTask("1", "1"));
        testManager.newSubtask(new NewTask("2", "2"), 1);
        testManager.newSubtask(new NewTask("3", "3"), 1);
        testManager.removeTaskById(1);
        boolean isRemoved = testManager.getTaskById(2) == null
                && testManager.getTaskById(3) == null
                && testManager.getTaskById(1) == null;
        Assertions.assertTrue(isRemoved, "Ошибка при удалении эпика и подзадач эпика.");
    }

    @DisplayName("Время выполнения задач. Создание задачи со временем")
    @Test
    public void createTaskWithTimeData() {
        testManager.newSimpleTask(new NewTask("1", "1",
                LocalDateTime.of(2023, Month.APRIL, 28, 0, 0), 30));
        Assertions.assertTrue(testManager.getTaskById(1).getStartTime().equals
                (LocalDateTime.of(2023, Month.APRIL, 28, 0, 0)) &&
                testManager.getTaskById(1).getDuration() == 30,
                "Ошибка при создании файла с данными о времени выполнения");
    }

    @DisplayName("Время выполнения задач. Создание задачи без времени")
    @Test
    public void createTaskWithNoTimeData() {
        testManager.newSimpleTask(new NewTask("1", "1", null, 0));
        Assertions.assertTrue(testManager.getTaskById(1).getStartTime() == null &&
                testManager.getTaskById(1).getDuration() == 0,
                "Ошибка при создании файла без данных о времени выполнения");
    }

    @DisplayName("Время выполнения задач. Создание задачи с пересечением по времени с началом другой задачи")
    @Test
    public void createTaskWithCrossingTimeDataInBeginning() {
        ManagerExceptions.TaskTimeOverlayException exception =
                Assertions.assertThrows(ManagerExceptions.TaskTimeOverlayException.class, () -> {
            testManager.newSimpleTask(new NewTask("1", "1",
                    LocalDateTime.of(2023, Month.APRIL, 28, 0, 0), 30));
            testManager.newSimpleTask(new NewTask("2", "2",
                    LocalDateTime.of(2023, Month.APRIL, 28, 0, 0), 60));
        });
        Assertions.assertEquals(exception.getMessage(), "Время выполнения задачи 2 пересекается со сроками других задач.",
                "Ошибка при проверке наличия пересечений задач по времени в начале их выполнения.");
    }

    @DisplayName("Время выполнения задач. Создание задачи с пересечением по времени с концом другой задачи")
    @Test
    public void createTaskWithCrossingTimeDataInEnd() {
        ManagerExceptions.TaskTimeOverlayException exception =
                Assertions.assertThrows(ManagerExceptions.TaskTimeOverlayException.class, () -> {
                    testManager.newSimpleTask(new NewTask("1", "1",
                            LocalDateTime.of(2023, Month.APRIL, 28, 0, 0), 30));
                    testManager.newSimpleTask(new NewTask("2", "2",
                            LocalDateTime.of(2023, Month.APRIL, 28, 0, 29), 60));
        });
        Assertions.assertEquals(exception.getMessage(), "Время выполнения задачи 2 пересекается со сроками других задач.",
                "Ошибка при проверке наличия пересечений задач по времени в конце их выполнения.");
    }

    @DisplayName("Время выполнения задач. Создание задачи с пересечением по времени полностью входит в другую задачу")
    @Test
    public void createTaskWithCrossingTimeDataInMiddle() {
        ManagerExceptions.TaskTimeOverlayException exception =
                Assertions.assertThrows(ManagerExceptions.TaskTimeOverlayException.class, () -> {
                    testManager.newSimpleTask(new NewTask("1", "1",
                            LocalDateTime.of(2023, Month.APRIL, 28, 0, 0), 30));
                    testManager.newSimpleTask(new NewTask("2", "2",
                            LocalDateTime.of(2023, Month.APRIL, 28, 0, 10), 10));
                });
        Assertions.assertEquals(exception.getMessage(), "Время выполнения задачи 2 пересекается со сроками других задач.",
                "Ошибка при проверке наличия пересечений задач по времени в середине их выполнения.");
    }

    @DisplayName("Время выполнения задач. Создание задачи с пересечением по времени полностью выходит за другую задачу")
    @Test
    public void createTaskWithCrossingTimeDataInMiddle2() {
        ManagerExceptions.TaskTimeOverlayException exception =
                Assertions.assertThrows(ManagerExceptions.TaskTimeOverlayException.class, () -> {
                    testManager.newSimpleTask(new NewTask("1", "1",
                            LocalDateTime.of(2023, Month.APRIL, 28, 1, 0), 30));
                    testManager.newSimpleTask(new NewTask("2", "2",
                            LocalDateTime.of(2023, Month.APRIL, 28, 0, 0), 360));
                });
        Assertions.assertEquals(exception.getMessage(), "Время выполнения задачи 2 пересекается со сроками других задач.",
                "Ошибка при проверке наличия пересечений задач по времени в середине их выполнения.");
    }

    @DisplayName("Время выполнения задач. Определение времени эпика без подзадач")
    @Test
    public void getEpicTimeDataWithOutSubTasks() {
        testManager.newEpic(new NewTask("1", "1"));
        Assertions.assertTrue(testManager.getTaskById(1).getStartTime() == null &&
                        testManager.getTaskById(1).getDuration() == 0,
                "Ошибка во информации о времени для эпика без подзадач.");
    }

    @DisplayName("Время выполнения задач. Определение времени эпика с подзадачами")
    @Test
    public void getEpicTimeDataWithSubTasks() {
        testManager.newEpic(new NewTask("1", "1"));
        testManager.newSubtask(new NewTask("2", "2",
                LocalDateTime.of(2022, Month.APRIL, 1, 0, 0), 10), 1);
        testManager.newSubtask(new NewTask("3", "3", null, 10), 1);
        testManager.newSubtask(new NewTask("4", "4",
                LocalDateTime.of(2022, Month.APRIL, 1, 0, 11), 10), 1);
        Assertions.assertTrue((testManager.getTaskById(1).getStartTime().equals
                        (LocalDateTime.of(2022, Month.APRIL, 1, 0, 0))) &&
                        (testManager.getTaskById(1).getDuration() == 21),
                "Ошибка в информации о времени для эпика c подзадачами.");
    }

    @DisplayName("Время выполнения задач. Определение времени эпика после удаления подзадачи")
    @Test
    public void getEpicTimeDataAfterRemovingSubTask() {
        testManager.newEpic(new NewTask("1", "1"));
        testManager.newSubtask(new NewTask("2", "2",
                LocalDateTime.of(2022, Month.APRIL, 1, 0, 0), 10), 1);
        testManager.newSubtask(new NewTask("3", "3", null, 10), 1);
        testManager.newSubtask(new NewTask("4", "4",
                LocalDateTime.of(2022, Month.APRIL, 1, 0, 20), 10), 1);
        testManager.removeTaskById(4);
        Assertions.assertTrue((testManager.getTaskById(1).getStartTime().equals
                        (LocalDateTime.of(2022, Month.APRIL, 1, 0, 0))) &&
                        (testManager.getTaskById(1).getDuration() == 10),
                "Ошибка в информации о времени для эпика c подзадачами после удаления подзадачи.");
    }

    @DisplayName("Время выполнения задач. Получение приоритетов с задачами")
    @Test
    public void getPriorityWithTasks() {
        testManager.newSimpleTask(new NewTask("1", "1",
                LocalDateTime.of(2023, Month.APRIL, 1, 0, 0), 30));
        testManager.newSimpleTask(new NewTask("2", "2",
                LocalDateTime.of(2023, Month.APRIL, 3, 0, 10), 10));
        testManager.newSimpleTask(new NewTask("1", "1",
                LocalDateTime.of(2023, Month.APRIL, 2, 0, 0), 30));
        testManager.newSimpleTask(new NewTask("2", "2",
                LocalDateTime.of(2023, Month.APRIL, 10, 0, 10), 10));
        Assertions.assertEquals(testManager.getPrioritizedTasks().toString(), ("[1, 3, 2, 4]"),
                "Ошибка при получении приоритетов с задачами.");
    }

    @DisplayName("Время выполнения задач. Получение приоритетов без задач")
    @Test
    public void getPriorityWithNoTasks() {
        Assertions.assertEquals(testManager.getPrioritizedTasks().toString(), ("[]"),
                "Ошибка при получении приоритетов при отсутствии задач.");
    }

    @DisplayName("Время выполнения задач. Получение приоритетов с задачами без времени")
    @Test
    public void getPriorityWithTasksWithNoTimeData() {

        testManager.newSimpleTask(new NewTask("1", "1", null, 30));
        testManager.newSimpleTask(new NewTask("2", "2",
                LocalDateTime.of(2023, Month.APRIL, 3, 0, 10), 10));
        testManager.newSimpleTask(new NewTask("3", "4", null, 30));
        testManager.newSimpleTask(new NewTask("4", "4",
                LocalDateTime.of(2023, Month.APRIL, 10, 0, 10), 10));
        Assertions.assertEquals(testManager.getPrioritizedTasks().toString(), ("[2, 4, 1, 3]"),
                "Ошибка при получении приоритетов с задачами при отсутствии времени в них");
    }

    @DisplayName("Запись обычная с историей")
    // 12. Расчет время окончания при отсутствии время выполнения
    @Test
    public void getEndTimeWithNoTimeInTask() {
        testManager.newSimpleTask(new NewTask("2", "2"));
        Assertions.assertNull(testManager.getTaskById(1).getEndTime(),
                "Ошибка при расчете времени окончания при отсутствии времени в задаче.");
    }

    @DisplayName("Запись обычная с историей")
    // 13. Расчет время окончания при наличии время выполнения
    @Test
    public void getEndTimeWithTimeInTask() {
        testManager.newSimpleTask(new NewTask("2", "2",
                LocalDateTime.of(2023, Month.APRIL, 3, 0, 10), 10));
        Assertions.assertEquals(testManager.getTaskById(1).getEndTime(),
                LocalDateTime.of(2023, Month.APRIL, 3, 0, 20),
                "Ошибка при расчете времени окончания при наличия времени в задаче.");
    }

    @DisplayName("Запись обычная с историей")
    // 14. Расчет время окончания при наличии время выполнения равной 0
    @Test
    public void getEndTimeWithZeroTimeInTask() {
        testManager.newSimpleTask(new NewTask("2", "2",
                LocalDateTime.of(2023, Month.APRIL, 3, 0, 10), 0));
        Assertions.assertEquals(testManager.getTaskById(1).getEndTime(),
                LocalDateTime.of(2023, Month.APRIL, 3, 0, 10), "" +
                        "Ошибка при расчете времени окончания при 0 длительности.");
    }


    @DisplayName("Проверка пересечений при удалении задач с последующей записью новой задачи.")
    @Test
    public void checkOverlaysAfterRemovingAndAddingNewTask() {
        testManager.newSimpleTask(new NewTask("1", "1",
                LocalDateTime.of(2023, Month.APRIL, 1, 0, 0), 10));
        testManager.newSimpleTask(new NewTask("2", "2",
                LocalDateTime.of(2023, Month.APRIL, 1, 1, 0), 10));
        testManager.newSimpleTask(new NewTask("3", "3",
                LocalDateTime.of(2023, Month.APRIL, 1, 2, 0), 10));
        testManager.removeTaskById(2);
        testManager.newSimpleTask(new NewTask("4", "4",
                LocalDateTime.of(2023, Month.APRIL, 1, 1, 0), 20));
        Assertions.assertTrue((testManager.getTaskById(4).getStartTime().equals
                        (LocalDateTime.of(2023, Month.APRIL, 1, 1, 0))) &&
                        (testManager.getTaskById(4).getDuration() == 20),
                "Ошибка при создании задачи после удаления задачи в данном временном промежутке.");
    }
}