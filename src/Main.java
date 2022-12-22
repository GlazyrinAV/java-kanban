public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();
        System.out.println("-- Создание 1 простой задачи, 1 эпика с 2 подзадачами.");
        manager.newTask("Task 1", "Description of Task 1");
        manager.newEpic("Epic 1", "Description of Epic 1");
        manager.newSubtask(2, "Sub 1", "Description Sub 1");
        manager.newSubtask(2, "Sub 2", "Description Sub 2");

        System.out.println("-- Печать всех задач");
        manager.printAllTasks(manager.getSimpleTasks(), manager.getEpics());

        System.out.println("-- Поиск задачи номер 3 и номер 9");
        manager.getTaskById(3, manager.getSimpleTasks(), manager.getEpics());
        manager.getTaskById(9, manager.getSimpleTasks(), manager.getEpics());

        System.out.println("-- Замена простой задачи и 2-х подзадач в эпике.");
        manager.updateTask(1, "Task1-2", "Description of Task 1-2", 2);
        manager.updateTask(3, "Sub 1-2", "Description Sub 1-2", 1);
        manager.updateTask(4, "Sub 2-2", "Description Sub 2-2", 2);

        System.out.println("-- Печать всех задач");
        manager.printAllTasks(manager.getSimpleTasks(), manager.getEpics());

        System.out.println("-- Очистка задачи 4 из эпика");
        manager.removeTaskById(4);

        System.out.println("-- Печать подзадач из эпика");
        manager.getSubTasksOfEpicById(2);

        System.out.println("-- Замена эпика.");
        manager.updateTask(2, "Epic 1-2", "Description of Epic 1", 2);

        System.out.println("-- Печать всех задач");
        manager.printAllTasks(manager.getSimpleTasks(), manager.getEpics());

        System.out.println("-- Очистка всех задач");
        manager.clearAllTasks();

        System.out.println("-- Печать всех задач");
        manager.printAllTasks(manager.getSimpleTasks(), manager.getEpics());
    }
}