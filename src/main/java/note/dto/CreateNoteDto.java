package note.dto;

import java.util.UUID;

public record CreateNoteDto(
        UUID taskID,
        String content
) {}
