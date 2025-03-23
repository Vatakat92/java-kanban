package manager;

import task.Epic;
import task.Subtask;
import task.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private Map<Integer, Task> tasks;
    private Map<Integer, Epic> epics;
    private Map<Integer, Subtask> subtasks;
    private Integer globalId;
    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        globalId = 1;
    }

    public void addTask(Task task) {
        task.setId(globalId);
        tasks.put(globalId, task);
        globalId++;
    }

    public void addEpic(Epic epic) {
        epic.setId(globalId);
        epics.put(globalId, epic);
        globalId++;
    }

    public void addSubtasks(Subtask subtask) {
        subtask.setId(globalId);
        subtasks.put(globalId, subtask);
        globalId++;
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtask(subtask);
        epics.get(subtask.getEpicId()).updateStatus();

    }

    public void removeTaskById(Integer id) {
        tasks.remove(id);
    }

    public void removeEpicById(Integer id) {
        for (Subtask subtask : epics.get(id).getSubtasks()) {
            subtasks.remove(subtask.getId());
        }
        epics.remove(id);
    }

    public void removeSubtaskById(Integer id) {
        Epic epic = epics.get(subtasks.get(id).getEpicId());
        epic.deleteSubTask(subtasks.get(id));
        subtasks.remove(id);
        epic.updateStatus();
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public Task getTaskById(Integer id){
        return tasks.get(id);
    }

    public Epic getEpicById(Integer id){
        return epics.get(id);
    }

    public Subtask getSubtaskById(Integer id){
        return subtasks.get(id);
    }

    public List<Subtask> getEpicSubtasks(Integer id){
        return  epics.get(id).getSubtasks();

    }

    public void deleteTasks(){
        tasks.clear();
    }

    public void deleteEpics(){
        epics.clear();
        subtasks.clear();
    }

    public void deleteSubtasks(){
        Epic epic = null;
        for (Subtask subtask : subtasks.values()) {
            epic = epics.get(subtask.getEpicId());
            epic.deleteSubTask(subtask);
            epic.updateStatus();
        }
        subtasks.clear();

    }

    public void updateTask(Task task){
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic){
        epic.updateStatus();
        epics.put(epic.getId(), epic);
    }

    public void updateSubtask(Subtask subtask){
        Epic epic = epics.get(subtask.getEpicId());
        epic.updateStatus();
        subtasks.put(subtask.getId(),subtask);
    }

}


