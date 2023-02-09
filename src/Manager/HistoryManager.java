package Manager;

import java.util.Collection;

public interface HistoryManager {
    /**
     * Добавляет последнюю вызванную задачу в историю
     * @param id - номер просмотренного таска
     */
    void addHistory(int id);

    /**
     * Удаляет задачу из истории
     * @param id - номер задачи для удаления
     */
    void removeHistory(int id);

    /**
     * Метод получения истории о последних вызванных тасках
     * @return - возвращает лист с последними вызванными тасками
     */
    Collection<Integer> getHistory();
}