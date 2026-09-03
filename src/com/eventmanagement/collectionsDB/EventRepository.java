package com.eventmanagement.collectionsDB;

import com.eventmanagement.model.Event;

import java.util.*;

public class EventRepository {

    private final Map<Integer, Event> eventStore = new HashMap<>();

    public Event save(Event event) {
        eventStore.put(event.getEventId(), event);
        return event;
    }

    public Optional<Event> findById(int eventId) {
        return Optional.ofNullable(eventStore.get(eventId));
    }

    public List<Event> findAll() {
        return eventStore.values().stream()
                .sorted(Comparator.comparingInt(Event::getEventId)).toList();
    }

    public List<Event> findByOrganizerId(int organizerId) {
        List<Event> result = new ArrayList<>();

        for (Event event : eventStore.values()) {
            if (event.getOrganizerId() == organizerId) {
                result.add(event);
            }
        }
        return result;
    }

    public void deleteById(int eventId) {
        eventStore.remove(eventId);
    }

}
