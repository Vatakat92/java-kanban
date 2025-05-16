package manager;

import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    protected Integer globalId;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, Subtask> subtasks;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        globalId = 1;
    }

    @Override
    public void addTask(Task task) {
        task.setId(createNewId());
        tasks.put(task.getId(), task);
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(createNewId());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void addSubtasks(Subtask subtask) {
        subtask.setId(createNewId());
        subtasks.put(subtask.getId(), subtask);
    }

    @Override
    public void removeTaskById(Integer id) {
        tasks.remove(id);
    }

    @Override
    public void removeEpicById(Integer id) {
        List<Integer> subTaskListId = new ArrayList<>(epics.get(id).getSubtasksId());
        for (Integer subTaskId : subTaskListId) {
            subtasks.remove(subTaskId);
        }
        epics.remove(id);
    }

    @Override
    public void removeSubtaskById(Integer id) {
        Integer epicId = subtasks.get(id).getEpicId();
        epics.get(epicId).deleteSubTask(id);
        updateStatus(epicId);
        subtasks.remove(id);
    }

    @Override
    public List<Task> getTasksIdList() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpicsIdList() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasksIdList() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Task getTaskById(Integer id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(Integer id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(Integer id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public List<Integer> getEpicSubtasksList(Integer id) {
        return epics.get(id).getSubtasksId();
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            Integer epicId = subtask.getEpicId();
            epics.get(epicId).deleteSubTask(subtask.getId());
            updateStatus(epicId);
        }
        subtasks.clear();

    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), new Task(task));
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), new Epic(epic));
        updateStatus(epic.getId());
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), new Subtask(subtask));
        updateStatus(subtask.getEpicId());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    protected void updateStatus(Integer epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return;

        if (epic.getSubtasksId().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allDone = true;
        boolean anyInProgress = false;

        for (Integer subId : epic.getSubtasksId()) {
            Subtask subtask = subtasks.get(subId);
            if (subtask == null) continue;

            if (subtask.getStatus() == Status.IN_PROGRESS) {
                anyInProgress = true;
            }
            if (subtask.getStatus() != Status.DONE) {
                allDone = false;
            }
        }

        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (anyInProgress) {
            epic.setStatus(Status.IN_PROGRESS);
        } else {
            epic.setStatus(Status.NEW);
        }
    }

    private Integer createNewId() {
        return globalId++;
    }

}
