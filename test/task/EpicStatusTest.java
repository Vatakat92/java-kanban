package task;

import manager.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class EpicStatusTest {
    private TaskManager taskManager;
    private Epic epic;
    private Subtask sub1, sub2;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
        epic = new Epic("Epic", "Description");
        taskManager.addEpic(epic);

        sub1 = new Subtask("Subtask1", "Description");
        sub2 = new Subtask("Subtask2", "Description");
        sub1.setEpicId(epic.getId());
        sub2.setEpicId(epic.getId());
    }

    @Test
    void shouldSetStatusNew_WhenAllSubtasksAreNew() {
        sub1.setStatus(Status.NEW);
        sub2.setStatus(Status.NEW);
        taskManager.addSubtasks(sub1);
        taskManager.addSubtasks(sub2);

        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void shouldSetStatusDone_WhenAllSubtasksAreDone() {
        sub1.setStatus(Status.DONE);
        sub2.setStatus(Status.DONE);
        taskManager.addSubtasks(sub1);
        taskManager.addSubtasks(sub2);

        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    void shouldSetStatusInProgress_WhenSubtasksHaveDifferentStatuses() {
        sub1.setStatus(Status.NEW);
        sub2.setStatus(Status.DONE);
        taskManager.addSubtasks(sub1);
        taskManager.addSubtasks(sub2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void shouldSetStatusInProgress_WhenAllSubtasksInProgress() {
        sub1.setStatus(Status.IN_PROGRESS);
        sub2.setStatus(Status.IN_PROGRESS);
        taskManager.addSubtasks(sub1);
        taskManager.addSubtasks(sub2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void shouldSetStatusNew_WhenEpicHasNoSubtasks() {
        Epic emptyEpic = new Epic("Empty", "No subtasks");
        taskManager.addEpic(emptyEpic);
        assertEquals(Status.NEW, emptyEpic.getStatus(), "Epic без подзадач должен иметь статус NEW");
    }
}
