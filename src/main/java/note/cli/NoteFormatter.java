package note.cli;

import common.utils.DateUtils;
import note.dto.NoteDto;

import java.util.List;

public class NoteFormatter {

    private NoteFormatter() {}

    public static String format(NoteDto note) {
        return "Content: " + note.content()
                + " | Created: " + DateUtils.formatDateTime(note.createdAt());
    }

    public static boolean printList(List<NoteDto> notes, String emptyMessage) {
        if (notes.isEmpty()) {
            System.out.println(emptyMessage);
            return false;
        }
        for (int i = 0; i < notes.size(); i++) {
            System.out.println((i + 1) + ". " + format(notes.get(i)));
        }
        return true;
    }
}
