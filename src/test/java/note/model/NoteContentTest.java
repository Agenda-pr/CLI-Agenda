package note.model;

import common.exception.NoteContentEmpty;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class NoteContentTest {

    @Test
    void createsValidNoteContent() {
        NoteContent content = NoteContent.of("Recordar comprar el pan");

        assertEquals("Recordar comprar el pan", content.content());
    }

    @Test
    void trimsLeadingAndTrailingSpaces() {
        NoteContent content = NoteContent.of("   Recordar comprar el pan   ");

        assertEquals("Recordar comprar el pan", content.content());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    void throwsExceptionWhenContentIsInvalid(String invalidContent) {
        assertThrows(NoteContentEmpty.class, () -> NoteContent.of(invalidContent));
    }

    @Test
    void twoNoteContentsWithSameValueAreEqual() {
        NoteContent content1 = NoteContent.of("Revisar PR");
        NoteContent content2 = NoteContent.of("Revisar PR");

        assertEquals(content1, content2);
        assertEquals(content1.hashCode(), content2.hashCode());
    }
}
