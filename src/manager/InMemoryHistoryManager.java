package manager;

import task.Task;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history = new ArrayList<>();
    private static final int MAX_SIZE = 10;

    @Override
    public void add(Task task) {

        // По условиям ТЗ мы должны провести этот тест:
        // -убедитесь, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных.
        // Без того как это делается сейчас сделать будет затруднительно, если невозможно.
        history.add(new Task(task));
        if (history.size() > MAX_SIZE) {
            history.removeFirst();
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
