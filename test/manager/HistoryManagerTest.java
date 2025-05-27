package manager;

import task.*;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    private HistoryManager historyManager;
    private Task task1, task2, task3;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
        task1 = new Task("Task 1", "Description");
        task2 = new Task("Task 2", "Description");
        task3 = new Task("Task 3", "Description");

        task1.setId(1);
        task2.setId(2);
        task3.setId(3);
    }

    @Test
    void shouldReturnEmptyHistoryWhenNoTasks() {
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void shouldAddTasksAndRemoveOneCorrectly() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        assertEquals(3, historyManager.getHistory().size());

        historyManager.remove(task2.getId());
        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task3, history.get(1));
        assertFalse(history.contains(task2), "Removed task should not be present in history");
    }

    @Test
    void shouldNotAddDuplicateTasks() {
        historyManager.add(task1);
        historyManager.add(task1);
        historyManager.add(task1);

        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    void shouldRemoveTasksFromDifferentPositions() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task1.getId());
        historyManager.remove(task2.getId());
        historyManager.remove(task3.getId());

        assertTrue(historyManager.getHistory().isEmpty());
    }
}
