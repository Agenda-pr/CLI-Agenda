package event.model;

import common.exception.InvalidEventException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class EventTitleTest {

    @Test
    void createsValidEventTitle() {
        EventTitle eventTitle = EventTitle.of("Cumpleaños de Ana");

        assertEquals("Cumpleaños de Ana", eventTitle.title());
    }

    @Test
    void trimsLeadingAndTrailingSpaces() {
        EventTitle eventTitle = EventTitle.of("   Cumpleaños de Ana   ");

        assertEquals("Cumpleaños de Ana", eventTitle.title());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    void throwsExceptionWhenTitleIsInvalid(String invalidTitle) {
        assertThrows(InvalidEventException.class, () -> EventTitle.of(invalidTitle));
    }

    @Test
    void twoEventTitlesWithSameValueAreEqual() {
        EventTitle title1 = EventTitle.of("Cena de Navidad");
        EventTitle title2 = EventTitle.of("Cena de Navidad");

        assertEquals(title1, title2);
        assertEquals(title1.hashCode(), title2.hashCode());
    }
}
