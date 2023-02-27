package Tests;

import Exceptions.ManagerExceptions;
import Manager.TaskManager;
import Model.NewTask;
import Model.Subtask;
import Model.Task;
import Model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

public abstract class TaskManagerTest<T extends TaskManager> {

    private T testManager;

    public void setManager(T t) {
        this.testManager = t;
    }

    public T getTestManager() {
        return testManager;
    }

    // SimpleTask
    // 1. Статус при создании
    @Test
    public void simpleTaskStatusOnCreationIsNew() {
        testManager.newSimpleTask(new NewTask("1", "2"));
        Assertions.assertEquals(testManager.getTaskById(1).getTaskStatus(), TaskStatus.NEW,
                "Статус простой задачи установлен неверно.");
    }

    // 2. Статус при обновлении
    @Test
    public void simpleTaskStatusOnUpdateIsChanged() {
        testManager.newSimpleTask(new NewTask("1", "2"));
        testManager.updateTask(1, TaskStatus.DONE);
        Assertions.assertEquals(testManager.getTaskById(1).getTaskStatus(), TaskStatus.DONE,
                "Статус простой задачи при обновлении задан неверно.");
    }

    // EpicTask
    // 1. Статус при отсутствии подзадач
    @Test
    public void epicTaskStatusWithNoSubTasks() {
        testManager.newEpic(new NewTask("1", "1"));
        Assertions.assertEquals(testManager.getTaskById(1).getTaskStatus(), TaskStatus.NEW,
                "Статус пустого эпика при создании задан неверно.");
    }

    // 2. Статус при новых подзадачах
    @Test
    public void epicTaskStatusWhenSubTasksIsNew() {
        testManager.newEpic(new NewTask("1", "1"));
        testManager.newSubtask(new NewTask("2", "2"), 1);
        testManager.newSubtask(new NewTask("3", "3"), 1);
        Assertions.assertEquals(testManager.getTaskById(1).getTaskStatus(), TaskStatus.NEW,
                "Статус эпика при добавлении подзадач со статусом NEW задан неверно.");
    }

    // 3. Статус при выполненных подзадачах
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

    // 4. Статус при смешенном статусе подзадач
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

    // 5. Статус при выполняемых подзадачах
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

    // SubTask
    // 1. Статус при создании
    @Test
    public void subTasksStatusOnCreation() {
        testManager.newEpic(new NewTask("1", "1"));
        testManager.newSubtask(new NewTask("2", "2"), 1);
        Assertions.assertEquals(testManager.getTaskById(2).getTaskStatus(), TaskStatus.NEW,
                "Статус подзадачи при создании задан неверно.");
    }

    // 2. Статус при обновлении
    @Test
    public void subTasksStatusOnUpdate() {
        testManager.newEpic(new NewTask("1", "1"));
        testManager.newSubtask(new NewTask("2", "2"), 1);
        testManager.updateTask(2, TaskStatus.IN_PROGRESS);
        Assertions.assertEquals(testManager.getTaskById(2).getTaskStatus(), TaskStatus.IN_PROGRESS,
                "Статус подзадачи при обновлении задан неверно.");
    }

    // 3. Наличие эпика
    @Test
    public void subTaskHaveEpic() {
        testManager.newEpic(new NewTask("1", "1"));
        testManager.newSubtask(new NewTask("2", "2"), 1);
        int epicID = ((Subtask) testManager.getTaskById(2)).getEpicId();
        Assertions.assertEquals(epicID, 1,
                "Ошибка при добавлении подзадачи к существующему эпику.");
    }

    // 4. Подзадача добавляется к несуществующему эпику
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

    // 5. Подзадача добавляется к задаче, которая не является эпиком
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

    // updateTask
    // 1. Обычное обновление задач
    @Test
    public void updateTaskBase() {
        testManager.newSimpleTask(new NewTask("1", "1"));
        testManager.updateTask(1, TaskStatus.IN_PROGRESS);
        Assertions.assertEquals(testManager.getTaskById(1).getTaskStatus(), TaskStatus.IN_PROGRESS,
                "Неверно обновлен статус простой задачи.");
    }

    // 2. Обновление задач с несуществующим номером задачи
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

    // 3. Обновление задач с неверным номером задачи
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

    // getAllTasks
    // 1. Обычное поведение
    @Test
    public void getAllTasksBase() {
        testManager.newSimpleTask(new NewTask("1", "1"));
        Assertions.assertEquals(testManager.getAllTasks().values().toString(),
                "[№1. Задача. Название задачи - 1. Описание задачи: 1. Статус задачи: NEW. Время начала: null. Продолжительность: 0. Время окончания: null]",
                "Ошибка при получении списка всех задач");
    }

