package Utils;

import Model.EpicTask;
import Model.Subtask;
import Model.Task;
import Model.TaskType;

import java.util.StringJoiner;

public class SupportFunctions {

    /**
     * Преобразует задачу в строку, которая содержит все данные о данный задаче
     * @param task - задача, подлежащая преобразованию
     * @return - возвращает строку с данными о задаче
     */
    public String taskToString(Task task) {
        StringJoiner taskInString = new StringJoiner(",");
        taskInString.add(String.valueOf(task.getTaskIdNumber()));
        taskInString.add(getTaskTypeInString(task));
        taskInString.add(task.getTaskTitle());
        taskInString.add(String.valueOf(task.getTaskStatus()));
        taskInString.add(task.getTaskDescription());
        taskInString.add(getEpicIdOfSubtask(task));
        return taskInString + "\n";
    }

    /**
     * Преобразует текущий статус задачи в строку
     * @param task - задача, статус которой будет обрабатываться
     * @return - возвращает статус в виде текстовой строки
     */
    private String getTaskTypeInString(Task task) {
        if (task instanceof EpicTask) {
            return TaskType.EPIC.toString();
        } else if (task instanceof Subtask) {
            return TaskType.SUBTASK.toString();
        }
        return TaskType.TASK.toString();
    }

    /**
     * Получает номер эпика у выбранной подзадачи в виде текстовой строки
     * @param task - подзадача для преобразования номера эпика
     * @return - возвращает номер эпика в виде текстовой строки
     */
    private String getEpicIdOfSubtask(Task task) {
        if (task instanceof Subtask) {
            return String.valueOf(((Subtask) task).getEpicId());
        }
        return " ";
    }
}