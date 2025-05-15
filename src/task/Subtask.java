package task;

public class Subtask extends Task {
    private Integer epicId;

    public Subtask(String name, String description) {
        super(name, description);
        this.type = TaskType.SUBTASK;
        this.status = Status.NEW;
        epicId = 0;
    }

    public Subtask(Subtask subtask) {
        super(subtask.getName(), subtask.getDescription(), subtask.getStatus(), subtask.getId());
        epicId = subtask.getEpicId();
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer id) {
        if (id.equals(this.getId())) {
            throw new IllegalArgumentException("Неверная операция: " +
                    "подзадача не может быть своим же эпиком");
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
                "|EpicTask:" + epicId +
                "}";
    }
}