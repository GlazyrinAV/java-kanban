import Manager.Managers;
import Model.Task;
import Model.TaskStatus;

public class Main {
    public static void main(String[] args) {
//        var managers = Managers.getDefault();
        var managers = Managers.getWithAutosave();
        System.out.println("-- Создание 1 простой задачи, 2 эпика с 2 подзадачами.");
        managers.newSimpleTask("Task 1", "Description of Task 1");
        managers.newEpic("Epic 1", "Description of Epic 1");
        managers.newSubtask("Sub 1", "Description Sub 1", managers.getTaskIdByName("Epic 1"));
        managers.newSubtask("Sub 2", "Description Sub 2", managers.getTaskIdByName("Epic 1"));
        managers.newEpic("Epic 2", "Description of Epic 2");
        managers.newSubtask("Sub 1", "Description Sub 1", managers.getTaskIdByName("Epic 2"));
        managers.newSubtask("Sub 2", "Description Sub 2", managers.getTaskIdByName("Epic 2"));

        System.out.println("-- Получение списка всех задач");
        System.out.println(managers.getAllTasks().values());
        System.out.println();

        System.out.println("-- Поиск задачи номер 3 и номер 9");
        if (managers.getTaskById(3) != null)
            System.out.println("Задача найдена");
        else System.out.println("null");
        if (managers.getTaskById(9) != null)
            System.out.println(managers.getTaskById(9).toString());
        else System.out.println("null");

        System.out.println("-- История запросов");
        for (Task task : managers.getHistory()) {
            System.out.println(task.getTaskIdNumber() + " " + task.getTaskTitle());
        }
        System.out.println("-- Замена простой задачи и 2-х подзадач в эпике.");
        managers.updateTask(1, TaskStatus.DONE);
        managers.updateTask(3, TaskStatus.IN_PROGRESS);
        managers.updateTask(4, TaskStatus.DONE);

        System.out.println("-- Печать всех задач");
        System.out.println(managers.getAllTasks().values());

        System.out.println("-- Очистка задачи 4 из эпика");
        managers.removeTaskById(4);

        System.out.println("-- Печать подзадач из эпика");
        if (managers.getSubTasksOfEpicById(2) != null)
            System.out.println(managers.getSubTasksOfEpicById(2));
        else System.out.println("null");

        System.out.println("-- Замена эпика с сохранением подзадач.");
        managers.updateTask(2, true);

        System.out.println("-- Печать всех задач");
        System.out.println(managers.getAllTasks().values());

        System.out.println("-- вызов 14 задач");
        managers.getTaskById(1);
        managers.getTaskById(1);
        managers.getTaskById(5);
        managers.getTaskById(5);
        managers.getTaskById(2);
        managers.getTaskById(5);
        managers.getTaskById(6);
        managers.getTaskById(1);
        managers.getTaskById(1);
        managers.getTaskById(2);
        managers.getTaskById(2);
        managers.getTaskById(1);
        managers.getTaskById(1);
        managers.getTaskById(3);
        managers.getTaskById(3);
        System.out.println("-- История запросов");
        for (Task task : managers.getHistory()) {
            System.out.println(task.getTaskIdNumber() + " " + task.getTaskTitle());
        }

        System.out.println("-- Замена эпика без сохранения подзадач.");
        managers.updateTask(2, false);

        System.out.println("-- Печать всех задач");
        System.out.println(managers.getAllTasks().values());

        var managers2 = Managers.getWithAutosave();
        System.out.println("--Перезапуск системы");
        System.out.println("-- История запросов");
        for (Task task : managers2.getHistory()) {
            System.out.println(task.getTaskIdNumber() + " " + task.getTaskTitle());
        }
        managers2.newSimpleTask("newSimple", "JustSimple");
        System.out.println(managers2.getAllTasks().values());
        managers2.getTaskById(managers2.getTaskIdByName("newSimple"));
        System.out.println("-- История запросов");
        for (Task task : managers2.getHistory()) {
            System.out.println(task.getTaskIdNumber() + " " + task.getTaskTitle());
        }

        System.out.println("-- Очистка всех задач");
        managers2.clearAllTasks();
        System.out.println("-- Печать всех задач");
        System.out.println(managers2.getAllTasks().values());
        System.out.println("-- История запросов");
        for (Task task : managers2.getHistory()) {
            System.out.println(task.getTaskIdNumber() + " " + task.getTaskTitle());
        }
    }
}