package note.cli;

import common.exception.NoteContentEmpty;
import common.utils.ConsoleReader;
import note.dto.NoteDto;
import note.model.NoteContent;

import java.util.List;

public class NoteInputReader {

    private NoteInputReader() {}

    public static String readContent(String current) {
        String prompt = (current == null)
                ? "Content: "
                : "Content [current: " + current + "] (or ENTER to keep): ";

        while (true) {
            String input = ConsoleReader.readString(prompt);
            if (current != null && input.isEmpty()) {
                return current;
            }
            try {
                return NoteContent.of(input).content();
            } catch (NoteContentEmpty error) {
                System.out.println("Content cannot be empty. Try again.");
            }
        }
    }

    public static NoteDto selectNoteFromList(List<NoteDto> notes) {
        while (true) {
            String input = ConsoleReader.readString(
                    "\nSelect note number (1-" + notes.size() + ", or ENTER/0 to cancel): ");
            if (input.isBlank() || input.equals("0")) {
                return null;
            }
            try {
                int index = Integer.parseInt(input);
                if (index >= 1 && index <= notes.size()) {
                    return notes.get(index - 1);
                }
                System.out.println("Invalid number. Choose between 1 and " + notes.size() + ".");
            } catch (NumberFormatException error) {
                System.out.println("Enter a valid number.");
            }
        }
    }
}
