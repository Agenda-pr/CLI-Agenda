package note.dao;

import common.db.ConnectionFactory;
import common.exception.DatabaseException;
import note.model.Note;
import note.model.NoteContent;
import task.model.Priority;
import task.model.Task;
import task.model.TaskDueDate;
import task.model.TaskTitle;

import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class NoteDAOAdapter implements NoteDAO {
    private static final String INSERT =
            "INSERT INTO notes (id, task_id, content, created_at) VALUES (?, ?, ?, ?)";
    private static final String UPDATE =
            "UPDATE notes SET content = ? WHERE id = ?";
    private static final String SELECT_BY_ID =
            "SELECT id, task_id, content, created_at FROM notes WHERE id = ?";
    private static final String SELECT_ALL =
            "SELECT id, task_id, content, created_at FROM notes ORDER BY created_at";
    private static final String DELETE =
            "DELETE FROM notes WHERE id = ?";

    @Override
    public void insert(Note note) {
        Connection connection = ConnectionFactory.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(INSERT)) {
            statement.setString(1, note.getId().toString());
            statement.setString(2, note.getTaskID().toString());
            statement.setString(3, note.getContent().content());
            statement.setTimestamp(4, Timestamp.valueOf(note.getCreatedAt()));
            statement.executeUpdate();
        } catch (SQLException error) {
            throw new DatabaseException("Could not insert note", error);
        }
    }

    @Override
    public boolean update(Note note) {
        Connection connection = ConnectionFactory.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(UPDATE)) {
            statement.setString(1, note.getContent().content());
            statement.setString(2, note.getId().toString());
            return statement.executeUpdate() > 0;
        } catch (SQLException error) {
            throw new DatabaseException("Could not update note", error);
        }
    }

    @Override
    public Optional<Note> findById(UUID id) {
        Connection connection = ConnectionFactory.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID)) {
            statement.setString(1, id.toString());
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return Optional.of(mapRow(result));
                }
                return Optional.empty();
            }
        } catch (SQLException error) {
            throw new DatabaseException("Could not find note", error);
        }
    }

    @Override
    public List<Note> findAll() {
        return List.of();
    }

    @Override
    public boolean deleteById(UUID id) {
        return false;
    }

    private Note mapRow(ResultSet result) throws SQLException {
        Date dueDate = result.getDate("due_date");
        return Note.restore(
                UUID.fromString(result.getString("id")),
                NoteContent.of(result.getString("content")),
                result.getTimestamp("created_at").toLocalDateTime()
        );
    }
}
