package com.eventmanagement.service;

import com.eventmanagement.exception.EventNotFoundException;
import com.eventmanagement.exception.UserNotFoundException;
import com.eventmanagement.model.Booking;
import com.eventmanagement.model.BookingStatus;
import com.eventmanagement.model.Event;
import com.eventmanagement.model.Role;
import com.eventmanagement.model.User;

import java.util.List;

public class ReportServiceImpl implements ReportService {

    private final UserService userService;
    private final EventService eventService;
    private final BookingService bookingService;

    public ReportServiceImpl(UserService userService, EventService eventService, BookingService bookingService) {
        this.userService = userService;
        this.eventService = eventService;
        this.bookingService = bookingService;
    }

    @Override
    public String generateSystemReport() {

        int totalUsers = userService.getAllUsersByRole(Role.CUSTOMER).size();
        int totalOrganizers = userService.getAllUsersByRole(Role.ORGANIZER).size();
        int totalEvents = eventService.getAllEvents().size();

        List<Booking> allBookings = bookingService.getAllBookings();
        int totalBookings = allBookings.size();

        double totalRevenue = 0;
        for (Booking booking : allBookings) {
            if (booking.getBookingStatus() == BookingStatus.CONFIRMED) {
                totalRevenue += booking.getTotalAmount();
            }
        }

        StringBuilder report = new StringBuilder();
        report.append("======================================================\n");
        report.append("                  SYSTEM REPORT\n");
        report.append("======================================================\n");
        report.append("Total Customers   : ").append(totalUsers).append("\n");
        report.append("Total Organizers  : ").append(totalOrganizers).append("\n");
        report.append("Total Events      : ").append(totalEvents).append("\n");
        report.append("Total Bookings    : ").append(totalBookings).append("\n");
        report.append("Total Revenue     : ").append(totalRevenue).append("\n");

        return report.toString();
    }

    @Override
    public String generateEventReport(int eventId) throws EventNotFoundException, UserNotFoundException {

        Event event = eventService.getEventById(eventId);
        List<Booking> bookings = bookingService.getBookingsByEvent(eventId);

        int totalBookingsCount = 0;
        int seatsBooked = 0;
        double totalRevenue = 0;

        StringBuilder bookingLines = new StringBuilder();
        bookingLines.append(String.format("%-12s %-20s %-8s %-10s%n", "Booking ID", "Customer", "Seats", "Amount"));
        bookingLines.append("-----------------------------------------------------\n");

        for (Booking booking : bookings) {
            if (booking.getBookingStatus() != BookingStatus.CONFIRMED) {
                continue;
            }
            totalBookingsCount++;
            seatsBooked += booking.getSeatsBooked();
            totalRevenue += booking.getTotalAmount();

            String customerName = "Unknown";
            try {
                User customer = userService.getUserById(booking.getUserId());
                customerName = customer.getName();
            } catch (UserNotFoundException ignored) {

            }

            bookingLines.append(String.format("%-12d %-20s %-8d %-10.2f%n",
                    booking.getBookingId(), customerName, booking.getSeatsBooked(), booking.getTotalAmount()));
        }

        StringBuilder report = new StringBuilder();
        report.append("======================================================\n");
        report.append("                  EVENT REPORT\n");
        report.append("======================================================\n");
        report.append("Event              : ").append(event.getEventName()).append("\n");
        report.append("Total Seats        : ").append(event.getTotalSeats()).append("\n");
        report.append("Seats Booked       : ").append(seatsBooked).append("\n");
        report.append("Available Seats    : ").append(event.getAvailableSeats()).append("\n");
        report.append("Total Bookings     : ").append(totalBookingsCount).append("\n");
        report.append("Total Revenue      : ").append(totalRevenue).append("\n\n");
        report.append(bookingLines);

        return report.toString();
    }
}
