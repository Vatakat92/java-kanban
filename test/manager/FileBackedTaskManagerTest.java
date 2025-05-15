package manager;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import task.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

class FileBackedTaskManagerTest {
    private File tempFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("tasks", ".csv");
        manager = new FileBackedTaskManager(tempFile);
    }
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @AfterEach
    void tearDown() {
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }

    @Test
    void shouldWorkLikeInMemoryManagerForTasks() {
        Task task = new Task("Task", "Description");
        manager.addTask(task);

        Task savedTask = manager.getTaskById(task.getId());
        assertNotNull(savedTask);
        assertEquals(task, savedTask);

        List<Task> tasks = manager.getTasksIdList();
        assertEquals(1, tasks.size());
        assertEquals(task, tasks.getFirst());
    }

    @Test
    void shouldSaveAndLoadEmptyManager() {
        manager.save();
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loadedManager.getTasksIdList().isEmpty());
        assertTrue(loadedManager.getEpicsIdList().isEmpty());
        assertTrue(loadedManager.getSubtasksIdList().isEmpty());
        assertTrue(loadedManager.getHistory().isEmpty());
    }

    @Test
    void shouldSaveAndLoadMultipleTasks() {
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");
        manager.addTask(task1);
        manager.addTask(task2);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        List<Task> loadedTasks = loadedManager.getTasksIdList();

        assertEquals(2, loadedTasks.size());
        assertEquals(task1.getName(), loadedManager.getTaskById(task1.getId()).getName());
        assertEquals(task2.getDescription(), loadedManager.getTaskById(task2.getId()).getDescription());
    }

    @Test
    void shouldSaveAndLoadEpicsWithSubtasks() {
        Epic epic = new Epic("Epic", "Description");
        manager.addEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1");
        subtask1.setEpicId(epic.getId());
        manager.addSubtasks(subtask1);

        Subtask subtask2 = new Subtask("Subtask 2", "Description 2");
        subtask2.setEpicId(epic.getId());
        subtask2.setStatus(Status.DONE);
        manager.addSubtasks(subtask2);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        Epic loadedEpic = loadedManager.getEpicById(epic.getId());
        List<Subtask> loadedSubtasks = loadedManager.getSubtasksIdList();

        assertNotNull(loadedEpic);
        assertEquals(2, loadedEpic.getSubtasksId().size());
        assertEquals(2, loadedSubtasks.size());
        assertEquals(Status.DONE, loadedSubtasks.get(1).getStatus());
    }

    @Test
    void shouldSaveAndLoadTaskWithDifferentStatuses() {
        Task newTask = new Task("New", "Description", Status.NEW, 1);
        Task inProgressTask = new Task("In Progress", "Description", Status.IN_PROGRESS, 2);
        Task doneTask = new Task("Done", "Description", Status.DONE, 3);

        manager.addTask(newTask);
        manager.addTask(inProgressTask);
        manager.addTask(doneTask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(Status.NEW, loadedManager.getTaskById(newTask.getId()).getStatus());
        assertEquals(Status.IN_PROGRESS, loadedManager.getTaskById(inProgressTask.getId()).getStatus());
        assertEquals(Status.DONE, loadedManager.getTaskById(doneTask.getId()).getStatus());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void shouldHandleEmptyFile() throws IOException {
        File emptyFile = File.createTempFile("empty", ".csv");
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(emptyFile);

        assertTrue(loadedManager.getTasksIdList().isEmpty());
        assertTrue(loadedManager.getEpicsIdList().isEmpty());
        assertTrue(loadedManager.getSubtasksIdList().isEmpty());

        emptyFile.delete();
    }

    @Test
    void shouldAutoSaveOnAllOperations() {
        Task task = new Task("Task", "Description");
        manager.addTask(task);

        Epic epic = new Epic("Epic", "Description");
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Description");
        subtask.setEpicId(epic.getId());
        manager.addSubtasks(subtask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loadedManager.getTasksIdList().size());
        assertEquals(1, loadedManager.getEpicsIdList().size());
        assertEquals(1, loadedManager.getSubtasksIdList().size());
    }
}