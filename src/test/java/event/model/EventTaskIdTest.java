package event.model;

import common.exception.InvalidEventException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class EventTaskIdTest {

    @Test
    void createsValidEventTaskId() {
        UUID uuid = UUID.randomUUID();
        EventTaskId eventTaskId = EventTaskId.of(uuid);

        assertNotNull(eventTaskId);
        assertEquals(uuid, eventTaskId.id());
    }

    @Test
    void throwsExceptionWhenTaskIdIsNull() {
        assertThrows(InvalidEventException.class, () -> EventTaskId.of(null));
    }

    @Test
    void twoEventTaskIdsWithSameValueAreEqual() {
        UUID uuid = UUID.randomUUID();
        EventTaskId id1 = EventTaskId.of(uuid);
        EventTaskId id2 = EventTaskId.of(uuid);

        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }
}
