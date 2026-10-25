package event.dao;

import event.model.Event;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventDAO {
    void insert(Event event);
    boolean update(Event event);
    Optional<Event> findById(UUID id);
    List<Event> findAll();
    boolean deleteById(UUID id);
}
