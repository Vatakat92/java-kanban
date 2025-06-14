package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import manager.InMemoryTaskManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private final InMemoryTaskManager taskManager;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(java.time.Duration.class, new DurationAdapter())
            .registerTypeAdapter(java.time.LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = (InMemoryTaskManager) taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            try {
                List<?> prioritized = taskManager.getPrioritizedTasks();
                String response = gson.toJson(prioritized);
                sendText(exchange, response);
            } catch (Exception e) {
                sendServerError(exchange, "Ошибка при получении приоритета: " + e.getMessage());
            }
        } else {
            sendNotFound(exchange, "Метод не поддерживается");
        }
    }
}
