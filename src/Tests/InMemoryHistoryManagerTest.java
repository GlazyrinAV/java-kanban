package Tests;

import Manager.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;

public class InMemoryHistoryManagerTest {
    @BeforeEach
    public void createHistoryManager() {
        InMemoryHistoryManager testHistoryManager = new InMemoryHistoryManager();
        testHistoryManager.addHistory(1);
        testHistoryManager.addHistory(2);
        testHistoryManager.addHistory(3);
    }
}