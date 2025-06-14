package task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private Integer epicId;

    public Subtask(String name, String description) {
        super(name, description);
        this.type = TaskType.SUBTASK;
        this.status = Status.NEW;
        this.epicId = 0;
    }

    public Subtask(String name, String description, Status status, Integer id,
                   Duration duration, LocalDateTime startTime, Integer epicId) {
        super(name, description, status, id);
        this.type = TaskType.SUBTASK;
        this.duration = duration;
        this.startTime = startTime;
        this.epicId = epicId;
    }

    public Subtask(Subtask subtask) {
        super(subtask.getName(), subtask.getDescription(), subtask.getStatus(), subtask.getId());
        this.type = TaskType.SUBTASK;
        this.duration = subtask.getDuration();
        this.startTime = subtask.getStartTime();
        this.epicId = subtask.getEpicId();
    }

    public Subtask() {
        super();
        this.type = TaskType.SUBTASK;
        this.status = Status.NEW;
        this.epicId = 0;
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer id) {

        if (id.equals(this.getId())) {
            throw new IllegalArgumentException("Неверная операция: подзадача не может быть своим же эпиком");
        }
        if (id.equals(this.getEpicId())) {
            return;
        }
        this.epicId = id;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "Name:" + this.getName() + " \\ " +
                this.getDescription() +
                "|ID:" + this.getId() +
                "|Status:" + this.getStatus() +
                "|Duration:" + (duration != null ? duration.toMinutes() + "m" : "null") +
                "|Start:" + startTime +
                "|End:" + getEndTime() +
                "|EpicTask:" + epicId +
                "}";
    }
}