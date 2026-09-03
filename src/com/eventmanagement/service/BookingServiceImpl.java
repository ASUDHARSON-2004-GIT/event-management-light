package com.eventmanagement.service;

import com.eventmanagement.collectionsDB.BookingRepository;
import com.eventmanagement.collectionsDB.EventRepository;
import com.eventmanagement.exception.BookingNotFoundException;
import com.eventmanagement.exception.EventNotFoundException;
import com.eventmanagement.exception.InsufficientSeatsException;
import com.eventmanagement.exception.InvalidBookingException;
import com.eventmanagement.model.Booking;
import com.eventmanagement.model.BookingStatus;
import com.eventmanagement.model.Event;
import com.eventmanagement.model.EventStatus;
import com.eventmanagement.util.IdGenerator;

import java.util.List;

public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final IdGenerator bookingIdGenerator;

    public BookingServiceImpl(BookingRepository bookingRepository, EventRepository eventRepository,
                               IdGenerator bookingIdGenerator) {
        this.bookingRepository = bookingRepository;
        this.eventRepository = eventRepository;
        this.bookingIdGenerator = bookingIdGenerator;
    }

    @Override
    public Booking bookEvent(int userId, int eventId, int seatsRequested)
            throws EventNotFoundException, InsufficientSeatsException, InvalidBookingException {

        if (seatsRequested <= 0) {
            throw new InvalidBookingException("Number of seats must be greater than zero.");
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("No event found with id " + eventId));

        if (event.getStatus() == EventStatus.CANCELLED || event.getStatus() == EventStatus.COMPLETED) {
            throw new InvalidBookingException("This event is no longer open for booking.");
        }

        synchronized (event) {

            if (seatsRequested > event.getAvailableSeats()) {
                throw new InsufficientSeatsException(
                        "Not enough seats available. Only " + event.getAvailableSeats() + " seats are remaining.");
            }

            int seatsBeforeBooking = event.getAvailableSeats();
            double totalAmount = seatsRequested * event.getTicketPrice();

            event.setAvailableSeats(event.getAvailableSeats() - seatsRequested);

            try {
                Booking booking = new Booking(bookingIdGenerator.nextId(), userId, eventId, seatsRequested, totalAmount);
                bookingRepository.save(booking);
                eventRepository.save(event);
                return booking;

            } catch (RuntimeException unexpectedError) {
                event.setAvailableSeats(seatsBeforeBooking);
                throw new InvalidBookingException("Booking could not be completed, please try again.");
            }
        }
    }

    @Override
    public void cancelBooking(int bookingId, int userId) throws BookingNotFoundException, InvalidBookingException {

        Booking booking = getBookingById(bookingId);

        if (booking.getUserId() != userId) {
            throw new InvalidBookingException("You can only cancel your own bookings.");
        }
        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new InvalidBookingException("This booking is already cancelled.");
        }

        Event event = eventRepository.findById(booking.getEventId()).orElse(null);

        if (event != null) {
            synchronized (event) {
                event.setAvailableSeats(event.getAvailableSeats() + booking.getSeatsBooked());
                eventRepository.save(event);
            }
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    @Override
    public List<Booking> getBookingsByUser(int userId) {
        return bookingRepository.findByUserId(userId);
    }

    @Override
    public List<Booking> getBookingsByEvent(int eventId) {
        return bookingRepository.findByEventId(eventId);
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public Booking getBookingById(int bookingId) throws BookingNotFoundException {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("No booking found with id " + bookingId));
    }
}
