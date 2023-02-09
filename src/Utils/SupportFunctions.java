package Utils;

import Model.EpicTask;
import Model.Subtask;
import Model.Task;
import Model.TaskType;

import java.util.StringJoiner;

public class SupportFunctions {
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

    private String getTaskTypeInString(Task task) {
        if (task instanceof EpicTask) {
            return TaskType.EPIC.toString();
        } else if (task instanceof Subtask) {
            return TaskType.SUBTASK.toString();
        }
        return TaskType.TASK.toString();
    }

    private String getEpicIdOfSubtask(Task task) {
        if (task instanceof Subtask) {
            return String.valueOf(((Subtask) task).getEpicId());
        }
        return " ";
    }
}