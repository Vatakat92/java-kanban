package manager;

import static org.junit.jupiter.api.Assertions.*;

import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InMemoryTaskManagerTest {
    private TaskManager manager;
    private Task taskA;
    private Task taskB;
    private Epic epicA;
    private Epic epicB;
    private Subtask subA;
    private Subtask subB;

    @BeforeEach
    void beforeEachTest() {
        manager = Managers.getDefault();
        taskA = new Task("Задача A", "Описание задачи A");
        taskB = new Task("Задача B", "Описание задачи B");
        epicA = new Epic("Эпик A", "Описание эпика A");
        epicB = new Epic("Эпик B", "Описание эпика B");
        subA = new Subtask("Подзадача A", "Описание подзадачи A");
        subB = new Subtask("Подзадача B", "Описание подзадачи B");
    }


    @Test
    void shouldAddAndFindTasksOfDifferentTypesById() {
        manager.addTask(taskA);
        manager.addEpic(epicA);
        manager.addSubtasks(subA);
        assertEquals(taskA, manager.getTaskById(taskA.getId()));
        assertEquals(epicA, manager.getEpicById(epicA.getId()));
        assertEquals(subA, manager.getSubtaskById(subA.getId()));
    }

    @Test
    void shouldGenerateUniqueIdsForTasks() {
        manager.addTask(taskA);
        manager.addTask(taskB);
        assertEquals(manager.getTaskById(taskB.getId()), manager.getTaskById(taskA.getId() + 1));
    }

    @Test
    void shouldKeepAddedTaskUnmodifiedInManager() {
        manager.addTask(taskA);
        assertEquals(taskA.getName(), manager.getTaskById(taskA.getId()).getName());
        assertEquals(taskA.getDescription(), manager.getTaskById(taskA.getId()).getDescription());
        assertEquals(taskA.getStatus(), manager.getTaskById(taskA.getId()).getStatus());
        assertEquals(taskA.getId(), manager.getTaskById(taskA.getId()).getId());
    }


    @Test
    void shouldChangeEpicStatusToDoneWhenAllSubtasksStatusChangedToDone() {
        manager.addEpic(epicA);
        manager.addSubtasks(subA);
        manager.addSubtasks(subB);
        manager.getEpicById(epicA.getId()).addSubtask(subA.getId());
        manager.getEpicById(epicA.getId()).addSubtask(subB.getId());
        manager.getSubtaskById(subA.getId()).setEpicId(epicA.getId());
        manager.getSubtaskById(subB.getId()).setEpicId(epicA.getId());
        manager.updateEpic(manager.getEpicById(epicA.getId()));
        manager.getSubtaskById(subA.getId()).setStatus(Status.DONE);
        manager.getSubtaskById(subB.getId()).setStatus(Status.DONE);
        manager.updateEpic(manager.getEpicById(epicA.getId()));
        assertEquals(Status.DONE, manager.getEpicById(epicA.getId()).getStatus());
    }

    @Test
    void shouldUpdateEpicStatusToInProgressWhenSubtasksHaveDifferentStatuses() {
        manager.addEpic(epicA);
        manager.addSubtasks(subA);
        manager.addSubtasks(subB);
        manager.getEpicById(epicA.getId()).addSubtask(subA.getId());
        manager.getEpicById(epicA.getId()).addSubtask(subB.getId());
        manager.getSubtaskById(subA.getId()).setEpicId(epicA.getId());
        manager.getSubtaskById(subB.getId()).setEpicId(epicA.getId());
        manager.updateEpic(manager.getEpicById(epicA.getId()));
        manager.getSubtaskById(subA.getId()).setStatus(Status.IN_PROGRESS);
        manager.getSubtaskById(subB.getId()).setStatus(Status.DONE);
        manager.updateEpic(manager.getEpicById(epicA.getId()));
        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epicA.getId()).getStatus());
    }

    @Test
    void shouldClearEpicSubtaskListAndResetStatusWhenLastSubtaskRemoved() {
        manager.addEpic(epicA);
        subA.setStatus(Status.DONE);
        manager.addSubtasks(subA);
        manager.getEpicById(epicA.getId()).addSubtask(subA.getId());
        manager.getSubtaskById(subA.getId()).setEpicId(epicA.getId());
        manager.updateEpic(manager.getEpicById(epicA.getId()));
        assertEquals(Status.DONE, manager.getEpicById(epicA.getId()).getStatus());
        assertFalse(manager.getEpicSubtasksList(epicA.getId()).isEmpty());
        manager.removeSubtaskById(subA.getId());
        assertTrue(manager.getEpicSubtasksList(epicA.getId()).isEmpty());
        assertEquals(Status.NEW, manager.getEpicById(epicA.getId()).getStatus());
    }

    @Test
    void shouldRemoveEpicTaskWithAllItsSubtasks() {
        manager.addEpic(epicA);
        manager.addSubtasks(subA);
        manager.getEpicById(epicA.getId()).addSubtask(subA.getId());
        manager.getSubtaskById(subA.getId()).setEpicId(epicA.getId());
        assertFalse(manager.getEpicsIdList().isEmpty());
        assertFalse(manager.getSubtasksIdList().isEmpty());
        manager.removeEpicById(epicA.getId());
        assertTrue(manager.getEpicsIdList().isEmpty());
        assertTrue(manager.getSubtasksIdList().isEmpty());
    }

    @Test
    void shouldEmptyTaskListWhenAllTasksAreRemoved() {
        manager.addTask(taskA);
        assertFalse(manager.getTasksIdList().isEmpty());
        manager.removeTaskById(taskA.getId());
        assertTrue(manager.getTasksIdList().isEmpty());
    }

    @Test
    void shouldEmptyTaskManagerListWhenRemovingTasksByType() {
        manager.addTask(taskA);
        manager.addEpic(epicA);
        manager.addSubtasks(subA);
        assertFalse(manager.getTasksIdList().isEmpty());
        assertFalse(manager.getEpicsIdList().isEmpty());
        assertFalse(manager.getSubtasksIdList().isEmpty());
        manager.deleteTasks();
        manager.deleteEpics();
        manager.deleteSubtasks();
        assertTrue(manager.getTasksIdList().isEmpty());
        assertTrue(manager.getEpicsIdList().isEmpty());
        assertTrue(manager.getSubtasksIdList().isEmpty());
    }

    @Test
    void shouldUpdateTaskWhenChangesAreMade() {
        manager.addTask(taskA);
        assertEquals("Задача A", manager.getTaskById(taskA.getId()).getName());
        assertEquals("Описание задачи A", manager.getTaskById(taskA.getId()).getDescription());
        taskB.setId(taskA.getId());
        taskB.setName("Обновление задачи A");
        taskB.setDescription("Обновление описания задачи A");
        manager.updateTask(taskB);
        assertEquals("Обновление задачи A", manager.getTaskById(taskA.getId()).getName());
        assertEquals("Обновление описания задачи A", manager.getTaskById(taskA.getId()).getDescription());
        taskB.setName("Обновление задачи B");
        taskB.setDescription("Обновление описания задачи B");
        assertEquals("Обновление задачи A", manager.getTaskById(taskA.getId()).getName());
        assertEquals("Обновление описания задачи A", manager.getTaskById(taskA.getId()).getDescription());
    }

    @Test
    void shouldUpdateEpicTaskPropertiesWhenModified() {
        manager.addEpic(epicA);
        manager.addSubtasks(subA);
        manager.getSubtaskById(subA.getId()).setStatus(Status.IN_PROGRESS);
        manager.getEpicById(epicA.getId()).addSubtask(subA.getId());
        manager.getSubtaskById(subA.getId()).setEpicId(epicA.getId());
        manager.updateEpic(manager.getEpicById(epicA.getId()));
        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epicA.getId()).getStatus());

        subB.setStatus(Status.DONE);
        manager.addSubtasks(subB);
        epicB.setId(epicA.getId());
        epicB.addSubtask(subB.getId());
        manager.updateEpic(epicB);
        assertEquals(Status.DONE, manager.getEpicById(epicA.getId()).getStatus());
    }

    @Test
    void shouldUpdateSubtaskPropertiesWhenModified() {
        manager.addEpic(epicA);
        manager.addSubtasks(subA);
        manager.getSubtaskById(subA.getId()).setStatus(Status.IN_PROGRESS);
        manager.getEpicById(epicA.getId()).addSubtask(subA.getId());
        manager.getSubtaskById(subA.getId()).setEpicId(epicA.getId());
        manager.updateEpic(manager.getEpicById(epicA.getId()));
        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epicA.getId()).getStatus());

        subB.setId(subA.getId());
        subB.setEpicId(epicA.getId());
        subB.setStatus(Status.DONE);
        manager.updateSubtask(subB);
        assertEquals(Status.DONE, manager.getEpicById(epicA.getId()).getStatus());
    }

}