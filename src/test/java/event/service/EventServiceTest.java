package event.service;

import common.exception.EventNotFound;
import common.exception.InvalidEventException;
import common.exception.TaskNotFound;
import event.dto.CreateEventDto;
import event.dto.EventDto;
import event.model.EventRecurrenceType;
import event.repository.InRamEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import task.model.Priority;
import task.model.Task;
import task.model.TaskTitle;
import task.repository.InRamTaskRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class EventServiceTest {
    private InRamEventRepository repository;
    private InRamTaskRepository taskRepository;
    private EventService service;

    @BeforeEach
    void ini() {
        repository = new InRamEventRepository();
        taskRepository = new InRamTaskRepository();
        service = new EventService(repository, taskRepository);
    }

    @Test
    void createEventSavesInRepository() {
        LocalDate date = LocalDate.now().plusDays(10);
        EventDto created = service.createEvent(
                new CreateEventDto("Cumpleaños", date, EventRecurrenceType.YEARLY));

        assertEquals(1, repository.showAllEvents().size());
        assertEquals("Cumpleaños", created.title());
        assertEquals(date, created.celebDate());
        assertEquals(EventRecurrenceType.YEARLY, created.eventRecurrenceType());
        assertTrue(created.taskIds().isEmpty());
    }

    @Test
    void createEventTrimsTitle() {
        EventDto created = service.createEvent(
                new CreateEventDto("   Reunión   ", LocalDate.now().plusDays(5), EventRecurrenceType.NONE));

        assertEquals("Reunión", created.title());
    }

    @Test
    void createEventWithNullRecurrenceDefaultsToNone() {
        EventDto created = service.createEvent(
                new CreateEventDto("Cita médica", LocalDate.now().plusDays(3), null));

        assertEquals(EventRecurrenceType.NONE, created.eventRecurrenceType());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    void createEventThrowsExceptionWhenTitleIsInvalid(String invalidTitle) {
        assertThrows(InvalidEventException.class,
                () -> service.createEvent(new CreateEventDto(invalidTitle, LocalDate.now(), EventRecurrenceType.NONE)));

        assertTrue(repository.showAllEvents().isEmpty());
    }

    @Test
    void createEventThrowsExceptionWhenDateIsNull() {
        assertThrows(InvalidEventException.class,
                () -> service.createEvent(new CreateEventDto("Evento sin fecha", null, EventRecurrenceType.NONE)));

        assertTrue(repository.showAllEvents().isEmpty());
    }

    @Test
    void createEventThrowsExceptionWhenDateIsBeforeYear2000() {
        assertThrows(InvalidEventException.class,
                () -> service.createEvent(
                        new CreateEventDto("Evento antiguo", LocalDate.of(1999, 12, 31), EventRecurrenceType.NONE)));

        assertTrue(repository.showAllEvents().isEmpty());
    }

    @Nested
    class ListAndUpcomingEvents {
        @Test
        void listEventsByCreationReturnsAllEvents() {
            EventDto event1 = service.createEvent(
                    new CreateEventDto("Evento 1", LocalDate.now().plusDays(20), EventRecurrenceType.NONE));
            EventDto event2 = service.createEvent(
                    new CreateEventDto("Evento 2", LocalDate.now().plusDays(5), EventRecurrenceType.NONE));

            List<EventDto> result = service.listEventsByCreation();

            assertEquals(List.of(event1, event2), result);
        }

        @Test
        void listEventsByDateSortsClosestFirst() {
            EventDto later = service.createEvent(
                    new CreateEventDto("Más tarde", LocalDate.now().plusDays(30), EventRecurrenceType.NONE));
            EventDto sooner = service.createEvent(
                    new CreateEventDto("Más pronto", LocalDate.now().plusDays(2), EventRecurrenceType.NONE));
            EventDto middle = service.createEvent(
                    new CreateEventDto("En medio", LocalDate.now().plusDays(10), EventRecurrenceType.NONE));

            List<EventDto> result = service.listEventsByDate();

            assertEquals(List.of(sooner, middle, later), result);
        }

        @Test
        void refreshRecurringEventsAdvancesPastRecurringEventToNextOccurrence() {
            LocalDate pastDate = LocalDate.now().minusYears(1).plusDays(5);
            EventDto created = service.createEvent(
                    new CreateEventDto("Aniversario", pastDate, EventRecurrenceType.YEARLY));

            List<EventDto> result = service.listEventsByDate();

            assertEquals(1, result.size());
            assertEquals(created.id(), result.get(0).id());
            assertEquals(pastDate.plusYears(1), result.get(0).celebDate());
        }

        @Test
        void refreshRecurringEventsKeepsPastDateWhenRecurrenceIsNone() {
            LocalDate pastDate = LocalDate.now().minusDays(10);
            service.createEvent(
                    new CreateEventDto("Evento único pasado", pastDate, EventRecurrenceType.NONE));

            List<EventDto> result = service.listEventsByDate();

            assertEquals(1, result.size());
            assertEquals(pastDate, result.get(0).celebDate());
        }

        @Test
        void listUpcomingEventsReturnsOnlyEventsWithinDaysAheadRange() {
            EventDto todayEvent = service.createEvent(
                    new CreateEventDto("Hoy", LocalDate.now(), EventRecurrenceType.NONE));
            EventDto inThreeDays = service.createEvent(
                    new CreateEventDto("En 3 días", LocalDate.now().plusDays(3), EventRecurrenceType.NONE));
            service.createEvent(
                    new CreateEventDto("En 15 días", LocalDate.now().plusDays(15), EventRecurrenceType.NONE));
            service.createEvent(
                    new CreateEventDto("Pasado sin repetición", LocalDate.now().minusDays(2), EventRecurrenceType.NONE));

            List<EventDto> upcoming = service.listUpcomingEvents(7);

            assertEquals(List.of(todayEvent, inThreeDays), upcoming);
        }

        @Test
        void listUpcomingEventsWithZeroDaysReturnsOnlyTodayEvents() {
            EventDto todayEvent = service.createEvent(
                    new CreateEventDto("Hoy", LocalDate.now(), EventRecurrenceType.NONE));
            service.createEvent(
                    new CreateEventDto("Mañana", LocalDate.now().plusDays(1), EventRecurrenceType.NONE));

            List<EventDto> todayOnly = service.listUpcomingEvents(0);

            assertEquals(List.of(todayEvent), todayOnly);
        }

        @Test
        void listUpcomingEventsThrowsExceptionWhenDaysAheadIsNegative() {
            assertThrows(InvalidEventException.class, () -> service.listUpcomingEvents(-1));
        }
    }

    @Nested
    class FindEventById {
        @Test
        void findEventByIdReturnsEvent() {
            EventDto created = service.createEvent(
                    new CreateEventDto("Evento 1", LocalDate.now().plusDays(5), EventRecurrenceType.MONTHLY));

            EventDto found = service.findEventById(created.id());

            assertEquals(created.id(), found.id());
            assertEquals("Evento 1", found.title());
            assertEquals(EventRecurrenceType.MONTHLY, found.eventRecurrenceType());
        }

        @Test
        void findEventByIdThrowsExceptionUnknownId() {
            assertThrows(EventNotFound.class, () -> service.findEventById(UUID.randomUUID()));
        }
    }

    @Nested
    class UpdateEvent {
        @Test
        void updateEventModifiesEventFields() {
            EventDto created = service.createEvent(
                    new CreateEventDto("Original", LocalDate.now().plusDays(2), EventRecurrenceType.NONE));
            LocalDate newDate = LocalDate.now().plusDays(10);

            EventDto updatedDto = new EventDto(
                    created.id(),
                    "Actualizado",
                    newDate,
                    EventRecurrenceType.WEEKLY,
                    created.createdAt(),
                    created.taskIds()
            );

            EventDto result = service.updateEvent(updatedDto);

            assertEquals("Actualizado", result.title());
            assertEquals(newDate, result.celebDate());
            assertEquals(EventRecurrenceType.WEEKLY, result.eventRecurrenceType());
            assertEquals(1, repository.showAllEvents().size());
        }

        @Test
        void updateEventTrimsTitle() {
            EventDto created = service.createEvent(
                    new CreateEventDto("Original", LocalDate.now().plusDays(2), EventRecurrenceType.NONE));

            EventDto updatedDto = new EventDto(
                    created.id(),
                    "   Título Limpio   ",
                    created.celebDate(),
                    created.eventRecurrenceType(),
                    created.createdAt(),
                    created.taskIds()
            );

            EventDto result = service.updateEvent(updatedDto);

            assertEquals("Título Limpio", result.title());
        }

        @Test
        void updateEventThrowsExceptionUnknownId() {
            EventDto nonExistent = new EventDto(
                    UUID.randomUUID(),
                    "Alguno",
                    LocalDate.now(),
                    EventRecurrenceType.NONE,
                    LocalDate.now(),
                    List.of()
            );

            assertThrows(EventNotFound.class, () -> service.updateEvent(nonExistent));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        void updateEventThrowsExceptionInvalidTitle(String newTitle) {
            EventDto created = service.createEvent(
                    new CreateEventDto("Válido", LocalDate.now().plusDays(2), EventRecurrenceType.NONE));

            EventDto invalidDto = new EventDto(
                    created.id(),
                    newTitle,
                    created.celebDate(),
                    created.eventRecurrenceType(),
                    created.createdAt(),
                    created.taskIds()
            );

            assertThrows(InvalidEventException.class, () -> service.updateEvent(invalidDto));
        }

        @Test
        void updateEventThrowsExceptionDateBeforeYear2000() {
            EventDto created = service.createEvent(
                    new CreateEventDto("Válido", LocalDate.now().plusDays(2), EventRecurrenceType.NONE));

            EventDto invalidDto = new EventDto(
                    created.id(),
                    "Válido",
                    LocalDate.of(1999, 12, 31),
                    created.eventRecurrenceType(),
                    created.createdAt(),
                    created.taskIds()
            );

            assertThrows(InvalidEventException.class, () -> service.updateEvent(invalidDto));
        }
    }

    @Nested
    class EventTasks {
        private EventDto existingEvent;
        private Task existingTask;

        @BeforeEach
        void setUp() {
            existingEvent = service.createEvent(
                    new CreateEventDto("Fiesta", LocalDate.now().plusDays(5), EventRecurrenceType.NONE));
            existingTask = Task.create(TaskTitle.of("Comprar bebida"), Priority.HIGH);
            taskRepository.save(existingTask);
        }

        @Test
        void addTaskToEventAssociatesExistingTask() {
            EventDto updated = service.addTaskToEvent(existingEvent.id(), existingTask.getId());

            assertEquals(List.of(existingTask.getId()), updated.taskIds());
            assertEquals(List.of(existingTask.getId()), service.findEventById(existingEvent.id()).taskIds());
        }

        @Test
        void addTaskToEventThrowsEventNotFoundWhenEventDoesNotExist() {
            assertThrows(EventNotFound.class,
                    () -> service.addTaskToEvent(UUID.randomUUID(), existingTask.getId()));
        }

        @Test
        void addTaskToEventThrowsTaskNotFoundWhenTaskDoesNotExist() {
            assertThrows(TaskNotFound.class,
                    () -> service.addTaskToEvent(existingEvent.id(), UUID.randomUUID()));
        }

        @Test
        void removeTaskFromEventRemovesTaskAssociation() {
            service.addTaskToEvent(existingEvent.id(), existingTask.getId());

            EventDto updated = service.removeTaskFromEvent(existingEvent.id(), existingTask.getId());

            assertTrue(updated.taskIds().isEmpty());
            assertTrue(service.findEventById(existingEvent.id()).taskIds().isEmpty());
        }
    }

    @Nested
    class DeleteEventById {
        @Test
        void deleteEventByIdRemovesEventFromRepository() {
            EventDto created = service.createEvent(
                    new CreateEventDto("Evento a borrar", LocalDate.now().plusDays(5), EventRecurrenceType.NONE));

            service.deleteEventById(created.id());

            assertTrue(repository.showAllEvents().isEmpty());
            assertNull(repository.findEventById(created.id()));
        }

        @Test
        void deleteEventByIdThrowsExceptionUnknownId() {
            assertThrows(EventNotFound.class, () -> service.deleteEventById(UUID.randomUUID()));
        }
    }
}
