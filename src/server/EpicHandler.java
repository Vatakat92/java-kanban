package server;

import task.Epic;
import manager.TaskManager;
import java.util.List;

public class EpicHandler extends AbstractEntityHandler<Epic> {
    private final TaskManager taskManager;

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    protected Epic getById(int id) {
        return taskManager.getEpicById(id);
    }

    @Override
    protected List<Epic> getAll() {
        return taskManager.getEpicsIdList();
    }

    @Override
    protected void add(Epic entity) {
        taskManager.addEpic(entity);
    }

    @Override
    protected void update(Epic entity) {
        taskManager.updateEpic(entity);
    }

    @Override
    protected void removeById(int id) {
        try {
            taskManager.removeEpicById(id);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении эпика: " + e.getMessage(), e);
        }
    }

    @Override
    protected void removeAll() {
        taskManager.deleteEpics();
    }

    @Override
    protected Class<Epic> getEntityClass() {
        return Epic.class;
    }

    @Override
    protected Integer getId(Epic entity) {
        return entity.getId();
    }
}
