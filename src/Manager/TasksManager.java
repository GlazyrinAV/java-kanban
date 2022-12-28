package Manager;
import java.util.HashMap;
import Model.*;

public class TasksManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();

    /**
     * Получение списка всех задач в виде единого хранилища
     * @return - единое хранилище, которое состоит из всех задач
     */
    public HashMap<Integer, Task> getAllTasks() {
        HashMap<Integer, Task> allTasks = new HashMap<>();
        if (!tasks.isEmpty()) {
            for (int taskID : tasks.keySet()) {
                allTasks.put(taskID, tasks.get(taskID));
                if (tasks.get(taskID).getClass().equals(EpicTask.class)) {
                    EpicTask task = (EpicTask) tasks.get(taskID);
                    for (int subTaskID : task.getSubTasks().keySet()) {
                        allTasks.put(subTaskID, task.getSubTasks().get(subTaskID));
                    }
                }
            }
        }
        return allTasks;
    }

    /**
     * Удаляет все виды задач
     */
    public void clearAllTasks() {
        tasks.clear();
    }

    /**
     * Получение информации о задаче по объявленному номеру
     * @param taskId - номер задачи
     * @return - искомый объект или null, если он не найден
     */
    public Task getTaskById (int taskId) {
        Task taskById = null;
        if (tasks.containsKey(taskId)) {
            taskById = tasks.get(taskId);
        } else {
            for (int taskID : tasks.keySet()) {
                if (tasks.get(taskID).getClass().equals(EpicTask.class)) {
                    EpicTask task = (EpicTask) tasks.get(taskID);
                    if (task.getSubTasks().containsKey(taskId)) {
                        taskById = task.getSubTasks().get(taskId);
                    }
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
        tasks.put(newTask.getTaskIdNumber(), newTask);
    }

    /**
     * Создание нового эпика     *
     * @param taskTitle       - название эпика
     * @param taskDescription - описание эпика
     */
    public void newEpic(String taskTitle, String taskDescription) {
        EpicTask newEpic = new EpicTask(taskTitle, taskDescription);
        tasks.put(newEpic.getTaskIdNumber(), newEpic);
    }

    /**
     * Создание подзадачи для эпика
     * @param taskId - номер Эпика
     * @param taskTitle - название подзадачи
     * @param taskDescription - описание подзадачи
     */
    public void newSubtask (int taskId, String taskTitle, String taskDescription) {
        if (tasks.containsKey(taskId) && tasks.get(taskId).getClass().equals(EpicTask.class)) {
            EpicTask task = (EpicTask) tasks.get(taskId);
            Subtask subTask = new Subtask(taskTitle, taskDescription);
            task.addSubTask(subTask);
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
        if (tasks.containsKey(taskId)) {
            Task task = new Task(taskTitle, taskDescription, taskId, taskStatus);
            tasks.put(taskId, task);
        } else {
            for (int taskID : tasks.keySet()) {
                if (tasks.get(taskID).getClass().equals(EpicTask.class)) {
                    EpicTask task = (EpicTask) tasks.get(taskID);
                    if (task.getSubTasks().containsKey(taskId)) {
                        Subtask subtask = new Subtask(taskTitle, taskDescription, taskId, taskStatus);
                        task.addSubTask(subtask);
                    }
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
            if (tasks.get(taskId).getClass().equals(EpicTask.class)) {
                EpicTask epic = new EpicTask(taskTitle, taskDescription, taskId);
                EpicTask task = (EpicTask) tasks.get(taskId);
                HashMap<Integer, Subtask> temporarySubTasks;
                temporarySubTasks = task.getSubTasks();
                tasks.put(taskId, epic);
                for (int taskID : temporarySubTasks.keySet()) {
                    epic.addSubTask(temporarySubTasks.get(taskID));
                }
            }
        } else {
            if (tasks.get(taskId).getClass().equals(EpicTask.class)) {
                EpicTask epic = new EpicTask(taskTitle, taskDescription, taskId);
                tasks.put(taskId, epic);
            }
        }
    }

    /**
     * Метод проверяет все хранилища задач и удаляет задачу с объявленным номером
     * @param taskId - номер задачи, которую необходимо удалить
     */
    public void removeTaskById(int taskId) {
        if (tasks.containsKey(taskId)) {
            tasks.remove(taskId);
        } else  {
            for (int epicID : tasks.keySet()) {
                if (tasks.get(epicID).getClass().equals(EpicTask.class)) {
                    EpicTask task = (EpicTask) tasks.get(epicID);
                    if (task.getSubTasks().containsKey(taskId)) {
                        task.getSubTasks().remove(taskId);
                    }
                }
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
        HashMap<Integer, Subtask> subtasks = null;
        if (tasks.get(taskId).getClass().equals(EpicTask.class)) {
            EpicTask task = (EpicTask) tasks.get(taskId);
            subtasks = task.getSubTasks();
        }
        return subtasks;
    }

    /**
     * Метод возвращает номер задачи по имени данной задачи
     * @param name - имя искомой задачи
     * @return - номер искомой задачи
     */
    public int getTaskIdByName(String name) {
        int result = -1;
        if (!tasks.isEmpty()) {
            for (int taskID : tasks.keySet()) {
                if (tasks.get(taskID).getTaskTitle().equals(name)) {
                    result = taskID;
                } else {
                    if (tasks.get(taskID).getClass().equals(EpicTask.class)) {
                        EpicTask task = (EpicTask) tasks.get(taskID);
                        for (int subTaskID : task.getSubTasks().keySet()) {
                            if (task.getSubTasks().get(subTaskID).equals(name)) {
                                result = subTaskID;
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }
}