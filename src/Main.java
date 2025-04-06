import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        
        Task taskA = new Task("Задача A", "Описание задачи A");
        Task taskB = new Task("Задача B", "Описание задачи B");
        Epic epicA = new Epic("Эпик A", "Описание эпика A");
        Epic epicB = new Epic("Эпик B", "Описание эпика B");
        Subtask subA = new Subtask("Подзадача A эпика A", "Описание подзадачи A");
        Subtask subB = new Subtask("Подзадача B эпика A", "Описание подзадачи B");
        Subtask subC = new Subtask("Подзадача A эпика B", "Описание подзадачи A");

        manager.addTask(taskA);
        manager.addTask(taskB);
        manager.addEpic(epicA);
        manager.addEpic(epicB);
        manager.addSubtasks(subA);
        manager.addSubtasks(subB);
        manager.addSubtasks(subC);

        manager.getEpicById(epicA.getId()).addSubtask(subA.getId());
        manager.getEpicById(epicA.getId()).addSubtask(subB.getId());
        manager.getEpicById(epicB.getId()).addSubtask(subC.getId());
        manager.getSubtaskById(subA.getId()).setEpicId(epicA.getId());
        manager.getSubtaskById(subB.getId()).setEpicId(epicA.getId());
        manager.getSubtaskById(subC.getId()).setEpicId(epicB.getId());
        manager.updateEpic(manager.getEpicById(epicA.getId()));
        manager.updateEpic(manager.getEpicById(epicB.getId()));

        System.out.println("Шаг 1: Инициализация");
        for (Task task : (ArrayList<Task>) manager.getTasksIdList()) System.out.println(task);
        for (Epic epic : (ArrayList<Epic>) manager.getEpicsIdList()) System.out.println(epic);
        for (Subtask sub : (ArrayList<Subtask>) manager.getSubtasksIdList()) System.out.println(sub);

        manager.getTaskById(taskA.getId()).setStatus(Status.IN_PROGRESS);
        manager.getSubtaskById(subA.getId()).setStatus(Status.IN_PROGRESS);
        manager.updateEpic(manager.getEpicById(epicA.getId()));
        manager.getSubtaskById(subC.getId()).setStatus(Status.DONE);
        manager.updateEpic(manager.getEpicById(epicB.getId()));

        System.out.println("\nШаг 2: Изменение статусов");
        for (Task task : (ArrayList<Task>) manager.getTasksIdList()) System.out.println(task);
        for (Epic epic : (ArrayList<Epic>) manager.getEpicsIdList()) System.out.println(epic);
        for (Subtask sub : (ArrayList<Subtask>) manager.getSubtasksIdList()) System.out.println(sub);

        manager.removeTaskById(taskB.getId());
        manager.removeSubtaskById(subA.getId());
        manager.removeEpicById(epicB.getId());

        System.out.println("\nШаг 3: Удаление задач");
        for (Task task : (ArrayList<Task>) manager.getTasksIdList()) System.out.println(task);
        for (Epic epic : (ArrayList<Epic>) manager.getEpicsIdList()) System.out.println(epic);
        for (Subtask sub : (ArrayList<Subtask>) manager.getSubtasksIdList()) System.out.println(sub);
    }
}