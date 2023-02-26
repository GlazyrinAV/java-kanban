package Tests;

import Manager.InMemoryHistoryManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class InMemoryHistoryManagerTest {
    InMemoryHistoryManager testHistoryManager;
    @BeforeEach
    public void createHistoryManager() {
        testHistoryManager = new InMemoryHistoryManager();
    }

    // 1. Получение пустой истории
    @Test
    public void getHistoryWithNoTasks() {
        Assertions.assertEquals(testHistoryManager.getHistory(), new ArrayList<>(),
                "Ошибка при получении пустой истории.");
    }

    // 2. Получение истории с записями
    @Test
    public void getHistoryWithTasks() {
        createHistoryData();
        Assertions.assertEquals(testHistoryManager.getHistory(), new ArrayList<>(Arrays.asList(1, 2, 3)),
                "Ошибка при получении истории с задачами.");
    }

    // 3. Перезапись существующей записи
    @Test
    public void reWritingExcitingHistory() {
        createHistoryData();
        testHistoryManager.addHistory(1);
        Assertions.assertEquals(testHistoryManager.getHistory(), new ArrayList<>(Arrays.asList(2, 3, 1)),
                "Ошибка при перезаписи существующей записи.");
    }

    // 4. Удаление первой записи
    @Test
    public void deletingHistoryFromBeginning() {
        createHistoryData();
        testHistoryManager.removeHistory(1);
        Assertions.assertEquals(testHistoryManager.getHistory(), new ArrayList<>(Arrays.asList(2, 3)),
                "Ошибка при удалении первой записи.");
    }

    // 5. Удаление записи из середины
    @Test
    public void deletingHistoryFromMiddle() {
        createHistoryData();
        testHistoryManager.removeHistory(2);
        Assertions.assertEquals(testHistoryManager.getHistory(), new ArrayList<>(Arrays.asList(1, 3)),
                "Ошибка при удалении записи из середины.");
    }

    // 6. Удаление последней записи
    @Test
    public void deletingHistoryFromEnd() {
        createHistoryData();
        testHistoryManager.removeHistory(3);
        Assertions.assertEquals(testHistoryManager.getHistory(), new ArrayList<>(Arrays.asList(1, 2)),
                "Ошибка при удалении последней записи.");
    }

    private void createHistoryData() {
        testHistoryManager.addHistory(1);
        testHistoryManager.addHistory(2);
        testHistoryManager.addHistory(3);
    }
}