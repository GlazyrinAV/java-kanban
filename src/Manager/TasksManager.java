package Manager;
import java.util.HashMap;
import Model.*;


public class TasksManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();

    /**
     * Получение списка всех задач в виде единого хранилища
     * @return - единое хранилище, которое состоит из всех задач
     */
    public HashMap<Integer, Task> getAllTasks() {
        HashMap<Integer, Task> allTasks = new HashMap<>();
        if (!tasks.isEmpty()) {
            for (int taskID : tasks.keySet()) {
                allTasks.put(taskID, tasks.get(taskID));
                if (tasks.get(taskID) instanceof EpicTask) {
                    EpicTask task = (EpicTask) tasks.get(taskID);
                    for (int subTaskID : task.getSubTasks().keySet()) {
                        allTasks.put(subTaskID, task.getSubTasks().get(subTaskID));
                    }
                }
            }
        }
        return allTasks; // maybe null
    }

    /**
     * Удаляет все виды задач
     */
    public void clearAllTasks() {
        tasks.clear();
    }

    /**
     * Получение задачи по объявленному номеру
     * @param taskId - номер задачи
     * @return       - искомый объект или null, если он не найден
     */
    public Task getTaskById (int taskId) {
        Task taskById = null;
        if (tasks.containsKey(taskId)) {
            taskById = tasks.get(taskId);
        } else {
            for (int taskID : tasks.keySet()) {
                if (tasks.get(taskID) instanceof EpicTask) {
                    EpicTask task = (EpicTask) tasks.get(taskID);
                    if (task.getSubTasks().containsKey(taskId)) {
                        taskById = task.getSubTasks().get(taskId);
                    }
                }
            }
        }
        return taskById; // maybe null
    }

    /**
     * Создание новой простой задачи     *
     * @param taskTitle       - название задачи
     * @param taskDescription - описание задачи
     */
    public void newTask(String taskTitle, String taskDescription) {
        SimpleTask newTask = new SimpleTask(taskTitle, taskDescription);
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
     * @param epicId          - номер Эпика
     * @param taskTitle       - название подзадачи
     * @param taskDescription - описание подзадачи
     */
    public void newSubtask (int epicId, String taskTitle, String taskDescription) {
        if (tasks.containsKey(epicId) && tasks.get(epicId).getClass().equals(EpicTask.class)) {
            EpicTask task = (EpicTask) tasks.get(epicId);
            Subtask subTask = new Subtask(taskTitle, taskDescription);
            task.addSubTask(subTask);
        }
    }

    /**
     * Обновление простой задачи и подзадачи эпика по объявленному номеру с возможностью установить новый статус
     * @param taskId          - номер задачи
     * @param taskTitle       - название задачи
     * @param taskDescription - описание задачи
     * @param taskStatus      - статус задачи
     */
    public void updateTask(int taskId, String taskTitle, String taskDescription, TaskStatus taskStatus) {
        if (tasks.containsKey(taskId)) {
            SimpleTask task = new SimpleTask(taskTitle, taskDescription, taskId, taskStatus);
            tasks.put(taskId, task);
        } else {
            for (int taskID : tasks.keySet()) {
                if (tasks.get(taskID) instanceof EpicTask) {
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
     * @param taskId          - номер эпика
     * @param taskTitle       - название эпика
     * @param taskDescription - описание эпика
     * @param saveSubTasks    - опредеяет необходимость сохранения подзадач в обновленном эпике
     * (true - сохранить, false - удалить).
     */
    public void updateTask(int taskId, String taskTitle, String taskDescription, boolean saveSubTasks) {
        if (saveSubTasks) {
            if (tasks.get(taskId) instanceof EpicTask) {
                EpicTask epic = new EpicTask(taskTitle, taskDescription, taskId);
                EpicTask task = (EpicTask) tasks.get(taskId);
                HashMap<Integer, Subtask> temporarySubTasks;
                temporarySubTasks = task.getSubTasks();
                tasks.put(taskId, epic);
                for (int subTaskID : temporarySubTasks.keySet()) {
                    epic.addSubTask(temporarySubTasks.get(subTaskID));
                }
            }
        } else if (tasks.get(taskId) instanceof EpicTask) {
            EpicTask epic = new EpicTask(taskTitle, taskDescription, taskId);
            tasks.put(taskId, epic);
        }
    }

    /**
     * Метод проверяет все задачи и подзадачи и удаляет задачу с объявленным номером
     * @param taskId - номер задачи, которую необходимо удалить
     */
    public void removeTaskById(int taskId) {
        if (tasks.containsKey(taskId)) {
            tasks.remove(taskId);
        } else  {
            for (int epicID : tasks.keySet()) {
                if (tasks.get(epicID) instanceof EpicTask) {
                    EpicTask task = (EpicTask) tasks.get(epicID);
                    if (task.getSubTasks().containsKey(taskId)) {
                        task.removeSubTask(taskId);
                    }
                }
            }
        }
    }

    /**
     * Получение списка всех подзадач определённого эпика по номеру данного эпика
     * Возвращает HashMap, состоящий из подзадач или null, если не найден эпик или у него отсутствуют подзадачи
     * @param epicId - номер задачи
     * @return       - список подзадач выбранного эпика
     */
    public HashMap<Integer, Subtask> getSubTasksOfEpicById(int epicId) {
        HashMap<Integer, Subtask> subtasks = null;
        if (tasks.get(epicId) instanceof EpicTask) {
            EpicTask task = (EpicTask) tasks.get(epicId);
            subtasks = task.getSubTasks();
        }
        return subtasks; // maybe null
    }

    /**
     * Метод возвращает номер задачи по имени данной задачи
     * @param name - имя искомой задачи
     * @return     - номер искомой задачи
     */
    public Integer getTaskIdByName(String name) {
        Integer result = null;
        if (!tasks.isEmpty()) {
            for (int taskID : tasks.keySet()) {
                if (tasks.get(taskID).getTaskTitle().equals(name)) {
                    result = taskID;
                } else if (tasks.get(taskID) instanceof EpicTask) {
                    EpicTask task = (EpicTask) tasks.get(taskID);
                    for (int subTaskID : task.getSubTasks().keySet()) {
                        if (task.getSubTasks().get(subTaskID).getTaskTitle().equals(name)) {
                            result = subTaskID;
                        }
                    }

                }
            }
        }
        return result; // maybe null
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks; // maybe null
    }
}