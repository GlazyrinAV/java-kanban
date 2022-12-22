import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> simpleTasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();

    public HashMap<Integer, Task> getSimpleTasks() {
        return simpleTasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    /**
     * Печатает все задачи с описанием
     * @param simpleTasks - простые задачи
     * @param epics - эпики с учетом вложенных подзадач
     */
    public void printAllTasks(HashMap<Integer, Task> simpleTasks, HashMap<Integer, Epic> epics) {
        System.out.println(simpleTasks.values().toString());
        System.out.println(epics.values().toString());
    }

    /**
     * Удаляет все виды задач
     */

    public void clearAllTasks() {
        simpleTasks.clear();
        epics.clear();
    }
//    Получение по идентификатору.

    /**
     * Получение информации по задаче по объявленному номеру
     * @param taskId - номер задачи
     * @param simpleTasks - множество простых задач
     * @param epics - множество эпиков
     */
    public void getTaskById (int taskId, HashMap<Integer, Task> simpleTasks, HashMap<Integer, Epic> epics) {
        int errorCheck = 1;
        if (simpleTasks.containsKey(taskId)) {
            System.out.println(simpleTasks.get(taskId).toString());
            errorCheck = 0;
        }
        if (epics.containsKey(taskId)) {
            System.out.println(epics.get(taskId).toString());
            errorCheck = 0;
        } else {
            for (int task : epics.keySet()) {
                if (epics.get(task).getSubTasks().containsKey(taskId)) {
                    System.out.println("\n" + epics.get(task).getSubTasks().get(taskId));
                    System.out.println("Данная задача является подзадачей Эпика №" + epics.get(task).getTaskIdNumber());
                    errorCheck = 0;
                }
            }
        }

        if (errorCheck == 1) {
            System.out.println("Задача с данным номером не найдено.");
        }
    }

    /**
     * Создание новой простой задачи
     *
     * @param taskTitle       - название задачи
     * @param taskDescription - описание задачи
     */

    public void newTask(String taskTitle, String taskDescription) {
        Task newTask = new Task(taskTitle, taskDescription);
        simpleTasks.put(newTask.getTaskIdNumber(), newTask);
    }

    /**
     * Создание нового эпика
     *
     * @param taskTitle       - название эпика
     * @param taskDescription - описание эпика
     */

    public void newEpic(String taskTitle, String taskDescription) {
        Epic newEpic = new Epic(taskTitle, taskDescription);
        epics.put(newEpic.getTaskIdNumber(), newEpic);
    }

    /**
     * Метод добавляющий в эпик подзадачи
     * @param taskId - номер Эпика
     * @param taskTitle - название подзадачи
     * @param taskDescription - описание подзадачи
     */
    public void newSubtask (int taskId, String taskTitle, String taskDescription) {
        if (epics.containsKey(taskId)) {
            Subtask subTask = new Subtask(taskTitle, taskDescription);
            epics.get(taskId).addSubTask(subTask);
        } else {
            System.out.println("Эпик с указанным номером не найден.");
        }
    }
//    Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.

    /**
     * Метод проверяет все хранилища задач и удаляет задачу с объявленным номером
     * @param taskId - номер задачи, которую необходимо удалить
     */
    public void removeTaskById(int taskId) {
        int errorCheck = 0;
        if (simpleTasks.containsKey(taskId)) {
            simpleTasks.remove(taskId);
            System.out.println("Задача с номером " + taskId + " удалена.");
            errorCheck = 1;
        }
        if (epics.containsKey(taskId)) {
            epics.remove(taskId);
            System.out.println("Задача с номером " + taskId + " удалена.");
            errorCheck = 1;
        }
        for (int epic : epics.keySet()) {
            if (epics.get(epic).getSubTasks().containsKey(taskId)) {
                epics.get(epic).getSubTasks().remove(taskId);
                System.out.println("Задача с номером " + taskId + " удалена.");
                errorCheck = 1;
            }
        }
        if (errorCheck == 0) {
            System.out.println("Задача с таким номером не найдена.");
        }
    }
}
