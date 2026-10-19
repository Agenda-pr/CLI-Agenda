package note.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record NoteDto(
        UUID id,
        UUID taskID,
        String content,
        LocalDateTime createdAt
) {}
