package server;

import task.Task;
import manager.TaskManager;

import java.util.List;

public class TaskHandler extends AbstractEntityHandler<Task> {
    private final TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    protected List<Task> getAll() {
        return taskManager.getTasksIdList();
    }

    @Override
    protected Task getById(int id) {
        return taskManager.getTaskById(id);
    }

    @Override
    protected void add(Task entity) {
        try {
            taskManager.addTask(entity);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при добавлении задачи: " + e.getMessage(), e);
        }
    }

    @Override
    protected void update(Task entity) {
        try {
            taskManager.updateTask(entity);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при обновлении задачи: " + e.getMessage(), e);
        }
    }

    @Override
    protected void removeById(int id) {
        try {
            taskManager.removeTaskById(id);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении задачи: " + e.getMessage(), e);
        }
    }

    @Override
    protected void removeAll() {
        taskManager.deleteTasks();
    }

    @Override
    protected Class<Task> getEntityClass() {
        return Task.class;
    }

    @Override
    protected Integer getId(Task entity) {
        return entity.getId();
    }
}
