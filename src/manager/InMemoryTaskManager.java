package manager;

import exception.ManagerSaveException;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class InMemoryTaskManager implements TaskManager {
    protected Integer globalId;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, Subtask> subtasks;
    protected final Set<Task> prioritizedTasks;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        prioritizedTasks = new TreeSet<>(Comparator.comparing(
                Task::getStartTime,
                Comparator.nullsLast(Comparator.naturalOrder()))
        );
        globalId = 1;
    }

    @Override
    public void addTask(Task task) {
        if (hasTimeOverlap(task)) {
            throw new ManagerSaveException("Задача пересекается по времени с существующей");
        }
        task.setId(createNewId());
        tasks.put(task.getId(), task);
        updatePrioritizedTasks(task);
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(createNewId());
        epics.put(epic.getId(), epic);
        updateEpic(epics.get(epic.getId()));
    }

    @Override
    public void addSubtasks(Subtask subtask) {
        if (hasTimeOverlap(subtask)) {
            throw new ManagerSaveException("Подзадача пересекается по времени с существующей");
        }
        subtask.setId(createNewId());
        subtasks.put(subtask.getId(), subtask);
        epics.get(subtask.getEpicId()).addSubtask(subtask.getId());
        updateEpicStatus(subtask.getEpicId());
        updateEpicTime(subtask.getEpicId());
        updatePrioritizedTasks(subtask);
    }

    @Override
    public void removeTaskById(Integer id) {
        removeFromPrioritizedTasks(id);
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeEpicById(Integer id) {
        List<Integer> subTaskListId = new ArrayList<>(epics.get(id).getSubtasksId());
        subTaskListId.forEach(subtasks::remove);
        epics.remove(id);
    }

    @Override
    public void removeSubtaskById(Integer id) {
        Integer epicId = subtasks.get(id).getEpicId();
        epics.get(epicId).deleteSubTask(id);
        updateEpicStatus(epicId);
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

    public boolean hasTimeOverlap(Task newTask) {
        if (newTask.getStartTime() == null) {
            return false;
        }
        for (Task existingTask : new ArrayList<>(prioritizedTasks)) {
            if (isTimeOverlapping(existingTask, newTask)) {
                return true;
            }
        }
        return false;
    }

    private void updatePrioritizedTasks(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    private void removeFromPrioritizedTasks(int id) {
        prioritizedTasks.removeIf(task -> task.getId() == id);
    }

    @Override
    public Task getTaskById(Integer id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Epic getEpicById(Integer id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Subtask getSubtaskById(Integer id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
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
            updateEpicStatus(epicId);
            updateEpicTime(epicId);
        }
        subtasks.clear();
    }

    @Override
    public void updateTask(Task task) {
        removeFromPrioritizedTasks(task.getId());
        tasks.put(task.getId(), new Task(task));
        updatePrioritizedTasks(task);
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic storedEpic = epics.get(epic.getId());
        if (storedEpic != null) {
            storedEpic.setName(epic.getName());
            storedEpic.setDescription(epic.getDescription());
            updateEpicTime(storedEpic.getId());
            updateEpicStatus(storedEpic.getId());
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), new Subtask(subtask));
        if (subtask.getEpicId() != null || subtask.getEpicId() != 0) {
            updateEpic(epics.get(subtask.getEpicId()));
        }
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private void updateEpicTime(Integer epicId) {
        if (!epics.containsKey(epicId)) {
            throw new NoSuchElementException("Epic with id: " + epicId + " not found.");
        }
        Epic epic = getEpicById(epicId);
        epic.setDuration(calculateEpicDuration(epic));
        epic.setStartTime(calculateStartTime(epic));
        epic.setEndTime(calculateEndTime(epic));
    }

    private Duration calculateEpicDuration(Epic epic) {
        if (epic.getSubtasksId().isEmpty()) {
            return null;
        }
        return epic.getSubtasksId().stream()
                .map(this::getSubtaskById)
                .filter(Objects::nonNull)
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);
    }

    public LocalDateTime calculateStartTime(Epic epic) {
        if (epic.getSubtasksId().isEmpty()) {
            return null;
        }
        return epic.getSubtasksId().stream()
                .map(this::getSubtaskById)
                .filter(Objects::nonNull)
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    public LocalDateTime calculateEndTime(Epic epic) {
        if (epic.getSubtasksId().isEmpty()) {
            return null;
        }
        return epic.getSubtasksId().stream()
                .map(this::getSubtaskById)
                .filter(Objects::nonNull)
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

    private boolean isTimeOverlapping(Task existingTask, Task newTask) {
        if (existingTask.getStartTime() == null || existingTask.getEndTime() == null
                || newTask == null || newTask.getStartTime() == null || newTask.getEndTime() == null) {
            return false;
        }
        return existingTask.getStartTime().isBefore(newTask.getEndTime())
                && newTask.getStartTime().isBefore(existingTask.getEndTime());
    }

    private void updateEpicStatus(Integer epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return;

        if (epic.getSubtasksId().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        int newCount = 0;
        int doneCount = 0;
        int inProgressCount = 0;

        for (Integer subId : epic.getSubtasksId()) {
            Subtask subtask = subtasks.get(subId);
            if (subtask == null) continue;
            switch (subtask.getStatus()) {
                case NEW: newCount++; break;
                case DONE: doneCount++; break;
                case IN_PROGRESS: inProgressCount++; break;
                default: break;
            }
        }

        int total = newCount + doneCount + inProgressCount;

        if (doneCount == total) {
            epic.setStatus(Status.DONE);
        } else if (newCount == total) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    private Integer createNewId() {
        return globalId++;
    }
}