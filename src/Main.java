import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        // Базовое время
        LocalDateTime base = LocalDateTime.of(2024, 4, 1, 10, 0);

        // Tasks с непересекающимися диапазонами
        Task taskA = new Task("Задача A", "Описание задачи A");
        taskA.setDuration(Duration.ofHours(2));
        taskA.setStartTime(base);  // 10:00-12:00

        Task taskB = new Task("Задача B", "Описание задачи B");
        taskB.setDuration(Duration.ofHours(2));
        taskB.setStartTime(base.plusHours(3)); // 13:00-15:00

        Epic epicA = new Epic("Эпик A", "Описание эпика A");
        Epic epicB = new Epic("Эпик B", "Описание эпика B");

        manager.addTask(taskA);
        manager.addTask(taskB);
        manager.addEpic(epicA);
        manager.addEpic(epicB);

        // Subtasks — одной цепочки не пересекаются между собой и с задачами!
        Subtask subA = new Subtask("Подзадача A эпика A", "Описание подзадачи A");
        subA.setEpicId(epicA.getId());
        subA.setDuration(Duration.ofHours(1));
        subA.setStartTime(base.plusHours(6)); // 16:00-17:00

        Subtask subB = new Subtask("Подзадача B эпика A", "Описание подзадачи B");
        subB.setEpicId(epicA.getId());
        subB.setDuration(Duration.ofHours(1));
        subB.setStartTime(base.plusHours(7)); // 17:00-18:00

        Subtask subC = new Subtask("Подзадача A эпика B", "Описание подзадачи A");
        subC.setEpicId(epicB.getId());
        subC.setDuration(Duration.ofHours(2));
        subC.setStartTime(base.plusHours(9)); // 19:00-21:00

        manager.addSubtasks(subA);
        manager.addSubtasks(subB);
        manager.addSubtasks(subC);

        System.out.println("Шаг 1: Инициализация");
        for (Task task : manager.getTasksIdList()) System.out.println(task);
        for (Epic epic : manager.getEpicsIdList()) System.out.println(epic);
        for (Subtask sub : manager.getSubtasksIdList()) System.out.println(sub);

        manager.getTaskById(taskA.getId()).setStatus(Status.IN_PROGRESS);
        manager.getSubtaskById(subA.getId()).setStatus(Status.IN_PROGRESS);
        manager.updateEpic(manager.getEpicById(epicA.getId()));
        manager.getSubtaskById(subC.getId()).setStatus(Status.DONE);
        manager.updateEpic(manager.getEpicById(epicB.getId()));

        System.out.println("\nШаг 2: Изменение статусов");
        for (Task task : manager.getTasksIdList()) System.out.println(task);
        for (Epic epic : manager.getEpicsIdList()) System.out.println(epic);
        for (Subtask sub : manager.getSubtasksIdList()) System.out.println(sub);

        manager.removeTaskById(taskB.getId());
        manager.removeSubtaskById(subA.getId());
        manager.removeEpicById(epicB.getId());

        System.out.println("\nШаг 3: Удаление задач");
        for (Task task : manager.getTasksIdList()) System.out.println(task);
        for (Epic epic : manager.getEpicsIdList()) System.out.println(epic);
        for (Subtask sub : manager.getSubtasksIdList()) System.out.println(sub);
    }
}
