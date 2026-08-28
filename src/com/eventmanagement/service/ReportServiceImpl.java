package com.eventmanagement.service;

import com.eventmanagement.exception.EventNotFoundException;
import com.eventmanagement.exception.UserNotFoundException;
import com.eventmanagement.model.Booking;
import com.eventmanagement.model.BookingStatus;
import com.eventmanagement.model.Event;
import com.eventmanagement.model.Role;
import com.eventmanagement.model.EventStatus;
import com.eventmanagement.model.UserStatus;
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


    @Override
    public String generateUserReport(int userId) throws UserNotFoundException {
        User user = userService.getUserById(userId);
        if (user.getRole() != Role.CUSTOMER) {
            throw new UserNotFoundException("User with ID " + userId + " is not a Customer.");
        }

        List<Booking> userBookings = bookingService.getBookingsByUser(userId);
        int totalBookings = userBookings.size();
        int confirmedCount = 0;
        int cancelledCount = 0;

        for (Booking booking : userBookings) {
            if (booking.getBookingStatus() == BookingStatus.CONFIRMED) {
                confirmedCount++;
            } else if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
                cancelledCount++;
            }
        }

        StringBuilder report = new StringBuilder();
        report.append("======================================================\n");
        report.append("                 CUSTOMER REPORT\n");
        report.append("======================================================\n\n");
        report.append("Customer ID       : ").append(user.getUserId()).append("\n");
        report.append("Name              : ").append(user.getName()).append("\n");
        report.append("Email             : ").append(user.getEmail()).append("\n");
        report.append("Phone             : ").append(user.getPhone()).append("\n");
        report.append("Status            : ").append(user.getStatus()).append("\n\n");
        report.append("---------------- Booking Summary ----------------\n\n");
        report.append("Total Bookings    : ").append(totalBookings).append("\n");
        report.append("Confirmed         : ").append(confirmedCount).append("\n");
        report.append("Cancelled         : ").append(cancelledCount).append("\n\n");
        report.append("---------------- Booking History ----------------\n\n");
        report.append(String.format("%-12s %-24s %-7s %-9s %s%n", "Booking ID", "Event", "Seats", "Amount", "Status"));
        report.append("----------------------------------------------------------------\n");

        for (Booking booking : userBookings) {
            String eventName = "Unknown";
            try {
                Event event = eventService.getEventById(booking.getEventId());
                eventName = event.getEventName();
            } catch (EventNotFoundException ignored) {
            }
            report.append(String.format("%-12d %-24s %-7d %-9.2f %s%n",
                    booking.getBookingId(),
                    eventName.length() > 22 ? eventName.substring(0, 21) + "..." : eventName,
                    booking.getSeatsBooked(),
                    booking.getTotalAmount(),
                    booking.getBookingStatus()));
        }

        report.append("======================================================\n");
        return report.toString();
    }

    @Override
    public String generateOrganizerReport(int organizerId) throws UserNotFoundException {
        User organizer = userService.getUserById(organizerId);
        if (organizer.getRole() != Role.ORGANIZER) {
            throw new UserNotFoundException("User with ID " + organizerId + " is not an Organizer.");
        }

        List<Event> organizerEvents = eventService.getEventsByOrganizer(organizerId);
        int totalEvents = organizerEvents.size();
        int upcomingCount = 0;
        int completedCount = 0;
        int cancelledCount = 0;

        for (Event event : organizerEvents) {
            if (event.getStatus() == EventStatus.UPCOMING || event.getStatus() == EventStatus.ONGOING) {
                upcomingCount++;
            } else if (event.getStatus() == EventStatus.COMPLETED) {
                completedCount++;
            } else if (event.getStatus() == EventStatus.CANCELLED) {
                cancelledCount++;
            }
        }

        double totalRevenue = 0;
        for (Event event : organizerEvents) {
            List<Booking> bookings = bookingService.getBookingsByEvent(event.getEventId());
            for (Booking booking : bookings) {
                if (booking.getBookingStatus() == BookingStatus.CONFIRMED) {
                    totalRevenue += booking.getTotalAmount();
                }
            }
        }

        StringBuilder report = new StringBuilder();
        report.append("======================================================\n");
        report.append("                ORGANIZER REPORT\n");
        report.append("======================================================\n\n");
        report.append("Organizer ID      : ").append(organizer.getUserId()).append("\n");
        report.append("Name              : ").append(organizer.getName()).append("\n");
        report.append("Email             : ").append(organizer.getEmail()).append("\n");
        report.append("Phone             : ").append(organizer.getPhone()).append("\n");
        report.append("Status            : ").append(organizer.getStatus()).append("\n\n");
        report.append("---------------- Event Summary -----------------\n\n");
        report.append("Total Events      : ").append(totalEvents).append("\n");
        report.append("Upcoming Events   : ").append(upcomingCount).append("\n");
        report.append("Completed Events  : ").append(completedCount).append("\n");
        report.append("Cancelled Events  : ").append(cancelledCount).append("\n\n");
        report.append("---------------- Revenue Summary ---------------\n\n");
        report.append("Total Revenue     : ").append(String.format("%.2f", totalRevenue)).append("\n\n");
        report.append("---------------- Event Performance -------------\n\n");
        report.append(String.format("%-10s %-22s %-10s %-7s %s%n", "Event ID", "Event Name", "Bookings", "Seats", "Revenue"));
        report.append("----------------------------------------------------------------\n");

        for (Event event : organizerEvents) {
            List<Booking> bookings = bookingService.getBookingsByEvent(event.getEventId());
            int bookingsCount = 0;
            int seatsBooked = 0;
            double eventRevenue = 0;

            for (Booking booking : bookings) {
                if (booking.getBookingStatus() == BookingStatus.CONFIRMED) {
                    bookingsCount++;
                    seatsBooked += booking.getSeatsBooked();
                    eventRevenue += booking.getTotalAmount();
                }
            }

            String eventName = event.getEventName();
            report.append(String.format("%-10d %-22s %-10d %-7d %s%n",
                    event.getEventId(),
                    eventName.length() > 20 ? eventName.substring(0, 19) + "..." : eventName,
                    bookingsCount,
                    seatsBooked,
                    String.format("%.2f", eventRevenue)));
        }

        report.append("======================================================\n");
        return report.toString();
    }

    @Override
    public String generateOverallUserReport() {
        List<User> customers = userService.getAllUsersByRole(Role.CUSTOMER);

        StringBuilder report = new StringBuilder();
        report.append("======================================================\n");
        report.append("              OVERALL USER REPORT\n");
        report.append("======================================================\n");

        if (customers.isEmpty()) {
            report.append("No customers found.\n");
            report.append("======================================================\n");
            return report.toString();
        }

        report.append(String.format("%-13s %-20s %-25s %-15s %-14s %s%n",
                "Customer ID", "Name", "Email", "Events Booked", "Seats Booked", "Amount Paid"));
        report.append("--------------------------------------------------------------------------------------------------------\n");

        for (User user : customers) {
            List<Booking> userBookings = bookingService.getBookingsByUser(user.getUserId());
            int eventsBooked = 0;
            int seatsBooked = 0;
            double amountPaid = 0;

            for (Booking booking : userBookings) {
                if (booking.getBookingStatus() == BookingStatus.CONFIRMED) {
                    eventsBooked++;
                    seatsBooked += booking.getSeatsBooked();
                    amountPaid += booking.getTotalAmount();
                }
            }

            String name = user.getName();
            String email = user.getEmail();
            report.append(String.format("%-13d %-20s %-25s %-15d %-14d %s%n",
                    user.getUserId(),
                    name.length() > 18 ? name.substring(0, 17) + "..." : name,
                    email.length() > 23 ? email.substring(0, 22) + "..." : email,
                    eventsBooked,
                    seatsBooked,
                    String.format("%.2f", amountPaid)));
        }

        report.append("======================================================\n");
        return report.toString();
    }

    @Override
    public String generateOverallOrganizerReport() {
        List<User> organizers = userService.getAllUsersByRole(Role.ORGANIZER);

        StringBuilder report = new StringBuilder();
        report.append("======================================================\n");
        report.append("            OVERALL ORGANIZER REPORT\n");
        report.append("======================================================\n\n");

        if (organizers.isEmpty()) {
            report.append("No organizers found.\n");
            report.append("======================================================\n");
            return report.toString();
        }

        report.append(String.format("%-14s %-20s %-25s %-16s %-16s %s%n",
                "Organizer ID", "Name", "Email", "Events Created", "Cancelled", "Total Revenue"));
        report.append("--------------------------------------------------------------------------------------------------------\n");

        for (User user : organizers) {
            List<Event> organizerEvents = eventService.getEventsByOrganizer(user.getUserId());
            int eventsCreated = organizerEvents.size();
            int cancelled = 0;
            double totalRevenue = 0;

            for (Event event : organizerEvents) {
                if (event.getStatus() == EventStatus.CANCELLED) {
                    cancelled++;
                }
                List<Booking> bookings = bookingService.getBookingsByEvent(event.getEventId());
                for (Booking booking : bookings) {
                    if (booking.getBookingStatus() == BookingStatus.CONFIRMED) {
                        totalRevenue += booking.getTotalAmount();
                    }
                }
            }

            String name = user.getName();
            String email = user.getEmail();
            report.append(String.format("%-14d %-20s %-25s %-16d %-16d %s%n",
                    user.getUserId(),
                    name.length() > 18 ? name.substring(0, 17) + "..." : name,
                    email.length() > 23 ? email.substring(0, 22) + "..." : email,
                    eventsCreated,
                    cancelled,
                    String.format("%.2f", totalRevenue)));
        }

        report.append("======================================================\n");
        return report.toString();
    }
}
