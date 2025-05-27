package manager;

import org.junit.jupiter.api.*;
import task.Epic;
import task.Status;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File tempFile;

    @BeforeEach
    @Override
    public void setUp() throws IOException {
        tempFile = File.createTempFile("tasks", ".csv");
        super.setUp();
    }

    @AfterEach
    void tearDown() {
        if (!tempFile.delete()) {
            System.err.println("Failed to delete temp file: " + tempFile.getAbsolutePath());
        }
    }

    @Override
    protected FileBackedTaskManager createTaskManager()  {
        return new FileBackedTaskManager(tempFile);
    }

    @Test
    void shouldLoadFromEmptyFile() throws IOException {
        Files.write(tempFile.toPath(), "id,type,name,status,description,epic,startTime,duration\n".getBytes());

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);
        assertTrue(loaded.getTasksIdList().isEmpty());
        assertTrue(loaded.getEpicsIdList().isEmpty());
    }

    @Test
    void shouldSaveAndLoadWithTimeData() {
        java.time.LocalDateTime base = java.time.LocalDateTime.of(2022, 1, 1, 8, 0);

        FileBackedTaskManager taskManager = new FileBackedTaskManager(tempFile);

        task.Task task = new task.Task("Task", "Desc");
        task.setDuration(java.time.Duration.ofDays(2));
        task.setStartTime(base);

        task.Epic epic = new task.Epic("Epic", "EpicDesc");
        taskManager.addEpic(epic); // теперь epic.getId() уникален и задан

        task.Subtask subtask = new task.Subtask("Sub", "SubDesc");
        subtask.setDuration(java.time.Duration.ofDays(2));
        subtask.setStartTime(base.plusDays(3)); // чтобы не пересекалась с task
        subtask.setEpicId(epic.getId());

        taskManager.addTask(task);
        taskManager.addSubtasks(subtask);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(task.getStartTime(), loaded.getTaskById(task.getId()).getStartTime());
        assertEquals(epic.getDuration(), loaded.getEpicById(epic.getId()).getDuration());
    }

    @Test
    void shouldThrowWhenLoadingFromNonExistingFile() {
        File nonExistent = new File("definitely-not-exists-123.csv");
        assertThrows(RuntimeException.class, () -> FileBackedTaskManager.loadFromFile(nonExistent));
    }

    @Test
    void shouldSetEpicStatusNewIfNoSubtasks()  {
        FileBackedTaskManager manager = createTaskManager();
        Epic epic = new Epic("Empty", "No subtasks");
        manager.addEpic(epic);
        assertEquals(Status.NEW, manager.getEpicById(epic.getId()).getStatus());
    }
}
