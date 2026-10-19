package task.service;

import common.exception.InvalidTaskException;
import common.exception.TaskNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import task.dto.CreateTaskDto;
import task.dto.TaskDto;
import task.model.Priority;
import task.repository.InRamRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TaskServiceTest {
    private InRamRepository repository;
    private TaskService service;

    @BeforeEach
    void ini() {
        repository = new InRamRepository();
        service = new TaskService(repository);
    }

    @Test
    void createTaskSavesInRepository() {
        TaskDto createdTask = service.createTask(new CreateTaskDto("Task 1", Priority.LOW, null));

        assertEquals(1, repository.showAllTasks().size());
        assertEquals("Task 1", createdTask.title());
    }

    @Test
    void createTaskTrimTitle() {
        TaskDto task1 = service.createTask(new CreateTaskDto("    Task1", Priority.HIGH, null));

        assertEquals("Task1", task1.title());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void createTaskThrowsException(String title) {
        assertThrows(InvalidTaskException.class,
                () -> service.createTask(new CreateTaskDto(title, Priority.LOW, null)));

        assertTrue(repository.showAllTasks().isEmpty());
    }

    @Test
    void createTaskThrowsExceptionWhenDueDateInPast() {
        assertThrows(InvalidTaskException.class,
                () -> service.createTask(new CreateTaskDto("Task 1", Priority.LOW, LocalDate.now().minusDays(1))));

        assertTrue(repository.showAllTasks().isEmpty());
    }

    @Nested
    class ListTask {
        private TaskDto task1;
        private TaskDto task2;
        private TaskDto task3;

        @BeforeEach
        void set() {
            task1 = service.createTask(new CreateTaskDto("Task 1", Priority.LOW, LocalDate.of(2026, 12, 10)));
            task2 = service.createTask(new CreateTaskDto("Task 2", Priority.MEDIUM, LocalDate.now().plusDays(3)));
            task3 = service.createTask(new CreateTaskDto("Task 3", Priority.HIGH, null));
        }

        @Test
        void listTasksSortByCreationDate() {
            assertEquals(List.of(task1, task2, task3), service.listTasksByCreation());
        }

        @Test
        void listTasksReturnsAll() {
            List<TaskDto> listResult = service.listTasksByCreation();

            assertEquals(3, listResult.size());
            assertTrue(listResult.containsAll(List.of(task1, task2, task3)));
        }
    }

    @Nested
    class FindTaskById {
        @Test
        void findTaskByIdReturnsTask() {
            TaskDto created = service.createTask(new CreateTaskDto("Task 1", Priority.LOW, null));

            TaskDto found = service.findTaskById(created.id());

            assertEquals(created.id(), found.id());
            assertEquals("Task 1", found.title());
            assertEquals(Priority.LOW, found.priority());
        }

        @Test
        void findTaskByIdThrowsExceptionUnknownId() {
            assertThrows(TaskNotFound.class, () -> service.findTaskById(UUID.randomUUID()));
        }
    }

    @Nested
    class UpdateTask {
        @Test
        void updateTaskModifiesTaskFields() {
            TaskDto created = service.createTask(new CreateTaskDto("Original Title", Priority.LOW, null));
            LocalDate newDate = LocalDate.now().plusDays(5);

            TaskDto updatedDto = new TaskDto(
                    created.id(),
                    "Updated Title",
                    Priority.HIGH,
                    true,
                    newDate,
                    created.createdAt()
            );

            TaskDto result = service.updateTask(updatedDto);

            assertEquals("Updated Title", result.title());
            assertEquals(Priority.HIGH, result.priority());
            assertTrue(result.completed());
            assertEquals(newDate, result.dueDate());
            assertEquals(1, repository.showAllTasks().size());
        }

        @Test
        void updateTaskTrimTitle() {
            TaskDto created = service.createTask(new CreateTaskDto("Original Title", Priority.LOW, null));

            TaskDto updatedDto = new TaskDto(
                    created.id(),
                    "   Trimmed Title   ",
                    Priority.MEDIUM,
                    false,
                    null,
                    created.createdAt()
            );

            TaskDto result = service.updateTask(updatedDto);

            assertEquals("Trimmed Title", result.title());
        }

        @Test
        void updateTaskThrowsExceptionUnknownId() {
            TaskDto nonExistent = new TaskDto(
                    UUID.randomUUID(),
                    "Some Title",
                    Priority.MEDIUM,
                    false,
                    null,
                    LocalDateTime.now()
            );

            assertThrows(TaskNotFound.class, () -> service.updateTask(nonExistent));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        void updateTaskThrowsExceptionInvalidTitle(String newTitle) {
            TaskDto created = service.createTask(new CreateTaskDto("Valid Title", Priority.LOW, null));

            TaskDto invalidDto = new TaskDto(
                    created.id(),
                    newTitle,
                    Priority.LOW,
                    false,
                    null,
                    created.createdAt()
            );

            assertThrows(InvalidTaskException.class, () -> service.updateTask(invalidDto));
        }

        @Test
        void updateTaskThrowsExceptionDueDateInPast() {
            TaskDto created = service.createTask(new CreateTaskDto("Valid Title", Priority.LOW, null));

            TaskDto invalidDto = new TaskDto(
                    created.id(),
                    "Valid Title",
                    Priority.LOW,
                    false,
                    LocalDate.now().minusDays(2),
                    created.createdAt()
            );

            assertThrows(InvalidTaskException.class, () -> service.updateTask(invalidDto));
        }
    }

    @Nested
    class DeleteTaskByID {
        @Test
        void deleteTaskByIDRemovesTaskFromRepository() {
            TaskDto created = service.createTask(new CreateTaskDto("Task 1", Priority.LOW, null));

            service.deleteTaskByID(created.id());

            assertTrue(repository.showAllTasks().isEmpty());
            assertNull(repository.findTaskByID(created.id()));
        }

        @Test
        void deleteTaskByIDThrowsExceptionUnknownId() {
            assertThrows(TaskNotFound.class, () -> service.deleteTaskByID(UUID.randomUUID()));
        }
    }
}
