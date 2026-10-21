package event.model;

import common.exception.InvalidEventException;

import java.util.UUID;

public record EventTaskId(UUID id) {

    public EventTaskId {
        if (id == null) {
            throw new InvalidEventException("Task ID cannot be null");
        }
    }

    public static EventTaskId of(UUID id) {
        return new EventTaskId(id);
    }
}
