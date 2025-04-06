package task;

public class Subtask extends Task {
    private Integer epicId;

    public Subtask(String name, String description) {
        super(name, description);
        epicId = 0;
    }

    public Subtask(Subtask subtask) {
        super(subtask.getName(), subtask.getDescription(), subtask.getStatus(), subtask.getId());
        epicId = subtask.getEpicId();
    }
    
    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer Id) {
        if (Id.equals(this.getId())) {
            throw new IllegalArgumentException("Неверная операция: " +
                    "подзадача не может быть своим же эпиком");
        }
        if (Id.equals(this.getEpicId())) {
            return;
        }
        this.epicId = Id;
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