package manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import task.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InMemoryTaskHistoryManagerTest {
    private HistoryManager historyManager;
    private Task taskA;
    private Task taskB;
    private Task taskC;
    private Task taskD;

    @BeforeEach
    void beforeEachTest() {
        historyManager = Managers.getDefaultHistory();
        taskA = new Task("Задача А", "Описание задачи A");
        taskB = new Task("Задача B", "Описание задачи B");
        taskC = new Task("Задача C", "Описание задачи C");
        taskD = new Task("Задача D", "Описание задачи D");
        taskA.setId(1);
        taskB.setId(2);
        taskC.setId(3);
        taskD.setId(4);
    }

    @Test
    void shouldInitializeWithEmptyHistory() {
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void shouldAddTaskToHistoryWhenTracked() {
        historyManager.add(taskA);
        historyManager.add(taskB);
        historyManager.add(taskC);
        assertFalse(historyManager.getHistory().isEmpty());
        assertEquals(3, historyManager.getHistory().size());
    }

    @Test
    void shouldMaintainImmutableHistoryWhenOriginalTaskIsModified() {
        historyManager.add(taskA);
        taskA.setDescription("Описание задачи B изменена");
        assertNotEquals(historyManager.getHistory().getFirst().getDescription(), taskA.getDescription());
    }

    @Test
    void shouldUpdateTaskPositionToLastWhenTrackedAgain() {
        historyManager.add(taskB);
        historyManager.add(taskC);
        historyManager.add(taskA);
        List<Task> expectedOrder = Arrays.asList(taskB, taskC, taskA);
        assertEquals(expectedOrder, historyManager.getHistory());
    }

    @Test
    void shouldMaintainUniqueTasksInHistoryWithLastAccessOrder() {
        historyManager.add(taskA);
        historyManager.add(taskB);
        historyManager.add(taskC);
        historyManager.add(taskC);
        historyManager.add(taskB);
        historyManager.add(taskA);
        assertFalse(hasDuplicates(historyManager.getHistory()));
        List<Task> expectedOrder = Arrays.asList(taskC, taskB, taskA);
        assertEquals(expectedOrder, historyManager.getHistory());
    }

    @Test
    void shouldMaintainCorrectOrderWithoutDuplicatesAfterTaskRemoval() {
        historyManager.add(taskA);
        historyManager.add(taskB);
        historyManager.add(taskC);
        historyManager.remove(taskA.getId());
        historyManager.add(taskD);
        historyManager.remove(taskC.getId());
        assertFalse(hasDuplicates(historyManager.getHistory()));
        List<Task> expectedOrder = Arrays.asList(taskB, taskD);
        assertEquals(expectedOrder, historyManager.getHistory());
    }

    // Utility method for search duplicates in ArrayList
    public <T> boolean hasDuplicates(ArrayList<T> list) {
        Set<T> set = new HashSet<>();
        for (T item : list) {
            if (!set.add(item)) {
                return true;
            }
        }
        return false;
    }
}