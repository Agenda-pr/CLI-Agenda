package event.service;

import event.dto.EventDto;

import java.util.List;

public interface EventObserver {
    void onUpcomingEvents(List<EventDto> upcomingEvents);
}
