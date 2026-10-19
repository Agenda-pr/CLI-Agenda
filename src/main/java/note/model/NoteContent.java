package note.model;

import common.exception.NoteContentEmpty;

public record NoteContent(String content) {

    public NoteContent {
        if (content == null || content.isBlank()) {
            throw new NoteContentEmpty();
        }
        content = content.trim();
    }
    public static NoteContent of(String content) {
        return new NoteContent(content);
    }
}