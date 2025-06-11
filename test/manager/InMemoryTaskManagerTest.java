package manager;

import exception.ManagerSaveException;
import org.junit.jupiter.api.Test;
import task.Status;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    private final LocalDateTime baseTime = LocalDateTime.of(2023, 1, 1, 0, 0);

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @Test
    void shouldReturnPrioritizedTasks_WhenMultipleTasksWithDifferentStartTimes() {
        Task earlyTask = new Task("Early", "Description");
        earlyTask.setId(4);
        earlyTask.setStartTime(baseTime.minusHours(1));
        earlyTask.setDuration(Duration.ofHours(1));

        taskManager.addTask(task);
        taskManager.addTask(earlyTask);

        List<Task> prioritized = taskManager.getPrioritizedTasks();
        assertEquals(2, prioritized.size());
        assertEquals(earlyTask, prioritized.get(0));
        assertEquals(task, prioritized.get(1));
    }

    @Test
    void shouldDetectTimeOverlap_WhenTasksOverlap() {
        LocalDateTime manualBaseTime = LocalDateTime.of(2024, 1, 1, 10, 0);

        Task manualTask = new Task("Base", "Description");
        manualTask.setStartTime(manualBaseTime);
        manualTask.setDuration(Duration.ofMinutes(30));
        taskManager.addTask(manualTask);

        Task overlapTask = new Task("Overlap", "Description");
        overlapTask.setStartTime(manualBaseTime.plusMinutes(15));
        overlapTask.setDuration(Duration.ofMinutes(30));

        assertTrue(taskManager.hasTimeOverlap(overlapTask));

        Task noOverlapTask = new Task("NoOverlap", "Description");
        noOverlapTask.setStartTime(manualBaseTime.plusHours(2));
        noOverlapTask.setDuration(Duration.ofMinutes(30));

        assertFalse(taskManager.hasTimeOverlap(noOverlapTask));
    }

    @Test
    void shouldSetEpicStatusNew_WhenEpicHasNoSubtasks() {
        taskManager.addEpic(epic);
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void shouldLinkSubtaskToEpic_WhenSubtaskAdded() {
        taskManager.addEpic(epic);
        taskManager.addSubtasks(subtask);
        assertEquals(epic.getId(), subtask.getEpicId());
    }

    @Test
    void shouldNotThrow_WhenTasksDoNotOverlap() {
        Task task1 = new Task("Task 1", "Description");
        task1.setStartTime(baseTime);
        task1.setDuration(Duration.ofHours(1));

        Task task2 = new Task("Task 2", "Description");
        task2.setStartTime(baseTime.plusHours(2));
        task2.setDuration(Duration.ofHours(1));

        taskManager.addTask(task1);
        assertDoesNotThrow(() -> taskManager.addTask(task2));
    }

    @Test
    void shouldThrow_WhenTasksFullyOverlap() {
        Task task1 = new Task("Task 1", "Description");
        task1.setStartTime(baseTime);
        task1.setDuration(Duration.ofHours(1));

        Task task2 = new Task("Task 2", "Description");
        task2.setStartTime(baseTime);
        task2.setDuration(Duration.ofHours(1));

        taskManager.addTask(task1);
        assertThrows(ManagerSaveException.class, () -> taskManager.addTask(task2));
    }

    @Test
    void shouldThrow_WhenTasksPartiallyOverlap() {
        Task task1 = new Task("Task 1", "Description");
        task1.setStartTime(baseTime);
        task1.setDuration(Duration.ofHours(1));

        Task task2 = new Task("Task 2", "Description");
        task2.setStartTime(baseTime.plusMinutes(30));
        task2.setDuration(Duration.ofHours(1));

        taskManager.addTask(task1);
        assertThrows(ManagerSaveException.class, () -> taskManager.addTask(task2));
    }

}
