package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;


public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager taskManager;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(java.time.Duration.class, new DurationAdapter())
            .registerTypeAdapter(java.time.LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            try {
                manager.HistoryManager historyManager = null;
                if (taskManager instanceof manager.InMemoryTaskManager) {
                    historyManager = ((manager.InMemoryTaskManager) taskManager).getHistoryManager();
                }
                String response = gson.toJson(historyManager != null ? historyManager.getHistory() : new java.util.ArrayList<>());
                sendText(exchange, response);
            } catch (Exception e) {
                sendServerError(exchange, "Ошибка при получении истории: " + e.getMessage());
            }
        } else {
            sendNotFound(exchange, "Метод не поддерживается");
        }
    }
}