    // 2. При пустом списке задач
    @Test
    public void getAllTasksWithNoTasks() {
        Assertions.assertEquals(testManager.getAllTasks(), new HashMap<>(),
                "Ошибка при получении списка всех задач при отсутствии задач.");
    }

    // clearAllTasks
    // 1. Обычное поведение
    @Test
    public void clearAllTasksBase() {
        testManager.newSimpleTask(new NewTask("1", "1"));
        testManager.clearAllTasks();
        Assertions.assertEquals(testManager.getAllTasks(), new HashMap<>(),
                "Ошибка при очистке списка существующих задач.");
    }

    // 2. При пустом списке задач
    @Test
    public void clearAllTasksWithNoTasks() {
        testManager.clearAllTasks();
        Assertions.assertEquals(testManager.getAllTasks(), new HashMap<>(),
                "Ошибка при очистке пустого списка.");
    }

    // getTaskById
    // 1. Обычное поведение
    @Test
    public void getTaskByIdBase() {
        testManager.newSimpleTask(new NewTask("1", "1"));
        boolean isEqual = testManager.getTaskById(1).getTaskTitle().equals("1")
                && testManager.getTaskById(1).getTaskDescription().equals("1")
                && testManager.getTaskById(1).getTaskStatus().equals(TaskStatus.NEW);
        Assertions.assertTrue(isEqual, "Ошибка при получении существующей задачи по номеру задачи.");
    }

    // 2. Поиск задач с неверным номером задачи
    @Test
    public void getTaskByIdWithWrongId() {
        testManager.newSimpleTask(new NewTask("1", "1"));
        Assertions.assertNull(testManager.getTaskById(2), "Ошибка при получении несуществующей задачи.");
    }

    // removeTaskById
    // 1. Обычное поведение
    @Test
    public void removeTaskByIdBase() {
        testManager.newSimpleTask(new NewTask("1", "1"));
        Task task = testManager.removeTaskById(1);
        boolean isEqual = task.getTaskTitle().equals("1")
                && task.getTaskDescription().equals("1")
                && task.getTaskStatus().equals(TaskStatus.NEW);
        Assertions.assertTrue(isEqual, "Ошибка при удалении задачи по номеру.");
    }

    // 2. При пустом списке задач
    @Test
    public void removeTaskByIdWithNoTasks() {
        Assertions.assertNull(testManager.removeTaskById(1),
                "Ошибка при удалении задачи при пустом списке.");
    }

    // 3. Удаление задач с неверным номером задачи
    @Test
    public void removeTaskByIdWithWrongId() {
        testManager.newSimpleTask(new NewTask("1", "1"));
        Assertions.assertNull(testManager.removeTaskById(2),
                "Ошибка при удалении несуществующей задачи.");
    }

    // 4. Удаление эпика и его подзадач по нормальному сценарию
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

    // Время выполнения задач
    // 1. Создание задачи со временем
    @Test
    public void createTaskWithTimeData() {
    }

    // 2. Создание задачи без времени
    @Test
    public void createTaskWithNoTimeData() {
    }

    // 3. Создание задачи с пересечением по времени с началом другой задачи
    @Test
    public void createTaskWithCrossingTimeDataInBeginning() {
    }

    // 4. Создание задачи с пересечением по времени с концом другой задачи
    @Test
    public void createTaskWithCrossingTimeDataInEnd() {
    }

    // 5. Создание задачи с пересечением по времени полностью входит в другую задачу
    @Test
    public void createTaskWithCrossingTimeDataInMiddle() {
    }

    // 6. Определение времени эпика без подзадач
    @Test
    public void getEpicTimeDataWithOutSubTasks() {
    }

    // 7. Определение времени эпика с подзадачами
    @Test
    public void getEpicTimeDataWithSubTasks() {
    }

    // 8. Определение времени эпика после удаления подзадачи
    @Test
    public void getEpicTimeDataAfterRemovingSubTask() {
    }

    // 9. Получение приоритетов с задачами
    @Test
    public void getPriorityWithTasks() {
    }

    // 10. Получение приоритетов без задач
    @Test
    public void getPriorityWithNoTasks() {
    }

    // 11. Получение приоритетов с задачами без времени
    @Test
    public void getPriorityWithTasksWithNoTimeData() {
    }
}