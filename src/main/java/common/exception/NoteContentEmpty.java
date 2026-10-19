package common.exception;

public class NoteContentEmpty extends RuntimeException {
    public NoteContentEmpty() {
        super("Note body can't be empty");
    }
}
