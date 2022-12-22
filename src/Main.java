public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();
        manager.newTask("Task 1", "Description of Task 1");
        manager.newEpic("Epic 1", "Description of Epic 1");
        manager.newSubtask(2, "Sub 1", "Description Sub 1");
        manager.newSubtask(2, "Sub 2", "Description Sub 2");
        manager.printAllTasks(manager.getSimpleTasks(), manager.getEpics());
        manager.getTaskById(3, manager.getSimpleTasks(), manager.getEpics());
        manager.getTaskById(9, manager.getSimpleTasks(), manager.getEpics());
    }
}
