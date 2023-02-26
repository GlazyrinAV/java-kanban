package Utils;

import Exceptions.UtilsExceptions;
import Manager.HistoryManager;
import Model.*;

import java.util.*;

public class Loader {

    /**
     * Воссоздает задачи на основании данных из файла-хранилища
     *
     * @param list  - данные из файла-хранилища
     * @param tasks - хранилище задач в оперативной памяти
     */
    public void loadTaskFromStorage(List<String[]> list, HashMap<Integer, Task> tasks)
            throws UtilsExceptions.NoEpicForSubTaskException {
        String taskType;
        for (String[] strings : list) {
            boolean isEndOfTasks = strings[0].equals(" ");
            if (isEndOfTasks) break;
            int taskId = Integer.parseInt(strings[0]);
            taskType = strings[1];
            try {
                if (taskType.equals("SUBTASK") && (tasks.get(Integer.parseInt(strings[5])) == null)) {
                    int epicId = Integer.parseInt(strings[5]);
                    throw new UtilsExceptions.NoEpicForSubTaskException(epicId);
                }
                if (tasks.get(taskId) == null) createTaskFromDataFile(strings, tasks);
            } catch (UtilsExceptions.NoEpicForSubTaskException e) {
                int epicId = UtilsExceptions.NoEpicForSubTaskException.getEpicId();
                System.out.println("Для подзадачи " + taskId + " не найден эпик " + epicId +
                        ". Предпринимается попытка найти данный эпик.");
                for (String[] epic : list) {
                    if (Integer.parseInt(epic[0]) == epicId) {
                        createTaskFromDataFile(epic, tasks);
                        System.out.println("Эпик " + epicId + " был найден и добавлен.");
                        return;
                    }
                }
                System.out.println("Не удалось найти эпик " + epicId + ". Подзадача " + taskId + " не добавлена.");
            }
        }
    }

    /**
     * Воссоздает историю просмотров на основании данных из файла-хранилища
     * @param list           - данные из файла-хранилища
     * @param historyManager - экземпляр класса менеджера историй
     */
    public void loadHistoryFromStorage(List<String[]> list, HistoryManager historyManager) {
        if (!list.isEmpty()) {
            String[] historyFromStorage = list.get(list.size() - 1);
            for (String taskId : historyFromStorage) {
                historyManager.addHistory(Integer.parseInt(taskId));
            }
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
            boolean isEndOfTasks = list.get(i).isBlank();
            if (isEndOfTasks) break;
            dataSeparated.add(list.get(i).split(","));
        }
        dataSeparated.sort(comparator);
        return dataSeparated;
    }

    /**
     * Выделяет блок информации об истории просмотров и разделяет данные на элементы массива
     *
     * @param list - выгруженные данные из файла-хранилища
     * @return - возвращает массив, элементы которого являются ID просмотренных задач
     */
    public List<String[]> getHistoryFromDataFile(List<String> list)
            throws UtilsExceptions.NoHistoryDataInStorageException {
        List<String[]> dataSeparated = new ArrayList<>();
        boolean isErrorInDataFile = !list.get(list.size() - 2).isBlank();
        boolean isHistoryEmpty = list.get(list.size() - 1).isBlank();
        try {
            if (isErrorInDataFile) throw new UtilsExceptions.NoHistoryDataInStorageException();
            if (isHistoryEmpty) return Collections.emptyList();
            dataSeparated.add(list.get(list.size() - 1).split(","));
            return dataSeparated;
        } catch (UtilsExceptions.NoHistoryDataInStorageException e) {
            return Collections.emptyList();
        }
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

    final Comparator<String[]> comparator = Comparator.comparingInt(o -> Integer.parseInt(o[0]));
}