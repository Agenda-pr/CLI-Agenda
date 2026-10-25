package event.dao;

import common.db.ConnectionFactory;
import common.exception.DatabaseException;
import event.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EventDAOAdapter implements EventDAO {
    private static final String INSERT =
            "INSERT INTO events (id, title, date, recurrence, created_at) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE =
            "UPDATE events SET title = ?, date = ?, recurrence = ? WHERE id = ?";
    private static final String SELECT_BY_ID =
            "SELECT id, title, date, recurrence, created_at FROM events WHERE id = ?";
    private static final String SELECT_ALL =
            "SELECT id, title, date, recurrence, created_at FROM events ORDER BY date";
    private static final String DELETE =
            "DELETE FROM events WHERE id = ?";

    private static final String SELECT_TASKS_BY_EVENT =
            "SELECT id FROM tasks WHERE event_id = ?";
    private static final String UNLINK_TASKS_BY_EVENT =
            "UPDATE tasks SET event_id = NULL WHERE event_id = ?";
    private static final String LINK_TASK_TO_EVENT =
            "UPDATE tasks SET event_id = ? WHERE id = ?";

    @Override
    public void insert(Event event) {
        Connection connection = ConnectionFactory.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(INSERT)) {
            statement.setString(1, event.getId().toString());
            statement.setString(2, event.getTitle().title());
            statement.setDate(3, Date.valueOf(event.getCelebDate().date()));
            statement.setString(4, event.getEventRecurrenceType().name());
            statement.setTimestamp(5, Timestamp.valueOf(event.getCreatedAt().atStartOfDay()));
            statement.executeUpdate();

            syncTasks(connection, event);
        } catch (SQLException error) {
            throw new DatabaseException("Could not insert event", error);
        }
    }

    @Override
    public boolean update(Event event) {
        Connection connection = ConnectionFactory.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(UPDATE)) {
            statement.setString(1, event.getTitle().title());
            statement.setDate(2, Date.valueOf(event.getCelebDate().date()));
            statement.setString(3, event.getEventRecurrenceType().name());
            statement.setString(4, event.getId().toString());

            boolean updated = statement.executeUpdate() > 0;
            if (updated) {
                syncTasks(connection, event);
            }
            return updated;
        } catch (SQLException error) {
            throw new DatabaseException("Could not update event", error);
        }
    }

    @Override
    public Optional<Event> findById(UUID id) {
        Connection connection = ConnectionFactory.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID)) {
            statement.setString(1, id.toString());
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    List<EventTaskId> taskIds = findTaskIds(connection, id);
                    return Optional.of(mapRow(result, taskIds));
                }
                return Optional.empty();
            }
        } catch (SQLException error) {
            throw new DatabaseException("Could not find event", error);
        }
    }

    @Override
    public List<Event> findAll() {
        List<Event> events = new ArrayList<>();
        Connection connection = ConnectionFactory.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_ALL);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                UUID id = UUID.fromString(result.getString("id"));
                List<EventTaskId> taskIds = findTaskIds(connection, id);
                events.add(mapRow(result, taskIds));
            }
            return events;
        } catch (SQLException error) {
            throw new DatabaseException("Could not list events", error);
        }
    }

    @Override
    public boolean deleteById(UUID id) {
        Connection connection = ConnectionFactory.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(DELETE)) {
            statement.setString(1, id.toString());
            return statement.executeUpdate() > 0;
        } catch (SQLException error) {
            throw new DatabaseException("Could not delete event", error);
        }
    }

    private List<EventTaskId> findTaskIds(Connection connection, UUID eventId) throws SQLException {
        List<EventTaskId> taskIds = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_TASKS_BY_EVENT)) {
            statement.setString(1, eventId.toString());
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    taskIds.add(EventTaskId.of(UUID.fromString(result.getString("id"))));
                }
            }
        }
        return taskIds;
    }

    private void syncTasks(Connection connection, Event event) throws SQLException {
        try (PreparedStatement unlinkStmt = connection.prepareStatement(UNLINK_TASKS_BY_EVENT)) {
            unlinkStmt.setString(1, event.getId().toString());
            unlinkStmt.executeUpdate();
        }

        if (!event.getTaskIds().isEmpty()) {
            try (PreparedStatement linkStmt = connection.prepareStatement(LINK_TASK_TO_EVENT)) {
                for (EventTaskId taskId : event.getTaskIds()) {
                    linkStmt.setString(1, event.getId().toString());
                    linkStmt.setString(2, taskId.id().toString());
                    linkStmt.executeUpdate();
                }
            }
        }
    }

    private Event mapRow(ResultSet result, List<EventTaskId> taskIds) throws SQLException {
        return Event.restore(
                UUID.fromString(result.getString("id")),
                EventTitle.of(result.getString("title")),
                EventCelebDate.of(result.getDate("date").toLocalDate()),
                EventRecurrenceType.valueOf(result.getString("recurrence")),
                result.getTimestamp("created_at").toLocalDateTime().toLocalDate(),
                taskIds
        );
    }
}
