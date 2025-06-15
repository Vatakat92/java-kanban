package task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subtasks;
    private LocalDateTime endTime;

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
        super(epic.getName(), epic.getDescription(), epic.getStatus(), epic.getId(),
                epic.getStartTime(), epic.getDuration());
        subtasks = new ArrayList<>(epic.getSubtasksId());
        type = TaskType.EPIC;
        endTime = epic.getEndTime();
    }

    public Epic() {
        super("", "");
        this.type = TaskType.EPIC;
        this.status = Status.NEW;
        this.subtasks = new ArrayList<>();
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
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

    public void setId(Integer id) {
        super.setId(id);
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