package task;

import manager.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final List<Integer> subtasks;
    private transient TaskManager taskManager; // Добавляем ссылку на менеджер

    public Epic(String name, String description) {
        super(name, description);
        this.type = TaskType.EPIC;
        this.status = Status.NEW;
        this.subtasks = new ArrayList<>();
    }

    public Epic(String name, String description, Status status, Integer id) {
        super(name, description, status, id);
        this.subtasks = new ArrayList<>();
        this.type = TaskType.EPIC;
    }

    public Epic(Epic epic) {
        super(epic.getName(), epic.getDescription(), epic.getStatus(), epic.getId());
        this.subtasks = new ArrayList<>(epic.getSubtasksId());
        this.type = TaskType.EPIC;
    }

    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public Duration getDuration() {
        if (subtasks.isEmpty() || taskManager == null) return null;

        return subtasks.stream()
                .map(id -> taskManager.getSubtaskById(id))
                .filter(Objects::nonNull)
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);
    }

    @Override
    public LocalDateTime getStartTime() {
        if (subtasks.isEmpty() || taskManager == null) return null;

        return subtasks.stream()
                .map(id -> taskManager.getSubtaskById(id))
                .filter(Objects::nonNull)
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    @Override
    public LocalDateTime getEndTime() {
        if (subtasks.isEmpty() || taskManager == null) return null;

        return subtasks.stream()
                .map(id -> taskManager.getSubtaskById(id))
                .filter(Objects::nonNull)
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

    public List<Integer> getSubtasksId() {
        return subtasks;
    }

    public void addSubtask(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID не должно быть null");
        }
        if (id.equals(this.getId())) {
            throw new IllegalArgumentException("Эпик не может быть своей же подзадачей");
        }
        if (!subtasks.contains(id)) {
            subtasks.add(id);
        }
    }

    public void deleteSubTask(Integer id) {
        subtasks.remove(id);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "Name:" + getName() + " \\ " +
                getDescription() +
                "|ID:" + getId() +
                "|Status:" + getStatus() +
                "|Duration:" + (getDuration() != null ? getDuration().toMinutes() + "m" : "null") +
                "|Start:" + getStartTime() +
                "|End:" + getEndTime() +
                "|SubTasks:" + subtasks +
                "}";
    }
}