package task;

public class Subtask extends Task {
    private Integer epicId;

    public Subtask(String name, String description) {
        super(name, description);
       this.epicId = 0;
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }
}