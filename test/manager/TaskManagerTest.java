package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected Task task;
    protected Epic epic;
    protected Subtask subtask;

    protected abstract T createTaskManager() throws IOException;

    @BeforeEach
    void setUp() throws IOException {
        taskManager = createTaskManager();
        LocalDateTime base = LocalDateTime.of(2024, 1, 1, 10, 0);
        task = new Task("Test Task", "Test Description", Status.NEW, null);
        task.setDuration(Duration.ofDays(60));
        task.setStartTime(base);
        epic = new Epic("Test Epic", "Test Epic Description");
        subtask = new Subtask("Test Subtask", "Test Subtask Description");
        subtask.setDuration(Duration.ofDays(30));
        subtask.setStartTime(base.plusDays(61));
        subtask.setEpicId(1);
        subtask.setStatus(Status.NEW);
    }

    @Test
    void shouldAddTask() {
        taskManager.addTask(task);
        final Task savedTask = taskManager.getTaskById(task.getId());
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
        final List<Task> tasks = taskManager.getTasksIdList();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void shouldAddEpic() {
        taskManager.addEpic(epic);
        final Epic savedEpic = taskManager.getEpicById(epic.getId());
        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");
        final List<Epic> epics = taskManager.getEpicsIdList();
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.getFirst(), "Эпики не совпадают.");
    }

    @Test
    void shouldAddSubtaskAndLinkToEpic() {
        taskManager.addEpic(epic);
        taskManager.addSubtasks(subtask);
        final Subtask savedSubtask = taskManager.getSubtaskById(subtask.getId());
        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");
        final List<Integer> epicSubtasks = taskManager.getEpicSubtasksList(epic.getId());
        assertNotNull(epicSubtasks, "Список подзадач эпика не возвращается.");
        assertEquals(1, epicSubtasks.size(), "Неверное количество подзадач у эпика.");
        assertEquals(subtask.getId(), epicSubtasks.getFirst(), "ID подзадачи не совпадает.");
    }

    @Test
    void shouldRemoveTaskById() {
        taskManager.addTask(task);
        taskManager.removeTaskById(task.getId());
        assertNull(taskManager.getTaskById(task.getId()), "Задача не удалена.");
        assertEquals(0, taskManager.getTasksIdList().size(), "Список задач не пуст.");
    }

    @Test
    void shouldRemoveEpicAndSubtasksById() {
        taskManager.addEpic(epic);
        taskManager.addSubtasks(subtask);
        taskManager.removeEpicById(epic.getId());
        assertNull(taskManager.getEpicById(epic.getId()), "Эпик не удален.");
        assertNull(taskManager.getSubtaskById(subtask.getId()), "Подзадача не удалена.");
        assertEquals(0, taskManager.getEpicsIdList().size(), "Список эпиков не пуст.");
        assertEquals(0, taskManager.getSubtasksIdList().size(), "Список подзадач не пуст.");
    }

    @Test
    void shouldRemoveSubtaskById() {
        taskManager.addEpic(epic);
        taskManager.addSubtasks(subtask);
        taskManager.removeSubtaskById(subtask.getId());
        assertNull(taskManager.getSubtaskById(subtask.getId()), "Подзадача не удалена.");
        assertEquals(0, taskManager.getSubtasksIdList().size(), "Список подзадач не пуст.");
        assertEquals(0, taskManager.getEpicSubtasksList(epic.getId()).size(), "Подзадача не удалена из эпика.");
    }

    @Test
    void shouldUpdateTask() {
        LocalDateTime base = LocalDateTime.of(2024, 1, 1, 10, 0);
        Task task = new Task("Task", "Description", Status.NEW, null);
        task.setDuration(Duration.ofDays(5));
        task.setStartTime(base);
        taskManager.addTask(task);
        Task updatedTask = new Task("Updated", "Updated", Status.IN_PROGRESS, task.getId());
        updatedTask.setDuration(Duration.ofDays(5));
        updatedTask.setStartTime(base.plusDays(7));
        taskManager.updateTask(updatedTask);
        final Task savedTask = taskManager.getTaskById(task.getId());
        assertEquals("Updated", savedTask.getName(), "Название задачи не обновлено.");
        assertEquals("Updated", savedTask.getDescription(), "Описание задачи не обновлено.");
        assertEquals(Status.IN_PROGRESS, savedTask.getStatus(), "Статус задачи не обновлен.");
    }

    @Test
    void shouldUpdateEpic() {
        taskManager.addEpic(epic);
        Epic updatedEpic = new Epic("Updated", "Updated");
        updatedEpic.setId(epic.getId());
        taskManager.updateEpic(updatedEpic);
        final Epic savedEpic = taskManager.getEpicById(epic.getId());
        assertEquals("Updated", savedEpic.getName(), "Название эпика не обновлено.");
        assertEquals("Updated", savedEpic.getDescription(), "Описание эпика не обновлено.");
    }

    @Test
    void shouldUpdateSubtaskAndEpicStatus() {
        Epic epic = new Epic("Epic", "Description");
        taskManager.addEpic(epic);
        LocalDateTime base = LocalDateTime.of(2024, 1, 1, 10, 0);
        Subtask subtask = new Subtask("Subtask", "Description");
        subtask.setEpicId(epic.getId());
        subtask.setDuration(Duration.ofDays(2));
        subtask.setStartTime(base);
        subtask.setStatus(Status.NEW);
        taskManager.addSubtasks(subtask);
        Subtask updatedSubtask = new Subtask("Updated", "Updated");
        updatedSubtask.setId(subtask.getId());
        updatedSubtask.setDuration(Duration.ofDays(2));
        updatedSubtask.setStartTime(base.plusDays(3));
        updatedSubtask.setEpicId(epic.getId());
        updatedSubtask.setStatus(Status.DONE);
        taskManager.updateSubtask(updatedSubtask);
        final Subtask savedSubtask = taskManager.getSubtaskById(subtask.getId());
        assertEquals("Updated", savedSubtask.getName(), "Название подзадачи не обновлено.");
        assertEquals(Status.DONE, savedSubtask.getStatus(), "Статус подзадачи не обновлен.");
        assertEquals(Status.DONE, taskManager.getEpicById(epic.getId()).getStatus(), "Статус эпика не обновлен.");
    }

    @Test
    void shouldRemoveAllTasks() {
        LocalDateTime base = LocalDateTime.of(2024, 1, 1, 10, 0);
        Task task = new Task("First", "Description");
        task.setDuration(Duration.ofDays(30));
        task.setStartTime(base);
        Task anotherTask = new Task("Another", "Description");
        anotherTask.setDuration(Duration.ofDays(30));
        anotherTask.setStartTime(base.plusDays(31));
        taskManager.addTask(task);
        taskManager.addTask(anotherTask);
        taskManager.deleteTasks();
        assertEquals(0, taskManager.getTasksIdList().size(), "Задачи не удалены.");
    }

    @Test
    void shouldRemoveAllEpicsAndSubtasks() {
        taskManager.addEpic(epic);
        taskManager.addSubtasks(subtask);
        taskManager.addEpic(new Epic("Another", "Description"));
        taskManager.deleteEpics();
        assertEquals(0, taskManager.getEpicsIdList().size(), "Эпики не удалены.");
        assertEquals(0, taskManager.getSubtasksIdList().size(), "Подзадачи не удалены.");
    }

    @Test
    void shouldRemoveAllSubtasks() {
        taskManager.addEpic(epic);
        LocalDateTime base = LocalDateTime.of(2024, 1, 1, 10, 0);
        Subtask subtask = new Subtask("FirstSubtask", "Description");
        subtask.setEpicId(epic.getId());
        subtask.setDuration(Duration.ofDays(30));
        subtask.setStartTime(base);
        Subtask anotherSubtask = new Subtask("Another", "Description");
        anotherSubtask.setDuration(Duration.ofDays(30));
        anotherSubtask.setStartTime(base.plusDays(31));
        anotherSubtask.setEpicId(epic.getId());
        taskManager.addSubtasks(subtask);
        taskManager.addSubtasks(anotherSubtask);
        taskManager.deleteSubtasks();
        assertEquals(0, taskManager.getSubtasksIdList().size(), "Подзадачи не удалены.");
        assertEquals(0, taskManager.getEpicSubtasksList(epic.getId()).size(), "Подзадачи не удалены из эпиков.");
    }


    @Test
    void epicStatusShouldBeNewIfAllSubtasksNew() {
        Epic epic = new Epic("Epic", "Desc");
        taskManager.addEpic(epic);
        Subtask sub1 = new Subtask("Sub1", "Desc");
        sub1.setEpicId(epic.getId());
        sub1.setStatus(Status.NEW);
        sub1.setDuration(Duration.ofDays(1));
        sub1.setStartTime(LocalDateTime.of(2024, 1, 1, 10, 0));
        taskManager.addSubtasks(sub1);
        Subtask sub2 = new Subtask("Sub2", "Desc");
        sub2.setEpicId(epic.getId());
        sub2.setStatus(Status.NEW);
        sub2.setDuration(Duration.ofDays(1));
        sub2.setStartTime(LocalDateTime.of(2024, 1, 2, 10, 0));
        taskManager.addSubtasks(sub2);
        assertEquals(Status.NEW, taskManager.getEpicById(epic.getId()).getStatus(), "Статус эпика должен быть NEW");
    }

    @Test
    void epicStatusShouldBeDoneIfAllSubtasksDone() {
        Epic epic = new Epic("Epic", "Desc");
        taskManager.addEpic(epic);
        Subtask sub1 = new Subtask("Sub1", "Desc");
        sub1.setEpicId(epic.getId());
        sub1.setStatus(Status.DONE);
        sub1.setDuration(Duration.ofDays(1));
        sub1.setStartTime(LocalDateTime.of(2024, 1, 1, 10, 0));
        taskManager.addSubtasks(sub1);
        Subtask sub2 = new Subtask("Sub2", "Desc");
        sub2.setEpicId(epic.getId());
        sub2.setStatus(Status.DONE);
        sub2.setDuration(Duration.ofDays(1));
        sub2.setStartTime(LocalDateTime.of(2024, 1, 2, 10, 0));
        taskManager.addSubtasks(sub2);
        assertEquals(Status.DONE, taskManager.getEpicById(epic.getId()).getStatus(), "Статус эпика должен быть DONE");
    }

    @Test
    void epicStatusShouldBeInProgressIfMixedNewAndDone() {
        Epic epic = new Epic("Epic", "Desc");
        taskManager.addEpic(epic);
        Subtask sub1 = new Subtask("Sub1", "Desc");
        sub1.setEpicId(epic.getId());
        sub1.setStatus(Status.NEW);
        sub1.setDuration(Duration.ofDays(1));
        sub1.setStartTime(LocalDateTime.of(2024, 1, 1, 10, 0));
        taskManager.addSubtasks(sub1);
        Subtask sub2 = new Subtask("Sub2", "Desc");
        sub2.setEpicId(epic.getId());
        sub2.setStatus(Status.DONE);
        sub2.setDuration(Duration.ofDays(1));
        sub2.setStartTime(LocalDateTime.of(2024, 1, 2, 10, 0));
        taskManager.addSubtasks(sub2);
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus(), "Статус эпика должен быть IN_PROGRESS");
    }

    @Test
    void epicStatusShouldBeInProgressIfAnyInProgress() {
        Epic epic = new Epic("Epic", "Desc");
        taskManager.addEpic(epic);
        Subtask sub1 = new Subtask("Sub1", "Desc");
        sub1.setEpicId(epic.getId());
        sub1.setStatus(Status.IN_PROGRESS);
        sub1.setDuration(Duration.ofDays(1));
        sub1.setStartTime(LocalDateTime.of(2024, 1, 1, 10, 0));
        taskManager.addSubtasks(sub1);
        Subtask sub2 = new Subtask("Sub2", "Desc");
        sub2.setEpicId(epic.getId());
        sub2.setStatus(Status.NEW);
        sub2.setDuration(Duration.ofDays(1));
        sub2.setStartTime(LocalDateTime.of(2024, 1, 2, 10, 0));
        taskManager.addSubtasks(sub2);
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus(), "Статус эпика должен быть IN_PROGRESS");
    }

    @Test
    void epicFieldsShouldBeCalculatedFromSubtasks() {
        Epic epic = new Epic("Epic", "Desc");
        taskManager.addEpic(epic);
        Subtask sub1 = new Subtask("Sub1", "Desc");
        sub1.setEpicId(epic.getId());
        sub1.setStatus(Status.NEW);
        sub1.setDuration(Duration.ofDays(2));
        sub1.setStartTime(LocalDateTime.of(2024, 1, 1, 10, 0));
        taskManager.addSubtasks(sub1);
        Subtask sub2 = new Subtask("Sub2", "Desc");
        sub2.setEpicId(epic.getId());
        sub2.setStatus(Status.NEW);
        sub2.setDuration(Duration.ofDays(3));
        sub2.setStartTime(LocalDateTime.of(2024, 1, 4, 10, 0));
        taskManager.addSubtasks(sub2);
        Epic savedEpic = taskManager.getEpicById(epic.getId());
        assertEquals(Duration.ofDays(5), savedEpic.getDuration(), "Продолжительность эпика должна быть суммой подзадач");
        assertEquals(LocalDateTime.of(2024, 1, 1, 10, 0), savedEpic.getStartTime(), "Дата старта эпика должна быть самой ранней из подзадач");
        assertEquals(LocalDateTime.of(2024, 1, 7, 10, 0), savedEpic.getEndTime(), "Дата окончания эпика должна быть самой поздней из подзадач");
    }
}
