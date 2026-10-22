package event.cli;

import common.utils.DateUtils;
import event.dto.EventDto;

import java.time.LocalDate;
import java.util.List;

public class EventFormatter {

    private EventFormatter() {}

    public static String format(EventDto eventDto) {
        return eventDto.title()
                + " | Date: " + DateUtils.formatDate(eventDto.celebDate())
                + " | Recurrence: " + eventDto.eventRecurrenceType()
                + " | Tasks: " + eventDto.taskIds().size()
                + " | Created: " + DateUtils.formatDate(eventDto.createdAt());
    }

    public static boolean printList(List<EventDto> events, String emptyMessage) {
        if (events.isEmpty()) {
            System.out.println(emptyMessage);
            return false;
        }
        for (int i = 0; i < events.size(); i++) {
            System.out.println((i + 1) + ". " + format(events.get(i)));
        }
        return true;
    }

    public static void printUpcomingNotifications(List<EventDto> upcomingEvents) {
        System.out.println("\n=== UPCOMING EVENTS REMINDER ===");
        if (upcomingEvents.isEmpty()) {
            System.out.println("No upcoming events scheduled.");
            return;
        }
        LocalDate today = LocalDate.now();
        List<EventDto> todayEvents = upcomingEvents.stream()
                .filter(event -> event.celebDate().equals(today))
                .toList();
        List<EventDto> soonEvents = upcomingEvents.stream()
                .filter(event -> event.celebDate().isAfter(today))
                .toList();

        if (!todayEvents.isEmpty()) {
            System.out.println("TODAY:");
            todayEvents.forEach(event -> System.out.println("- " + event.title()
                    + " (" + DateUtils.formatDate(event.celebDate()) + ")"));
        }
        if (!soonEvents.isEmpty()) {
            System.out.println("SOON:");
            soonEvents.forEach(event -> System.out.println("- " + event.title()
                    + " (" + DateUtils.formatDate(event.celebDate()) + ")"));
        }
    }
}
