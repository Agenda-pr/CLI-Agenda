package event.repository;

import event.dao.EventDAO;
import event.model.Event;

import java.util.List;
import java.util.UUID;

public class DAOEventRepository implements EventRepository {
    private final EventDAO dao;

    public DAOEventRepository(EventDAO dao) {
        this.dao = dao;
    }

    @Override
    public void save(Event event) {
        if (!dao.update(event)) {
            dao.insert(event);
        }
    }

    @Override
    public List<Event> showAllEvents() {
        return dao.findAll();
    }

    @Override
    public Event findEventById(UUID id) {
        return dao.findById(id).orElse(null);
    }

    @Override
    public boolean deleteEventById(UUID id) {
        return dao.deleteById(id);
    }
}
