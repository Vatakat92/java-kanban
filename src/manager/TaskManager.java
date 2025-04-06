package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    void addTask(Task task);
    void addEpic(Epic epic);
    void addSubtasks(Subtask subtask);

    void removeTaskById(Integer id);
    void removeEpicById(Integer id);
    void removeSubtaskById(Integer id);

    ArrayList<Task> getTasksIdList();
    ArrayList<Epic> getEpicsIdList();
    ArrayList<Subtask> getSubtasksIdList();

    Task getTaskById(Integer id);
    Epic getEpicById(Integer id);
    Subtask getSubtaskById(Integer id);
    List<Integer> getEpicSubtasksList(Integer id);

    void deleteTasks();
    void deleteEpics();
    void deleteSubtasks();

    void updateTask(Task task);
    void updateEpic(Epic epic);
    void updateSubtask(Subtask subtask);

    List<Task> getHistory();
}
