package task;

public class Subtask extends Task {
    private Epic epicTask;

    public Subtask(String name, String description) {
        super(name, description);
       this.epicTask = null;
    }

    public Epic getEpicTask() {
        return epicTask;
    }

    public void setEpicTask(Epic epicTask) {
        this.epicTask = epicTask;
    }
}