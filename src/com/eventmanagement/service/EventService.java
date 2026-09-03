package com.eventmanagement.service;

import com.eventmanagement.exception.AccessDeniedException;
import com.eventmanagement.exception.CategoryNotFoundException;
import com.eventmanagement.exception.EventNotFoundException;
import com.eventmanagement.exception.ValidationException;
import com.eventmanagement.model.Event;
import com.eventmanagement.model.EventStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface EventService {

    Event addEvent(int organizerId, int categoryId, String eventName, String description, String venue,
                   String address, LocalDate eventDate, LocalTime eventTime, int totalSeats, double ticketPrice)
            throws ValidationException, CategoryNotFoundException;

    List<Event> getEventsByOrganizer(int organizerId);

    void updateEvent(int eventId, int organizerId, String eventName, String description, String venue,
                     String address, LocalDate eventDate, LocalTime eventTime, double ticketPrice)
            throws EventNotFoundException, ValidationException, AccessDeniedException;

    void deleteEvent(int eventId, int organizerId) throws EventNotFoundException, AccessDeniedException;

    Event getEventById(int eventId) throws EventNotFoundException;

    List<Event> getAllEvents();

    List<Event> searchByName(String keyword);

    List<Event> searchByCategory(int categoryId);

    List<Event> searchByDate(LocalDate date);

    List<Event> searchByVenue(String keyword);

    void changeEventStatus(int eventId, EventStatus newStatus) throws EventNotFoundException;

    void refreshEventStatusIfNeeded(Event event);
}