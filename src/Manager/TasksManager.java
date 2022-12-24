package Manager;
import java.util.HashMap;
import Model.*;

public class TasksManager {
    private HashMap<Integer, Task> simpleTasks = new HashMap<>();
    private HashMap<Integer, EpicTask> epics = new HashMap<>();
    private HashMap<Integer, Task> allTasks = new HashMap<>();

    public HashMap<Integer, Task> getSimpleTasks() {
        return simpleTasks;
    }

    public HashMap<Integer, EpicTask> getEpics() {
        return epics;
    }

    /**
     * Получение списка всех задач в виде единого хранилища
     * @return - единое хранилище, которое состоит из всех задач
     */
    public HashMap<Integer, Task> getAllTasks() {
        if (!simpleTasks.isEmpty()) {
            for (int taskID : simpleTasks.keySet()) {
                allTasks.put(taskID, simpleTasks.get(taskID));
            }
            System.out.println(simpleTasks.values());
        }
        if (!epics.isEmpty()) {
            System.out.println(epics.values());
            for (int taskID : epics.keySet()) {
                allTasks.put(taskID, epics.get(taskID));
            }
        }
        return allTasks;
    }

    /**
     * Удаляет все виды задач
     */
    public void clearAllTasks() {
        simpleTasks.clear();
        epics.clear();
    }

    /**
     * Получение информации о задаче по объявленному номеру
     * @param taskId - номер задачи
     * @return - искомый объект или null, если он не найден
     */
    public Task getTaskById (int taskId) {
        Task taskById = null;
        if (simpleTasks.containsKey(taskId)) {
            taskById = simpleTasks.get(taskId);
        } else if (epics.containsKey(taskId)) {
            taskById = epics.get(taskId);
        } else {
            for (int task : epics.keySet()) {
                if (epics.get(task).getSubTasks().containsKey(taskId)) {
                    taskById = epics.get(task).getSubTasks().get(taskId);
                }
            }
        }
        return taskById;
    }

    /**
     * Создание новой простой задачи     *
     * @param taskTitle       - название задачи
     * @param taskDescription - описание задачи
     */
    public void newTask(String taskTitle, String taskDescription) {
        Task newTask = new Task(taskTitle, taskDescription);
        simpleTasks.put(newTask.getTaskIdNumber(), newTask);
    }

    /**
     * Создание нового эпика     *
     * @param taskTitle       - название эпика
     * @param taskDescription - описание эпика
     */
    public void newEpic(String taskTitle, String taskDescription) {
        EpicTask newEpic = new EpicTask(taskTitle, taskDescription);
        epics.put(newEpic.getTaskIdNumber(), newEpic);
    }

    /**
     * Создание подзадачи для эпика
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

    /**
     * Обновление простой задачи и подзадачи эпика по объявленному номеру с возможностью установить новый статус
     * @param taskId - номер задачи
     * @param taskTitle - название задачи
     * @param taskDescription - описание задачи
     * @param taskStatus - статус задачи
     */
    public void updateTask(int taskId, String taskTitle, String taskDescription, TaskStatus.Status taskStatus) {
        if (simpleTasks.containsKey(taskId)) {
            Task task = new Task(taskTitle, taskDescription, taskId, taskStatus);
            simpleTasks.put(taskId, task);
        } else {
            for (int task : epics.keySet()) {
                if (epics.get(task).getSubTasks().containsKey(taskId)) {
                    Subtask subtask = new Subtask(taskTitle, taskDescription, taskId, taskStatus);
                    epics.get(task).addSubTask(subtask);
                }
            }
        }
    }

    /**
     * Перегруженный метод обновления для эпика по объявленному номеру.
     * При обновлении эпика вложенные подзадачи могут сохраняться или удаляться.
     * @param taskId - номер эпика
     * @param taskTitle - название эпика
     * @param taskDescription - описание эпика
     * @param saveSubTasks - опредеяет необходимость сохранения подзадач в обновленном эпике
     * (true - сохранить, false - удалить).
     */
    public void updateTask(int taskId, String taskTitle, String taskDescription, boolean saveSubTasks) {
        if (saveSubTasks) {
            if (epics.containsKey(taskId)) {
                EpicTask epic = new EpicTask(taskTitle, taskDescription, taskId);
                HashMap<Integer, Subtask> temporarySubTasks;
                temporarySubTasks = epics.get(taskId).getSubTasks();
                epics.put(taskId, epic);
                for (int taskID : temporarySubTasks.keySet()) {
                    epic.addSubTask(temporarySubTasks.get(taskID));
                }
            }
        } else {
            if (epics.containsKey(taskId)) {
                EpicTask epic = new EpicTask(taskTitle, taskDescription, taskId);
                epics.put(taskId, epic);
            }
        }
    }

    /**
     * Метод проверяет все хранилища задач и удаляет задачу с объявленным номером
     * @param taskId - номер задачи, которую необходимо удалить
     */
    public void removeTaskById(int taskId) {
        int errorCheck = 0;
        if (simpleTasks.containsKey(taskId)) {
            simpleTasks.remove(taskId);
        }
        if (epics.containsKey(taskId)) {
            epics.remove(taskId);
        }
        for (int epic : epics.keySet()) {
            if (epics.get(epic).getSubTasks().containsKey(taskId)) {
                epics.get(epic).getSubTasks().remove(taskId);
            }
        }
    }

    /**
     * Получение списка всех подзадач определённого эпика по номеру данного эпика
     * Возвращает HashMap, состоящий из подзадач или null, если не найден эпик или у него отсутствуют подзадачи
     * @param taskId - номер задачи
     * @return - список подзадач выбранного эпика
     */
    public HashMap<Integer, Subtask> getSubTasksOfEpicById(int taskId) {
        HashMap<Integer, Subtask> subtask = null;
        if (epics.containsKey(taskId)) {
            subtask = epics.get(taskId).getSubTasks();
        }
        return subtask;
    }

    /**
     * Метод возвращает номер задачи по имени данной задачи
     * @param name - имя искомой задачи
     * @return - номер искомой задачи
     */
    public int getTaskIdByName(String name) {
        int result = -1;
        if (!simpleTasks.isEmpty()) {
            for (int taskID : simpleTasks.keySet()) {
                if (simpleTasks.get(taskID).getTaskTitle().equals(name)) {
                    result = taskID;
                }
            }
        }
        if (!epics.isEmpty()) {
            for (int taskID : epics.keySet()) {
                if (epics.get(taskID).getTaskTitle().equals(name)) {
                    result = taskID;
                } else {
                    for (int subTaskID : epics.get(taskID).getSubTasks().keySet()) {
                        if (epics.get(taskID).getSubTasks().get(subTaskID).equals(name)) {
                            result = subTaskID;
                        }
                    }
                }
            }
        }
        return result;
    }
}