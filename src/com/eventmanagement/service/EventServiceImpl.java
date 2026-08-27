package com.eventmanagement.service;

import com.eventmanagement.dao.EventRepository;
import com.eventmanagement.exception.AccessDeniedException;
import com.eventmanagement.exception.CategoryNotFoundException;
import com.eventmanagement.exception.EventNotFoundException;
import com.eventmanagement.exception.ValidationException;
import com.eventmanagement.model.Event;
import com.eventmanagement.model.EventStatus;
import com.eventmanagement.util.IdGenerator;
import com.eventmanagement.util.ValidationUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final IdGenerator eventIdGenerator;
    private final CategoryService categoryService;

    public EventServiceImpl(EventRepository eventRepository, IdGenerator eventIdGenerator,
                            CategoryService categoryService) {
        this.eventRepository = eventRepository;
        this.eventIdGenerator = eventIdGenerator;
        this.categoryService = categoryService;
    }

    @Override
    public Event addEvent(int organizerId, int categoryId, String eventName, String description, String venue,
                          String city, String address, LocalDate eventDate, LocalTime eventTime,
                          int totalSeats, double ticketPrice)
            throws ValidationException, CategoryNotFoundException {

        validateEventFields(eventName, venue, city, eventDate, totalSeats, ticketPrice);

        // Make sure the category actually exists before attaching the event to it.
        categoryService.getCategoryById(categoryId);

        Event event = new Event(eventIdGenerator.nextId(), organizerId, categoryId, eventName, description,
                venue, city, address, eventDate, eventTime, totalSeats, ticketPrice);

        eventRepository.save(event);
        return event;
    }

    @Override
    public List<Event> getEventsByOrganizer(int organizerId) {
        List<Event> events = eventRepository.findByOrganizerId(organizerId);
        events.forEach(this::refreshEventStatusIfNeeded);
        return events;
    }

    @Override
    public void updateEvent(int eventId, int organizerId, String eventName, String description, String venue,
                            String city, String address, LocalDate eventDate, LocalTime eventTime,
                            double ticketPrice)
            throws EventNotFoundException, ValidationException, AccessDeniedException {

        Event event = getEventById(eventId);
        ensureOwnership(event, organizerId);

        if (ValidationUtil.isEmpty(eventName)) {
            throw new ValidationException("Event name cannot be empty.");
        }
        if (ValidationUtil.isEmpty(venue)) {
            throw new ValidationException("Venue cannot be empty.");
        }
        if (ValidationUtil.isEmpty(city)) {
            throw new ValidationException("City cannot be empty.");
        }
        if (!ValidationUtil.isFutureOrTodayDate(eventDate)) {
            throw new ValidationException("Event date cannot be in the past.");
        }
        if (!ValidationUtil.isNonNegativeNumber(ticketPrice)) {
            throw new ValidationException("Ticket price cannot be negative.");
        }

        event.setEventName(eventName);
        event.setDescription(description);
        event.setVenue(venue);
        event.setCity(city);
        event.setAddress(address);
        event.setEventDate(eventDate);
        event.setEventTime(eventTime);
        event.setTicketPrice(ticketPrice);

        eventRepository.save(event);
    }

    @Override
    public void deleteEvent(int eventId, int organizerId) throws EventNotFoundException, AccessDeniedException {
        Event event = getEventById(eventId);
        ensureOwnership(event, organizerId);
        eventRepository.deleteById(eventId);
    }

    @Override
    public Event getEventById(int eventId) throws EventNotFoundException {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("No event found with id " + eventId));
        refreshEventStatusIfNeeded(event);
        return event;
    }

    @Override
    public List<Event> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        events.forEach(this::refreshEventStatusIfNeeded);
        return events;
    }

    @Override
    public List<Event> searchByName(String keyword) {
        List<Event> result = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();
        for (Event event : getAllEvents()) {
            boolean nameMatches = event.getEventName().toLowerCase().contains(lowerKeyword);
            boolean descriptionMatches = event.getDescription() != null
                    && event.getDescription().toLowerCase().contains(lowerKeyword);

            if (nameMatches || descriptionMatches) {
                result.add(event);
            }
        }
        return result;
    }

    @Override
    public List<Event> searchByCategory(int categoryId) {
        List<Event> result = new ArrayList<>();
        for (Event event : getAllEvents()) {
            if (event.getCategoryId() == categoryId) {
                result.add(event);
            }
        }
        return result;
    }

    @Override
    public List<Event> searchByDate(LocalDate date) {
        List<Event> result = new ArrayList<>();
        for (Event event : getAllEvents()) {
            if (event.getEventDate().equals(date)) {
                result.add(event);
            }
        }
        return result;
    }

    @Override
    public List<Event> searchByVenue(String keyword) {
        List<Event> result = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();
        for (Event event : getAllEvents()) {
            boolean venueMatches = event.getVenue().toLowerCase().contains(lowerKeyword);
            boolean cityMatches = event.getCity() != null && event.getCity().toLowerCase().contains(lowerKeyword);
            boolean addressMatches = event.getAddress() != null
                    && event.getAddress().toLowerCase().contains(lowerKeyword);

            if (venueMatches || cityMatches || addressMatches) {
                result.add(event);
            }
        }
        return result;
    }

    @Override
    public void changeEventStatus(int eventId, EventStatus newStatus) throws EventNotFoundException {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("No event found with id " + eventId));
        event.setStatus(newStatus);
        eventRepository.save(event);
    }

    @Override
    public void refreshEventStatusIfNeeded(Event event) {
        // Once an event is cancelled by an admin or organizer, it should stay cancelled.
        if (event.getStatus() == EventStatus.CANCELLED) {
            return;
        }

        LocalDate today = LocalDate.now();
        if (event.getEventDate().isBefore(today)) {
            event.setStatus(EventStatus.COMPLETED);
        } else if (event.getEventDate().isEqual(today)) {
            event.setStatus(EventStatus.ONGOING);
        } else {
            event.setStatus(EventStatus.UPCOMING);
        }
    }

    private void validateEventFields(String eventName, String venue, String city, LocalDate eventDate,
                                     int totalSeats, double ticketPrice) throws ValidationException {
        if (ValidationUtil.isEmpty(eventName)) {
            throw new ValidationException("Event name cannot be empty.");
        }
        if (ValidationUtil.isEmpty(venue)) {
            throw new ValidationException("Venue cannot be empty.");
        }
        if (ValidationUtil.isEmpty(city)) {
            throw new ValidationException("City cannot be empty.");
        }
        if (!ValidationUtil.isFutureOrTodayDate(eventDate)) {
            throw new ValidationException("Event date cannot be in the past.");
        }
        if (totalSeats <= 0) {
            throw new ValidationException("Total seats must be greater than zero.");
        }
        if (!ValidationUtil.isNonNegativeNumber(ticketPrice)) {
            throw new ValidationException("Ticket price cannot be negative.");
        }
    }

    private void ensureOwnership(Event event, int organizerId) throws AccessDeniedException {
        if (event.getOrganizerId() != organizerId) {
            throw new AccessDeniedException("You are not allowed to modify an event you do not own.");
        }
    }
}