import Manager.TasksManager;
import Model.TaskStatus;

public class Main {
    public static void main(String[] args) {
        TasksManager manager = new TasksManager();
        System.out.println("-- Создание 1 простой задачи, 1 эпика с 2 подзадачами.");
        manager.newTask("Task 1", "Description of Task 1");
        manager.newEpic("Epic 1", "Description of Epic 1");
        manager.newSubtask(manager.getTaskIdByName("Epic 1"), "Sub 1", "Description Sub 1");
        manager.newSubtask(manager.getTaskIdByName("Epic 1"), "Sub 2", "Description Sub 2");
        manager.newEpic("Epic 2", "Description of Epic 2");
        manager.newSubtask(manager.getTaskIdByName("Epic 2"), "Sub 1", "Description Sub 1");
        manager.newSubtask(manager.getTaskIdByName("Epic 2"), "Sub 2", "Description Sub 2");

        System.out.println("-- Печать всех задач");
        manager.getAllTasks().toString();

        System.out.println("-- Поиск задачи номер 3 и номер 9");
        if (manager.getTaskById(3) != null)
            System.out.println(manager.getTaskById(3));
        else System.out.println("null");
        if (manager.getTaskById(9) != null)
            System.out.println(manager.getTaskById(9).toString());
        else System.out.println("null");

        System.out.println("-- Замена простой задачи и 2-х подзадач в эпике.");
        manager.updateTask(1, "Task1-2", "Description of Task 1-2", TaskStatus.Status.DONE);
        manager.updateTask(3, "Sub 1-2", "Description Sub 1-2", TaskStatus.Status.IN_PROGRESS);
        manager.updateTask(4, "Sub 2-2", "Description Sub 2-2", TaskStatus.Status.DONE);

        System.out.println("-- Печать всех задач");
        manager.getAllTasks().toString();

        System.out.println("-- Очистка задачи 4 из эпика");
        manager.removeTaskById(4);

        System.out.println("-- Печать подзадач из эпика");
        if (manager.getSubTasksOfEpicById(2) != null)
            System.out.println(manager.getSubTasksOfEpicById(2).values());
        else System.out.println("null");

        System.out.println("-- Замена эпика с сохранением подзадач.");
        manager.updateTask(2, "Epic 1-2", "Description of Epic 1-2", true);

        System.out.println("-- Печать всех задач");
        manager.getAllTasks().toString();

        System.out.println("-- Замена эпика без сохранения подзадач.");
        manager.updateTask(2, "Epic 1-3", "Description of Epic 1-3", false);

        System.out.println("-- Печать всех задач");
        manager.getAllTasks().toString();

        System.out.println("-- Очистка всех задач");
        manager.clearAllTasks();

        System.out.println("-- Печать всех задач");
        manager.getAllTasks().toString();
    }
}