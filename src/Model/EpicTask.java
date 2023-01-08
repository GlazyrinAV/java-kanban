package Model;
import java.util.HashMap;

public class EpicTask extends Task {
    private final HashMap<Integer, Subtask> subTasks = new HashMap<>();

    /**
     * Конструктор для создания новых эпиков
     * Присваивается новый порядковый номер
     * @param taskTitle       - название эпика
     * @param taskDescription - описание эпика
     */
    public EpicTask(String taskTitle, String taskDescription) {
        super(taskTitle, taskDescription);
        updateStatus();
    }

    /**
     * Конструктор для обновления эпика
     * Номер остается прежним и указывается при обновлении
     * @param taskTitle       - название эпика
     * @param taskDescription - описание эпика
     * @param taskIdNumber    - номер обновляемого эпика
     */
    public EpicTask(String taskTitle, String taskDescription, int taskIdNumber) {
        super(taskTitle, taskDescription, taskIdNumber);
        updateStatus();
    }

    /**
     * Метод получает статус эпика на основании статусов входящих в него подзадач
     */
    private void updateStatus() {
        int sum = 0;
        if (subTasks.isEmpty()) {
            setTaskStatus(TaskStatus.Status.NEW);
        } else {
            for (Integer subTask : subTasks.keySet()) {
                TaskStatus.Status taskStatus = subTasks.get(subTask).getTaskStatus();
                switch (taskStatus) {
                    case NEW: {
                        sum += 0;
                        break;
                    }
                    case IN_PROGRESS: {
                        sum += 1;
                        break;
                    }
                    case DONE: {
                        sum += 2;
                        break;
                    }
                }
            }
            if (sum == 0) setTaskStatus(TaskStatus.Status.NEW);
            else if (sum == (subTasks.size() * 2)) setTaskStatus(TaskStatus.Status.DONE);
            else setTaskStatus(TaskStatus.Status.IN_PROGRESS);
        }
    }

    /**
     * Метод добавляет подзадачу к конкретному эпику и обновляет статус Эпика
     * @param task - подзадача, которая будет добавлена к эпику
     */
    public void addSubTask(Subtask task) {
        subTasks.put(task.getTaskIdNumber(), task);
        updateStatus();
    }

    public void removeSubTask(Integer subTaskID) {
        subTasks.remove(subTaskID);
        updateStatus();
    }

    public HashMap<Integer, Subtask> getSubTasks() {
        return subTasks;
    }

    @Override
    public String toString() {
        String result = "\n№" + getTaskIdNumber() + ". Эпик" +
                ". Название задачи - " + getTaskTitle() +
                ". Описание задачи: " + getTaskDescription() +
                ". Статус задачи: " + getTaskStatus();
        if (subTasks.isEmpty()) {
            result = result + ". Подзадачи отсутствуют.";
        } else {
            result = result + ". Эпик содержит следующие подзадачи: \n" + subTasks.values();
        }
        return  result;
    }
}