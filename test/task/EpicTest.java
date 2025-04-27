package task;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EpicTest {
    private Epic epicA;
    private Epic epicB;
    private Subtask subA;
    private Subtask subB;

    @BeforeEach
    void beforeEachTest() {
        epicA = new Epic("Эпик A", "Описание эпика A");
        epicB = new Epic("Эпик B", "Описание эпика B");
        subA = new Subtask("Подзадача A", "Описание подзадачи A");
        subB = new Subtask("Подзадача B", "Описание подзадачи B");
        epicA.setId(1);
        epicB.setId(2);
        subA.setId(3);
        subB.setId(4);
        epicA.addSubtask(subA.getId());
        epicA.addSubtask(subB.getId());
        epicB.addSubtask(subA.getId());
        epicB.addSubtask(subB.getId());
        subA.setStatus(Status.NEW);
        subB.setStatus(Status.NEW);
    }

    @Test
    void shouldThrowExceptionWhenSettingEpicAsItsOwnSubtask() {
        assertThrows(IllegalArgumentException.class, () -> epicA.addSubtask(epicA.getId()));
    }

    @Test
    void shouldMaintainNonNullSubtaskListWhenAddingSubtasksToEpic() {
        assertEquals(2, epicA.getSubtasksId().size());
    }

    @Test
    void shouldStoreSubtaskWithMatchingIdInEpicsSubtaskList() {
        ArrayList<Integer> SubtasksList = new ArrayList<>(epicA.getSubtasksId());
        assertEquals(subA.getId(), SubtasksList.get(0));
        assertEquals(subB.getId(), SubtasksList.get(1));
    }

    @Test
    void shouldConsiderEpicsEqualWhenTheyHaveSameId() {
        epicA.setId(1);
        epicB.setId(1);
        assertEquals(epicA, epicB);
    }
}