package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.*;
import task.Epic;
import task.Status;
import task.Subtask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerSubtasksTest {
    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(java.time.Duration.class, new DurationAdapter())
            .registerTypeAdapter(java.time.LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public HttpTaskManagerSubtasksTest() throws IOException {}

    @BeforeEach
    public void setUp() {
        manager.deleteTasks();
        manager.deleteSubtasks();
        manager.deleteEpics();
        taskServer.start();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void shouldAddSubtask_WhenValidSubtaskIsPosted() throws IOException, InterruptedException {
        // Сначала создаём эпик через HTTP-запрос
        Epic epic = new Epic("Epic1", "Description");
        String epicJson = gson.toJson(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI epicUrl = URI.create("http://localhost:8080/epics");
        HttpRequest epicRequest = HttpRequest.newBuilder().uri(epicUrl).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> epicResponse = client.send(epicRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, epicResponse.statusCode());
        System.out.println("DEBUG: POST /epics response body: " + epicResponse.body());
        // Получаем id созданного эпика через HTTP GET
        HttpRequest getEpicsRequest = HttpRequest.newBuilder().uri(epicUrl).GET().build();
        HttpResponse<String> getEpicsResponse = client.send(getEpicsRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getEpicsResponse.statusCode());
        System.out.println("DEBUG: GET /epics response body: " + getEpicsResponse.body());
        Epic[] epics = gson.fromJson(getEpicsResponse.body(), Epic[].class);
        assertNotNull(epics);
        assertTrue(epics.length > 0);
        int epicId = epics[0].getId();
        // Теперь создаём подзадачу с этим epicId
        Subtask subtask = new Subtask("Subtask1", "desc", Status.NEW, null, Duration.ofMinutes(10), LocalDateTime.now(), epicId);
        String subtaskJson = gson.toJson(subtask);
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        System.out.println("DEBUG: POST /subtasks response body: " + response.body());
        // Проверяем, что подзадача добавлена через HTTP GET
        HttpRequest getSubtasksRequest = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> getSubtasksResponse = client.send(getSubtasksRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getSubtasksResponse.statusCode());
        System.out.println("DEBUG: GET /subtasks response body: " + getSubtasksResponse.body());
        Subtask[] subtasks = gson.fromJson(getSubtasksResponse.body(), Subtask[].class);
        assertNotNull(subtasks);
        assertEquals(1, subtasks.length);
        assertEquals("Subtask1", subtasks[0].getName());
    }

    @Test
    public void shouldReturnSubtasks_WhenGetRequest() throws IOException, InterruptedException {
        // Сначала создаём эпик через HTTP-запрос
        Epic epic = new Epic("Epic2", "desc");
        String epicJson = gson.toJson(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI epicUrl = URI.create("http://localhost:8080/epics");
        HttpRequest epicRequest = HttpRequest.newBuilder().uri(epicUrl).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> epicResponse = client.send(epicRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, epicResponse.statusCode());
        System.out.println("DEBUG: POST /epics response body: " + epicResponse.body());
        // Получаем id созданного эпика через HTTP GET
        HttpRequest getEpicsRequest = HttpRequest.newBuilder().uri(epicUrl).GET().build();
        HttpResponse<String> getEpicsResponse = client.send(getEpicsRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getEpicsResponse.statusCode());
        System.out.println("DEBUG: GET /epics response body: " + getEpicsResponse.body());
        Epic[] epics = gson.fromJson(getEpicsResponse.body(), Epic[].class);
        assertNotNull(epics);
        assertTrue(epics.length > 0);
        int epicId = epics[0].getId();
        // Теперь создаём подзадачу с этим epicId через HTTP
        Subtask subtask = new Subtask("Subtask2", "Description", Status.NEW, null, Duration.ofMinutes(10), LocalDateTime.now(), epicId);
        String subtaskJson = gson.toJson(subtask);
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        System.out.println("DEBUG: POST /subtasks response body: " + response.body());
        // Теперь получаем все подзадачи через GET
        HttpRequest getRequest = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode());
        System.out.println("DEBUG: GET /subtasks response body: " + getResponse.body());
        assertTrue(getResponse.body().contains("Subtask2"));
    }

    @Test
    public void shouldReturn404_WhenSubtaskNotFound() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/999");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }
}
