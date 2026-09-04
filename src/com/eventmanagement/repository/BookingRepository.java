package com.eventmanagement.repository;

import com.eventmanagement.model.Booking;

import java.util.*;

public class BookingRepository {

    private final Map<Integer, Booking> bookingStore = new HashMap<>();

    public Booking save(Booking booking) {
        bookingStore.put(booking.getBookingId(), booking);
        return booking;
    }

    public Optional<Booking> findById(int bookingId) {
        return Optional.ofNullable(bookingStore.get(bookingId));
    }

    public List<Booking> findAll() {
        return bookingStore.values().stream()
                .sorted(Comparator.comparingInt(Booking::getBookingId))
                .toList();
    }

    public List<Booking> findByUserId(int userId) {
        List<Booking> result = new ArrayList<>();

        for (Booking booking : bookingStore.values()) {
            if (booking.getUserId() == userId) {
                result.add(booking);
            }
        }

        return result.stream()
                .sorted(Comparator.comparingInt(Booking::getBookingId))
                .toList();
    }

    public List<Booking> findByEventId(int eventId) {
        List<Booking> result = new ArrayList<>();

        for (Booking booking : bookingStore.values()) {

            if (booking.getEventId() == eventId) {
                result.add(booking);
            }

        }
        return result.stream()
                .sorted(Comparator.comparingInt(Booking::getBookingId))
                .toList();
    }
}
