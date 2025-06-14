package server;

import task.Subtask;
import manager.TaskManager;
import task.Epic;

import java.util.List;

public class SubtaskHandler extends AbstractEntityHandler<Subtask> {
    private final TaskManager taskManager;

    public SubtaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    protected Subtask getById(int id) {
        return taskManager.getSubtaskById(id);
    }

    @Override
    protected List<Subtask> getAll() {
        return taskManager.getSubtasksList();
    }

    @Override
    protected void add(Subtask entity) {
        try {
            if (entity.getStatus() == null) {
                entity.setStatus(task.Status.NEW);
            }
            Integer epicId = entity.getEpicId();
            if (epicId == null || taskManager.getEpicById(epicId) == null) {
                Epic targetEpic = null;
                for (Epic epic : taskManager.getEpicsIdList()) {
                    if ("Эпик для подзадачи".equals(epic.getName())) {
                        targetEpic = epic;
                        break;
                    }
                }
                if (targetEpic == null) {
                    targetEpic = new Epic("Эпик для подзадачи", "Описание Эпика");
                    taskManager.addEpic(targetEpic);
                }
                entity.setEpicId(targetEpic.getId());
            }
            taskManager.addSubtasks(entity);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при добавлении подзадачи: " + e.getMessage(), e);
        }
    }

    @Override
    protected void update(Subtask entity) {
        try {
            taskManager.updateSubtask(entity);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при обновлении подзадачи: " + e.getMessage(), e);
        }
    }

    @Override
    protected void removeById(int id) {
        try {
            taskManager.removeSubtaskById(id);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении подзадачи: " + e.getMessage(), e);
        }
    }

    @Override
    protected void removeAll() {
        taskManager.deleteSubtasks();
    }

    @Override
    protected Class<Subtask> getEntityClass() {
        return Subtask.class;
    }

    @Override
    protected Integer getId(Subtask entity) {
        return entity.getId();
    }
}
