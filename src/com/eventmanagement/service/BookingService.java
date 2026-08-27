package com.eventmanagement.service;

import com.eventmanagement.exception.BookingNotFoundException;
import com.eventmanagement.exception.EventNotFoundException;
import com.eventmanagement.exception.InsufficientSeatsException;
import com.eventmanagement.exception.InvalidBookingException;
import com.eventmanagement.model.Booking;

import java.util.List;

public interface BookingService {

    Booking bookEvent(int userId, int eventId, int seatsRequested)
            throws EventNotFoundException, InsufficientSeatsException, InvalidBookingException;

    void cancelBooking(int bookingId, int userId) throws BookingNotFoundException, InvalidBookingException;

    List<Booking> getBookingsByUser(int userId);

    List<Booking> getBookingsByEvent(int eventId);

    List<Booking> getAllBookings();

    Booking getBookingById(int bookingId) throws BookingNotFoundException;
}
