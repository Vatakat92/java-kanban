package manager;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ManagersTest {

    @Test
    void shouldReturnInitializedManagerInstance() {
        TaskManager manager = Managers.getDefault();
        assertNotNull(manager);
        assertTrue(manager.getTasksIdList().isEmpty());
    }

    @Test
    void shouldReturnInitializedHistoryManagerInstance() {
        HistoryManager manager = Managers.getDefaultHistory();
        assertNotNull(manager);
        assertTrue(manager.getHistory().isEmpty());
    }
}
