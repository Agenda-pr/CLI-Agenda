package task.dao;

import common.db.ConnectionFactory;
import common.exception.DatabaseException;
import task.model.Priority;
import task.model.Task;
import task.model.TaskDueDate;
import task.model.TaskTitle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TaskDAOAdapter implements TaskDAO {
    private static final String INSERT =
            "INSERT INTO tasks (id, title, priority, completed, due_date, created_at) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE =
            "UPDATE tasks SET title = ?, priority = ?, completed = ?, due_date = ? WHERE id = ?";
    private static final String SELECT_BY_ID =
            "SELECT id, title, priority, completed, due_date, created_at FROM tasks WHERE id = ?";
    private static final String SELECT_ALL =
            "SELECT id, title, priority, completed, due_date, created_at FROM tasks ORDER BY created_at";
    private static final String DELETE =
            "DELETE FROM tasks WHERE id = ?";

    @Override
    public void insert(Task task) {
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT)) {
            statement.setString(1, task.getId().toString());
            statement.setString(2, task.getTitle().title());
            statement.setString(3, task.getPriority().name());
            statement.setBoolean(4, task.isCompleted());
            setDueDate(statement, 5, task.getDueDate());
            statement.setTimestamp(6, Timestamp.valueOf(task.getCreatedAt()));
            statement.executeUpdate();
        } catch (SQLException error) {
            throw new DatabaseException("Could not insert task", error);
        }
    }

    @Override
    public boolean update(Task task) {
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE)) {
            statement.setString(1, task.getTitle().title());
            statement.setString(2, task.getPriority().name());
            statement.setBoolean(3, task.isCompleted());
            setDueDate(statement, 4, task.getDueDate());
            statement.setString(5, task.getId().toString());
            return statement.executeUpdate() > 0;
        } catch (SQLException error) {
            throw new DatabaseException("Could not update task", error);
        }
    }

    @Override
    public Optional<Task> findById(UUID id) {
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID)) {
            statement.setString(1, id.toString());
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return Optional.of(mapRow(result));
                }
                return Optional.empty();
            }
        } catch (SQLException error) {
            throw new DatabaseException("Could not find task", error);
        }
    }

    @Override
    public List<Task> findAll() {
        List<Task> tasks = new ArrayList<>();
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                tasks.add(mapRow(result));
            }
            return tasks;
        } catch (SQLException error) {
            throw new DatabaseException("Could not list tasks", error);
        }
    }

    @Override
    public boolean deleteById(UUID id) {
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE)) {
            statement.setString(1, id.toString());
            return statement.executeUpdate() > 0;
        } catch (SQLException error) {
            throw new DatabaseException("Could not delete task", error);
        }
    }

    private void setDueDate(PreparedStatement statement, int index, TaskDueDate dueDate) throws SQLException {
        if (dueDate == null) {
            statement.setNull(index, Types.DATE);
        } else {
            statement.setDate(index, Date.valueOf(dueDate.date()));
        }
    }

    private Task mapRow(ResultSet result) throws SQLException {
        Date dueDate = result.getDate("due_date");
        return Task.restore(
                UUID.fromString(result.getString("id")),
                TaskTitle.of(result.getString("title")),
                Priority.valueOf(result.getString("priority")),
                result.getBoolean("completed"),
                TaskDueDate.restore(dueDate == null ? null : dueDate.toLocalDate()),
                result.getTimestamp("created_at").toLocalDateTime()
        );
    }
}
