package Utils;

import Manager.HistoryManager;
import Model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Loader {

    /**
     * Воссоздает задачи на основании данных из файла-хранилища
     *
     * @param list  - данные из файла-хранилища
     * @param tasks - хранилище задач в оперативной памяти
     */
    public void loadTaskFromStorage(List<String[]> list, HashMap<Integer, Task> tasks) {
        String taskType;
        for (int i = 0; i < list.size() - 2; i++) {
            taskType = list.get(i)[1];
            if (!taskType.equals("SUBTASK")) createTaskFromDataFile(list.get(i), tasks);
        }
        for (int i = 0; i < list.size() - 2; i++) {
            taskType = list.get(i)[1];
            if (taskType.equals("SUBTASK")) createTaskFromDataFile(list.get(i), tasks);
        }
    }

    /**
     * Воссоздает историю просмотров на основании данных из файла-хранилища
     *
     * @param list           - данные из файла-хранилища
     * @param historyManager - экземпляр класса менеджера историй
     * @param tasks          - хранилище задач в оперативной памяти
     */
    public void loadHistoryFromStorage(List<String[]> list, HistoryManager historyManager,
                                       HashMap<Integer, Task> tasks) {
        String[] historyFromStorage = list.get(list.size() - 1);
        for (String taskId : historyFromStorage) {
            historyManager.addHistory(tasks.get(Integer.parseInt(taskId)));
        }
    }

    /**
     * Выделяет блок информации о задачах и разделяет строчные данные на элементы массива
     *
     * @param list - выгруженные данные из файла-хранилища
     * @return - возвращает лист с массивами данных. Каждая строка содержит массив с информацией об одной задаче
     */
    public List<String[]> getTasksFromDataFile(List<String> list) {
        List<String[]> dataSeparated = new ArrayList<>();
        for (int i = 1; i < list.size(); i++) {
            dataSeparated.add(list.get(i).split(","));
        }
        return dataSeparated;
    }

    /**
     * Выделяет блок информации об истории просмотров и разделяет данные на элементы массива
     *
     * @param list - выгруженные данные из файла-хранилища
     * @return - возращает массив, элементы которого являются ID просмотренных задач
     */
    public List<String[]> getHistoryFromDataFile(List<String> list) {
        List<String[]> dataSeparated = new ArrayList<>();
        dataSeparated.add(list.get(list.size() - 1).split(","));
        return dataSeparated;
    }

    /**
     * Преобразует данные из строчного массива в объект класса Task
     *
     * @param line  - строчный массив с данными по одной задаче
     * @param tasks - хранилище задач в оперативной памяти
     */
    private void createTaskFromDataFile(String[] line, HashMap<Integer, Task> tasks) {
        String taskTitle = line[2];
        TaskStatus taskStatus = TaskStatus.valueOf(line[3]);
        String taskDescription = line[4];
        int taskId = Integer.parseInt(line[0]);
        boolean isTask = line[1].equals("TASK");
        boolean isEpic = line[1].equals("EPIC");
        boolean isSubTask = line[1].equals("SUBTASK");
        if (isTask) tasks.put(taskId, new SimpleTask(taskTitle, taskDescription, taskStatus, taskId));
        else if (isEpic) tasks.put(taskId, new EpicTask(taskTitle, taskDescription, taskStatus, taskId));
        else if (isSubTask) {
            int subtaskEpicId = Integer.parseInt(line[5]);
            tasks.put(taskId, new Subtask(taskTitle, taskDescription, taskStatus, taskId, subtaskEpicId));
            ((EpicTask) tasks.get(subtaskEpicId)).addSubTask(taskId, taskStatus);
        }
    }
}
