package manager;

import exception.ManagerSaveException;
import task.*;

import java.io.*;
import java.nio.file.Files;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;


    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    protected void save() {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic\n");

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
            throw new ManagerSaveException("Ошибка сохранения в файл", e);
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
                task instanceof Subtask ? String.valueOf(((Subtask) task).getEpicId()) : ""
        };
        return String.join(",", data);
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        return value.contains(",") ? "\"" + value + "\"" : value;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try {
            String content = Files.readString(file.toPath());
            String[] lines = content.split("\n");

            for (String line : lines) {
                if (!line.startsWith("id,") && !line.isEmpty()) {
                    Task task = fromString(line);
                    if (task instanceof Epic) {
                        manager.addEpic((Epic) task);
                    } else if (task != null && !(task instanceof Subtask)) {
                        manager.addTask(task);
                    }
                }
            }

            for (String line : lines) {
                if (!line.startsWith("id,") && !line.isEmpty()) {
                    Task task = fromString(line);
                    if (task instanceof Subtask subtask) {
                        if (manager.getEpicById(subtask.getEpicId()) != null) {
                            manager.addSubtasks(subtask);
                            Epic epic = manager.getEpicById(subtask.getEpicId());
                            epic.addSubtask(subtask.getId());
                            manager.updateStatus(epic.getId());
                        }
                    }
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки", e);
        }
        return manager;
    }

    private static Task fromString(String value) {
        String[] fields = value.split(",", -1);
        if (fields.length < 6) return null;

        try {
            int id = Integer.parseInt(fields[0].trim());
            TaskType type = TaskType.valueOf(fields[1].trim());
            String name = fields[2].trim();
            if (name.isEmpty()) return null;
            Status status = Status.valueOf(fields[3].trim());
            String description = fields[4].trim();
            String epicId = fields[5].trim();

            switch (type) {
                case TASK:
                    Task task = new Task(name, description);
                    task.setId(id);
                    task.setStatus(status);
                    return task;
                case EPIC:
                    Epic epic = new Epic(name, description);
                    epic.setId(id);
                    epic.setStatus(status);
                    return epic;
                case SUBTASK:
                    Subtask subtask = new Subtask(name, description);
                    subtask.setId(id);
                    subtask.setStatus(status);
                    if (!epicId.isEmpty()) subtask.setEpicId(Integer.parseInt(epicId));
                    return subtask;
                default:
                    return null;
            }
        } catch (IllegalArgumentException e) {
            return null;
        }
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