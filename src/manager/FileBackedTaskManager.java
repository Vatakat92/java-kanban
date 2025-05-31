package manager;

import exception.ManagerSaveException;
import task.*;

import java.io.*;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic,startTime,duration\n");
            for (Task task : tasks.values()) {
                writer.write(taskToString(task) + "\n");
            }
            for (Epic epic : epics.values()) {
                writer.write(taskToString(epic) + "\n");
            }
            for (Subtask subtask : subtasks.values()) {
                writer.write(taskToString(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл");
        }
    }

    private String taskToString(Task task) {
        if (task == null || task.getType() == null) {
            return "";
        }
        String[] data = {
                String.valueOf(task.getId()),
                task.getType().name(),
                escapeCsv(task.getName()),
                task.getStatus().name(),
                escapeCsv(task.getDescription()),
                task instanceof Subtask ? String.valueOf(((Subtask) task).getEpicId()) : "",
                task.getStartTime() != null ? task.getStartTime().toString() : "",
                task.getDuration() != null ? String.valueOf(task.getDuration().toMinutes()) : ""
        };
        return String.join(",", data);
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        return value.contains(",") ? "\"" + value + "\"" : value;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        int loadedId = 0;
        List<Task> taskList = new ArrayList<>();
        List<Epic> epicList = new ArrayList<>();
        List<Subtask> subtaskList = new ArrayList<>();
        try {
            for (String line : Files.readAllLines(file.toPath())) {
                if (line.isBlank() || line.startsWith("id,")) {
                    continue;
                }
                Optional<Task> parsed = taskManager.fromString(line);
                if (parsed.isPresent()) {
                    Task actual = parsed.get();
                    if (actual instanceof Epic epic) {
                        epicList.add(epic);
                        loadedId = Math.max(epic.getId(), loadedId);
                    } else if (actual instanceof Subtask subtask) {
                        subtaskList.add(subtask);
                        loadedId = Math.max(subtask.getId(), loadedId);
                    } else {
                        taskList.add(actual);
                        loadedId = Math.max(actual.getId(), loadedId);
                    }
                }
            }
            for (Epic epic : epicList) {
                taskManager.addEpic(epic);
            }
            for (Task task : taskList) {
                taskManager.addTask(task);
            }
            for (Subtask subtask : subtaskList) {
                taskManager.addSubtasks(subtask);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки");
        }
        taskManager.globalId = loadedId;
        return taskManager;
    }

    private Optional<Task> fromString(String value) {
        String[] fields = value.split(",", -1);
        if (fields.length < 8) return Optional.empty();
        try {
            int id = Integer.parseInt(fields[0].trim());
            TaskType type = TaskType.valueOf(fields[1].trim());
            String name = fields[2].trim();
            if (name.isEmpty()) return Optional.empty();
            Status status = Status.valueOf(fields[3].trim());
            String description = fields[4].trim();
            String epicIdStr = fields[5].trim();
            String startTimeStr = fields[6].trim();
            String durationStr = fields[7].trim();
            LocalDateTime startTime = startTimeStr.isEmpty() ? null : LocalDateTime.parse(startTimeStr);
            Duration duration = durationStr.isEmpty() ? null : Duration.ofMinutes(Long.parseLong(durationStr));
            switch (type) {
                case TASK -> {
                    Task task = new Task(name, description, status, id);
                    task.setStartTime(startTime);
                    task.setDuration(duration);
                    return Optional.of(task);
                }
                case EPIC -> {
                    return Optional.of(new Epic(name, description, status, id));
                }
                case SUBTASK -> {
                    int epicId = epicIdStr.isEmpty() ? 0 : Integer.parseInt(epicIdStr);
                    Subtask subtask = new Subtask(name, description, status, id, duration, startTime, epicId);
                    return Optional.of(subtask);
                }
                default -> {
                    return Optional.empty();
                }
            }
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return super.getPrioritizedTasks();
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtasks(Subtask subtask) {
        if (hasTimeOverlap(subtask)) {
            throw new ManagerSaveException("Задача пересекается по времени");
        }
        super.addSubtasks(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeTaskById(Integer id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeEpicById(Integer id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeSubtaskById(Integer id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }
}
