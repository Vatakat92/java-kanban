package task;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subtasks;

    public Epic(String name, String description) {
        super(name, description);
        subtasks = new ArrayList<>();
    }

    public Epic(Epic epic) {
        super(epic.getName(), epic.getDescription(), epic.getStatus(), epic.getId());
        subtasks = epic.getSubtasksId();
    }

    public List<Integer> getSubtasksId() {
        return subtasks;
    }

    public void addSubtask(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Не должно быть равно нулю.");
        }
        if (id.equals(this.getId())) {
            throw new IllegalArgumentException("Неправильная операция: " +
                    "эпик не может быть своей же подзадачей");
        }
        if (subtasks.contains(id)) {
            return;
        }
        subtasks.add(id);
    }

    public void deleteSubTask(Integer id){
        subtasks.remove(id);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "Name:" + this.getName() + " \\ " +
                this.getDescription() +
                "|ID:" + this.getId() +
                "|Status:" + this.getStatus() +
                "|SubTask:" + subtasks +
                "}";
    }
}