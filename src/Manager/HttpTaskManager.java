package Manager;

public class HttpTaskManager extends FileBackedTasksManager{
    public HttpTaskManager(InMemoryHistoryManager history) {
        super(history);
    }
}
