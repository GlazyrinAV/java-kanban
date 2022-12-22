import java.util.HashMap;

public class Epic extends Task {
    private HashMap<Integer, Subtask> subTasks;
    public Epic(String taskTitle, String taskDescription) {
        super(taskTitle, taskDescription);
        subTasks = new HashMap<>();
        checkStatus(subTasks);
    }

    /**
     * Метод получает статус эпика на основании статусов входящих в него подзадач
     * @param subTasks - перечень подзадач в конкретном Эпике
     */
    private void checkStatus (HashMap<Integer, Subtask> subTasks) {
        if (subTasks.isEmpty()) {
            setTaskStatus(0);
        } else {
            int[] subTasksStatus = new int[subTasks.size()];
            int i = 0;
            for (Integer subTask : subTasks.keySet()) {
                int taskStatus = subTasks.get(subTask).getTaskStatus();
                subTasksStatus[i] = taskStatus;
                i++;
            }
            int sum = 0;
            for (int j = 0; j < subTasksStatus.length; j++) {
                sum += subTasksStatus[j];
            }
            if (sum == 0) setTaskStatus(0);
            else if (sum == (subTasksStatus.length * 2)) setTaskStatus(2);
            else setTaskStatus(1);
        }
    }

    /**
     * Метод добавляет подзадачу к конкретному эпику
     * @param task
     */
    public void addSubTask(Subtask task) {
        subTasks.put(task.getTaskIdNumber(), task);
        checkStatus(subTasks);
    }

    public HashMap<Integer, Subtask> getSubTasks() {
        return subTasks;
    }

    @Override
    public String toString() {
        String result = "№" + getTaskIdNumber() + ". Эпик" +
                ". Название задачи - " + getTaskTitle() +
                ". Описание задачи: " + getTaskDescription() +
                ". Статус задачи: " + getStatusName(getTaskStatus());
        if (subTasks.isEmpty()) {
            result = result + ". Подзадачи отсутствуют.";
        } else {
            result = result + ". Эпик содержит следующие подзадачи: \n" + subTasks.values().toString();
        }
        return  result;
    }
}
