package com.eventmanagement.dao;

import com.eventmanagement.model.Event;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EventRepository {

    private final Map<Integer, Event> eventStore = new LinkedHashMap<>();

    public Event save(Event event) {
        eventStore.put(event.getEventId(), event);
        return event;
    }

    public Optional<Event> findById(int eventId) {
        return Optional.ofNullable(eventStore.get(eventId));
    }

    public List<Event> findAll() {
        return new ArrayList<>(eventStore.values());
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

    public boolean existsById(int eventId) {
        return eventStore.containsKey(eventId);
    }
}
