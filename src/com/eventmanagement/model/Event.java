package com.eventmanagement.model;

import com.eventmanagement.util.DateUtil;

import java.time.LocalDate;
import java.time.LocalTime;

public class Event {

    private int eventId;
    private int organizerId;
    private int categoryId;
    private String eventName;
    private String description;
    private String venue;
    private String address;
    private LocalDate eventDate;
    private LocalTime eventTime;
    private int totalSeats;
    private int availableSeats;
    private double ticketPrice;
    private EventStatus status;

    public Event(int eventId, int organizerId, int categoryId, String eventName, String description,
                 String venue, String address, LocalDate eventDate, LocalTime eventTime,
                 int totalSeats, double ticketPrice) {
        this.eventId = eventId;
        this.organizerId = organizerId;
        this.categoryId = categoryId;
        this.eventName = eventName;
        this.description = description;
        this.venue = venue;
        this.address = address;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.totalSeats = totalSeats;
        this.availableSeats = totalSeats;
        this.ticketPrice = ticketPrice;
        this.status = EventStatus.UPCOMING;
    }

    public int getEventId() {
        return eventId;
    }

    public int getOrganizerId() {
        return organizerId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public LocalTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalTime eventTime) {
        this.eventTime = eventTime;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(double ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Event ID        : " + eventId +
                "\nEvent Name      : " + eventName +
                "\nDescription     : " + description +
                "\nVenue           : " + venue +
                "\nAddress         : " + address +
                "\nDate            : " + DateUtil.formatDate(eventDate) +
                "\nTime            : " + eventTime +
                "\nTotal Seats     : " + totalSeats +
                "\nAvailable Seats : " + availableSeats +
                "\nTicket Price    : " + ticketPrice +
                "\nStatus          : " + status;
    }
}