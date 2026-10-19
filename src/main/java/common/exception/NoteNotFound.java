package common.exception;

public class NoteNotFound extends RuntimeException {
    public NoteNotFound() {
        super("Note not found");
    }
}
