package com.eventmanagement.collectionsDB;

import com.eventmanagement.model.Booking;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BookingRepository {

    private final Map<Integer, Booking> bookingStore = new LinkedHashMap<>();

    public Booking save(Booking booking) {
        bookingStore.put(booking.getBookingId(), booking);
        return booking;
    }

    public Optional<Booking> findById(int bookingId) {
        return Optional.ofNullable(bookingStore.get(bookingId));
    }

    public List<Booking> findAll() {
        return new ArrayList<>(bookingStore.values());
    }

    public List<Booking> findByUserId(int userId) {
        List<Booking> result = new ArrayList<>();

        for (Booking booking : bookingStore.values()) {
            if (booking.getUserId() == userId) {
                result.add(booking);
            }
        }

        return result;
    }

    public List<Booking> findByEventId(int eventId) {
        List<Booking> result = new ArrayList<>();

        for (Booking booking : bookingStore.values()) {

            if (booking.getEventId() == eventId) {
                result.add(booking);
            }

        }
        return result;
    }
}
