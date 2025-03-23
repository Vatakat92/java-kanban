import manager.TaskManager;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        taskManager.addTask(new Task("Задача 1", "Описание задачи 1"));
        taskManager.addTask(new Task("Задача 2", "Описание задачи 2"));
        Epic epicA = new Epic("Эпик 1","Описание эпика 1");
        Subtask subtaskA = new Subtask("Подзадача 1","Описание подзадачи 1");
        Subtask subtaskB = new Subtask("Подзадача 2","Описание подзадачи 2");
        taskManager.addEpic(epicA);
        subtaskA.setEpicId(epicA.getId());
        subtaskB.setEpicId(epicA.getId());
        taskManager.addSubtasks(subtaskA);
        taskManager.addSubtasks(subtaskB);
        Epic epicB = new Epic("Эпик 2","Описание эпика 2");
        Subtask subtaskC = new Subtask("Подзадача 3","Описание подзадачи 3");
        taskManager.addEpic(epicB);
        subtaskC.setEpicId(epicB.getId());
        taskManager.addSubtasks(subtaskC);
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());

        taskManager.getTaskById(2).setStatus(Status.IN_PROGRESS);
        taskManager.getSubtaskById(4).setStatus(Status.IN_PROGRESS);
        taskManager.getEpicById(taskManager.getSubtaskById(4).getEpicId()).updateStatus();
        taskManager.getSubtaskById(7).setStatus(Status.DONE);
        taskManager.getEpicById(taskManager.getSubtaskById(7).getEpicId()).updateStatus();

        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());

        taskManager.removeTaskById(2);
        taskManager.removeSubtaskById(4);
        taskManager.removeEpicById(6);

        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());
    }

}