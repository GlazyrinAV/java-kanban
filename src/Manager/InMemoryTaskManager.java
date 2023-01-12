package Manager;
import java.util.HashMap;
import Model.*;


public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();

    @Override
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

    @Override
    public void clearAllTasks() {
        tasks.clear();
    }

    @Override
    public Task getTaskById (int taskId) {
        Task taskById = null;
        if (tasks.containsKey(taskId)) {
            taskById = tasks.get(taskId);
            Managers.addDefaultHistory(taskById);
        } else {
            for (int taskID : tasks.keySet()) {
                if (tasks.get(taskID) instanceof EpicTask) {
                    EpicTask task = (EpicTask) tasks.get(taskID);
                    if (task.getSubTasks().containsKey(taskId)) {
                        taskById = task.getSubTasks().get(taskId);
                        Managers.addDefaultHistory(taskById);
                    }
                }
            }
        }
        return taskById; // maybe null
    }

    @Override
    public void newTask(String taskTitle, String taskDescription) {
        SimpleTask newTask = new SimpleTask(taskTitle, taskDescription);
        tasks.put(newTask.getTaskIdNumber(), newTask);
    }

    @Override
    public void newEpic(String taskTitle, String taskDescription) {
        EpicTask newEpic = new EpicTask(taskTitle, taskDescription);
        tasks.put(newEpic.getTaskIdNumber(), newEpic);
    }

    @Override
    public void newSubtask (int epicId, String taskTitle, String taskDescription) {
        if (tasks.containsKey(epicId) && tasks.get(epicId).getClass().equals(EpicTask.class)) {
            EpicTask task = (EpicTask) tasks.get(epicId);
            Subtask subTask = new Subtask(taskTitle, taskDescription);
            task.addSubTask(subTask);
        }
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
    public HashMap<Integer, Subtask> getSubTasksOfEpicById(int epicId) {
        HashMap<Integer, Subtask> subtasks = null;
        if (tasks.get(epicId) instanceof EpicTask) {
            EpicTask task = (EpicTask) tasks.get(epicId);
            subtasks = task.getSubTasks();
        }
        return subtasks; // maybe null
    }

    @Override
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