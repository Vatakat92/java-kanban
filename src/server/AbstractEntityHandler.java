package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public abstract class AbstractEntityHandler<T> extends BaseHttpHandler implements HttpHandler {
    protected final Gson gson = new GsonBuilder()
            .registerTypeAdapter(java.time.Duration.class, new DurationAdapter())
            .registerTypeAdapter(java.time.LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    protected abstract T getById(int id);
    protected abstract List<T> getAll();
    protected abstract void add(T entity);
    protected abstract void update(T entity);
    protected abstract void removeById(int id);
    protected abstract void removeAll();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        try {
            switch (method) {
                case "GET":
                    handleGet(exchange);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange);
                    break;
                default:
                    sendNotFound(exchange, "Метод не поддерживается");
            }
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof exception.NotFoundException) {
                String msg = cause.getMessage() != null ? cause.getMessage() : "Объект не найден";
                sendNotFound(exchange, msg);
            } else {
                sendServerError(exchange, "Ошибка сервера: " + e.getMessage());
            }
        } catch (Exception e) {
            sendServerError(exchange, "Ошибка сервера: " + e.getMessage());
        }
    }

    private Integer extractIdFromPath(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        for (String part : pathParts) {
            try {
                return Integer.parseInt(part);
            } catch (NumberFormatException ignored) {}
        }
        return null;
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        Integer id = extractIdFromPath(exchange);
        if (id != null) {
            try {
                T entity = getById(id);
                if (entity == null) {
                    sendNotFound(exchange, "Объект с id = " + id + " не найден");
                    return;
                }
                String response = gson.toJson(entity);
                sendText(exchange, response);
            } catch (NumberFormatException e) {
                sendNotFound(exchange, "Некорректный id ");
            }
        } else {
            List<T> entities = getAll();
            String response = gson.toJson(entities);
            sendText(exchange, response);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        try {
            char[] buf = new char[4096];
            int len = reader.read(buf);
            String body = (len > 0) ? new String(buf, 0, len) : "";
            T entity = gson.fromJson(body, getEntityClass());
            Integer id = getId(entity);
            if (id == null || getById(id) == null) {
                add(entity);
            } else {
                update(entity);
            }
            sendCreated(exchange);
        } catch (Exception e) {
            Throwable cause = e.getCause();
            if (cause instanceof exception.NotFoundException) {
                String msg = cause.getMessage() != null ? cause.getMessage() : "Объект не найден";
                sendNotFound(exchange, msg);
            } else if (e.getMessage() != null && e.getMessage().contains("Ошибка: временной интервал пересекается с существующей задачей")) {
                sendHasInteractions(exchange);
            } else {
                sendServerError(exchange, "Ошибка при сохранении объекта: " + e.getMessage());
            }
        }
    }


    private void handleDelete(HttpExchange exchange) throws IOException {
        Integer id = extractIdFromPath(exchange);
        if (id != null) {
            try {
                T entity = getById(id);
                if (entity == null) {
                    sendNotFound(exchange, "Объект с id = " + id + " не найден");
                    return;
                }
                removeById(id);
                sendCreated(exchange);
            } catch (NumberFormatException e) {
                sendNotFound(exchange, "Некорректный id ");
            }
        } else {
            removeAll();
            sendCreated(exchange);
        }
    }

    protected abstract Class<T> getEntityClass();
    protected abstract Integer getId(T entity);
}
