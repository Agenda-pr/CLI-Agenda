package common.config;

import common.cli.MainMenuCli;
import event.cli.EventCli;
import event.dao.EventDAO;
import event.dao.EventDAOAdapter;
import event.repository.DAOEventRepository;
import event.repository.EventRepository;
import event.service.EventService;
import note.cli.NoteCli;
import note.dao.NoteDAO;
import note.dao.NoteDAOAdapter;
import note.repository.DAONoteRepository;
import note.repository.NoteRepository;
import note.service.NoteService;
import task.cli.TaskCli;
import task.dao.TaskDAO;
import task.dao.TaskDAOAdapter;
import task.repository.DAOTaskRepository;
import task.repository.TaskRepository;
import task.service.TaskService;

public class AppInitializer {

    public static MainMenuCli init() {
        TaskDAO taskDao = new TaskDAOAdapter();
        NoteDAO noteDao = new NoteDAOAdapter();
        EventDAO eventDao = new EventDAOAdapter();

        TaskRepository taskRepository = new DAOTaskRepository(taskDao);
        NoteRepository noteRepository = new DAONoteRepository(noteDao);
        EventRepository eventRepository = new DAOEventRepository(eventDao);

        TaskService taskService = new TaskService(taskRepository);
        NoteService noteService = new NoteService(noteRepository, taskRepository);
        EventService eventService = new EventService(eventRepository, taskRepository);

        TaskCli taskCli = new TaskCli(taskService);
        NoteCli noteCli = new NoteCli(noteService, taskService);
        EventCli eventCli = new EventCli(eventService, taskService);

        return new MainMenuCli(taskCli, noteCli, eventCli);
    }
}
