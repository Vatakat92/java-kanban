package manager;

import task.Epic;
import task.Subtask;
import task.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TaskManager {
    private Map<Integer, Task> tasks;
    private Map<Integer, Epic> epics;
    private Map<Integer, Subtask> subtasks;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    public void addTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void addEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void addSubtasks(Subtask subtask, Epic epic) {
        epic.addSubtask(subtask);
        subtask.setEpicTask(epic);
        subtasks.put(subtask.getId(), subtask);
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
        subtasks.get(id).getEpicTask().deleteSubTask(subtasks.get(id));
        subtasks.remove(id);
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

    public void deleteTasks(){
        tasks.clear();
    }

    public void deleteEpics(){
        epics.clear();
        subtasks.clear();
    }

    public void deleteSubtasks(){
        for (Subtask subtask : subtasks.values()) {
            subtask.getEpicTask().deleteSubTask(subtask);
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
        subtask.getEpicTask().updateStatus();
        subtasks.put(subtask.getId(),subtask);
    }

}


