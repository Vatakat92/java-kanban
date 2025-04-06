package task;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {
    private static Task TaskA;
    private static Task TaskB;

    @BeforeAll
    static void beforeAllTest() {
        TaskA = new Task("Задача A", "Описание задачи A");
        TaskB = new Task("Задача B", "Описание задачи B");
    }

    @BeforeEach
    void beforeEachTest() {
        TaskA.setId(1);
        TaskB.setId(2);
    }

    @Test
    void shouldConsiderTasksEqualWhenTheyHaveSameId() {
        TaskA.setId(1);
        TaskB.setId(1);
        assertEquals(TaskA, TaskB);
    }

    @Test
    void shouldUpdateNameWhenSettingNewName() {
        TaskA.setName("Название эпической задачи A изменено");
        assertEquals("Название эпической задачи A изменено", TaskA.getName());
    }

    @Test
    void shouldUpdateDescriptionWhenSettingNewDescription() {
        TaskA.setDescription("Описание задачи A изменено");
        assertEquals("Описание задачи A изменено", TaskA.getDescription());
    }

    @Test
    void shouldUpdateStatusWhenSettingNewStatus() {
        TaskA.setStatus(Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, TaskA.getStatus());
    }
}
