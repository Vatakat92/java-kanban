package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

import static task.Status.NEW;

public class Task {
    protected TaskType type;
    private Integer id;
    protected String name;
    protected String description;
    protected Status status;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.type = TaskType.TASK;
        this.status = NEW;
        this.id = 0;
    }

    public Task(String name, String description, Status status, Integer id) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
        this.type = TaskType.TASK;
    }

    public Task(Task task) {
        this.name = task.getName();
        this.description = task.getDescription();
        this.status = task.getStatus();
        this.id = task.getId();
        this.type = TaskType.TASK;
        this.duration = task.getDuration();
        this.startTime = task.getStartTime();
    }

    public Integer getId() {
        return id;
    }

    public TaskType getType() {
        return type;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) {
            return null;
        }
        return startTime.plus(duration);
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public boolean isOverlapping(Task other) {
        if (this.startTime == null || this.getEndTime() == null
                || other == null || other.startTime == null || other.getEndTime() == null) {
            return false;
        }
        return this.startTime.isBefore(other.getEndTime())
                && other.startTime.isBefore(this.getEndTime());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "Name:" + name + " \\ " +
                description +
                "|id:" + id +
                "|Status:" + status +
                "|Duration:" + (duration != null ? duration.toMinutes() + "m" : "null") +
                "|Start:" + startTime +
                "|End:" + getEndTime() +
                "}";
    }
}
