package task.model;

import common.exception.InvalidTaskException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTitleTest {

    @Test
    void createsValidTaskTitle() {
        TaskTitle taskTitle = new TaskTitle("Comprar Gofio");

        assertEquals("Comprar Gofio", taskTitle.title());
    }

    @Test
    void trimsLeadingAndTrailingSpaces() {
        TaskTitle taskTitle = new TaskTitle("   Comprar Gofio   ");

        assertEquals("Comprar Gofio", taskTitle.title());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    void throwsExceptionWhenTitleIsInvalid(String invalidTitle) {
        assertThrows(InvalidTaskException.class, () -> new TaskTitle(invalidTitle));
    }

    @Test
    void twoTaskTitlesWithSameValueAreEqual() {
        TaskTitle title1 = new TaskTitle("Ir a la playita");
        TaskTitle title2 = new TaskTitle("Ir a la playita");

        assertEquals(title1, title2);
        assertEquals(title1.hashCode(), title2.hashCode());
    }
}
