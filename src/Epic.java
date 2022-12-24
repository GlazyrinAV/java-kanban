import java.util.HashMap;
import java.util.Objects;

public class Epic extends Task {
    private HashMap<Integer, Subtask> subTasks = new HashMap<>();

    /**
     * Конструктор для создания новых эпиков
     * Присваивается новый порядковый номер
     * @param taskTitle - название эпика
     * @param taskDescription - описание эпика
     */
    public Epic(String taskTitle, String taskDescription) {
        super(taskTitle, taskDescription);
        updateStatus(subTasks);
    }

    /**
     * Конструктор для обновления эпика
     * Номер остается прежним и указывается при обновлении
     * @param taskTitle - название эпика
     * @param taskDescription - описание эпика
     * @param taskIdNumber - номер обновляемого эпика
     */
    public Epic(String taskTitle, String taskDescription, int taskIdNumber) {
        super(taskTitle, taskDescription, taskIdNumber);
        updateStatus(subTasks);
    }

    /**
     * Метод получает статус эпика на основании статусов входящих в него подзадач
     * @param subTasks - перечень подзадач в конкретном Эпике
     */
    private void updateStatus(HashMap<Integer, Subtask> subTasks) {
        int sum = 0;
        if (subTasks.isEmpty()) {
            setTaskStatus(TaskStatus.NEW);
        } else {
            for (Integer subTask : subTasks.keySet()) {
                TaskStatus taskStatus = subTasks.get(subTask).getTaskStatus();
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
            if (sum == 0) setTaskStatus(TaskStatus.NEW);
            else if (sum == (subTasks.size() * 2)) setTaskStatus(TaskStatus.DONE);
            else setTaskStatus(TaskStatus.IN_PROGRESS);
        }
    }

    /**
     * Метод добавляет подзадачу к конкретному эпику и обновляет статус Эпика
     * @param task - подзадача, которая будет добавлена к эпику
     */
    protected void addSubTask(Subtask task) {
        subTasks.put(task.getTaskIdNumber(), task);
        updateStatus(subTasks);
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