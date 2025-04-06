package task;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SubtaskTest {
    private static Subtask subA;
    private static Subtask subB;
    private static Epic epicA;

    @BeforeAll
    static void beforeAllTests() {
        subA = new Subtask("Подзадача A", "Описание подзадачи A");
        subB = new Subtask("Подзадача B", "Описание подзадачи B");
        epicA = new Epic("Эпик", "Описание эпика");
    }

    @Test
    void shouldThrowExceptionWhenSettingSubtaskAsItsOwnEpic() {
        assertThrows(IllegalArgumentException.class, () -> subA.setEpicId(subA.getId()));
    }

    @Test
    void shouldSetEpicAsParentAndMaintainCorrectIdReference() {
        subA.setEpicId(epicA.getId());
        assertNotNull(subA.getEpicId());
        assertEquals(subA.getEpicId(), epicA.getId());
    }

    @Test
    void shouldConsiderSubtasksEqualWhenTheyHaveSameId() {
        subA.setId(1);
        subB.setId(1);
        assertEquals(subA, subB);
    }
}
