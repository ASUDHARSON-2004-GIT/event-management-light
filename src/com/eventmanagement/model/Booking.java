package com.eventmanagement.model;

import java.time.LocalDateTime;

public class Booking {

    private final int bookingId;
    private final int userId;
    private final int eventId;
    private final int seatsBooked;
    private final double totalAmount;
    private final LocalDateTime bookingTimestamp;
    private BookingStatus bookingStatus;

    public Booking(int bookingId, int userId, int eventId, int seatsBooked, double totalAmount) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.eventId = eventId;
        this.seatsBooked = seatsBooked;
        this.totalAmount = totalAmount;
        this.bookingTimestamp = LocalDateTime.now();
        this.bookingStatus = BookingStatus.CONFIRMED;
    }

    public int getBookingId() {
        return bookingId;
    }

    public int getUserId() {
        return userId;
    }

    public int getEventId() {
        return eventId;
    }

    public int getSeatsBooked() {
        return seatsBooked;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getBookingTimestamp() {
        return bookingTimestamp;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    @Override
    public String toString() {
        return "Booking ID   : " + bookingId +
                "\nEvent ID     : " + eventId +
                "\nSeats Booked : " + seatsBooked +
                "\nTotal Amount : " + totalAmount +
                "\nBooked On    : " + bookingTimestamp +
                "\nStatus       : " + bookingStatus;
    }
}
