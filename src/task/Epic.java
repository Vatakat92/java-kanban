package task;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Subtask> subtasks;

    public Epic(String name, String description) {
        super(name, description);
        subtasks = new ArrayList<>();
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    public void deleteSubTask(Subtask subtask){
        subtasks.remove(subtask);
        updateStatus();
    }

    public void updateStatus() {
        int allInProgress = 0;
        int allDone = 0;

        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() == Status.IN_PROGRESS) {
                allInProgress++;
            }
            if (subtask.getStatus() == Status.DONE) {
                allDone++;
            }

        }
        if (allDone > 0 && allDone == subtasks.size()) {
            setStatus(Status.DONE);
        } else if (allInProgress>0 || allDone>0 ) {
            setStatus(Status.IN_PROGRESS);

        }else{
            setStatus(Status.NEW);
        }

    }
}