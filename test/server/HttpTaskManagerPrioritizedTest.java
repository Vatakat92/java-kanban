package server;

import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.*;
import task.Status;
import task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerPrioritizedTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);

    public HttpTaskManagerPrioritizedTest() throws IOException {}

    @BeforeEach
    public void setUp() {
        manager.deleteTasks();
        manager.deleteSubtasks();
        manager.deleteEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void shouldReturnPrioritizedTasks_WhenTasksExist() throws IOException, InterruptedException {
        Task task1 = new Task("Task1", "Description", Status.NEW, 0, LocalDateTime.now(), Duration.ofMinutes(5));
        Task task2 = new Task("Task2", "Description", Status.NEW, 1, LocalDateTime.now().plusMinutes(10), Duration.ofMinutes(10));
        manager.addTask(task1);
        manager.addTask(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Task1"));
        assertTrue(response.body().contains("Task2"));
    }
}
