package Tests;

import Manager.InMemoryHistoryManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class InMemoryHistoryManagerTest {
    InMemoryHistoryManager testHistoryManager;
    @BeforeEach
    public void createHistoryManager() {
        testHistoryManager = new InMemoryHistoryManager();
    }

    @DisplayName("Получение пустой истории")
    @Test
    public void getHistoryWithNoTasks() {
        Assertions.assertEquals(testHistoryManager.getHistory(), new ArrayList<>(),
                "Ошибка при получении пустой истории.");
    }

    @DisplayName("Получение истории с записями")
    @Test
    public void getHistoryWithTasks() {
        createHistoryData();
        Assertions.assertEquals(testHistoryManager.getHistory(), new ArrayList<>(Arrays.asList(1, 2, 3)),
                "Ошибка при получении истории с задачами.");
    }

    @DisplayName("Перезапись существующей записи истории")
    @Test
    public void reWritingExcitingHistory() {
        createHistoryData();
        testHistoryManager.addHistory(1);
        Assertions.assertEquals(testHistoryManager.getHistory(), new ArrayList<>(Arrays.asList(2, 3, 1)),
                "Ошибка при перезаписи существующей записи.");
    }

    @DisplayName("Удаление первой истории")
    @Test
    public void deletingHistoryFromBeginning() {
        createHistoryData();
        testHistoryManager.removeHistory(1);
        Assertions.assertEquals(testHistoryManager.getHistory(), new ArrayList<>(Arrays.asList(2, 3)),
                "Ошибка при удалении первой записи.");
    }

    @DisplayName("Удаление истории из середины")
    @Test
    public void deletingHistoryFromMiddle() {
        createHistoryData();
        testHistoryManager.removeHistory(2);
        Assertions.assertEquals(testHistoryManager.getHistory(), new ArrayList<>(Arrays.asList(1, 3)),
                "Ошибка при удалении записи из середины.");
    }

    @DisplayName("Удаление последней истории")
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