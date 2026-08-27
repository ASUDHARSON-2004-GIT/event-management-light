package com.eventmanagement.controller;

import com.eventmanagement.exception.BookingNotFoundException;
import com.eventmanagement.exception.EventManagementException;
import com.eventmanagement.exception.EventNotFoundException;
import com.eventmanagement.exception.InsufficientSeatsException;
import com.eventmanagement.exception.InvalidBookingException;
import com.eventmanagement.exception.UserNotFoundException;
import com.eventmanagement.exception.ValidationException;
import com.eventmanagement.model.Booking;
import com.eventmanagement.model.Event;
import com.eventmanagement.model.User;
import com.eventmanagement.service.BookingService;
import com.eventmanagement.service.EventService;
import com.eventmanagement.service.UserService;
import com.eventmanagement.util.ConsoleHelper;
import com.eventmanagement.util.Session;
import com.eventmanagement.util.ValidationUtil;

import java.time.LocalDate;
import java.util.List;

public class CustomerController {

    private final EventService eventService;
    private final BookingService bookingService;
    private final UserService userService;
    private final ConsoleHelper consoleHelper;
    private final Session session;

    public CustomerController(EventService eventService, BookingService bookingService, UserService userService,
                              ConsoleHelper consoleHelper, Session session) {
        this.eventService = eventService;
        this.bookingService = bookingService;
        this.userService = userService;
        this.consoleHelper = consoleHelper;
        this.session = session;
    }

    public void showMenu() {

        boolean stayInMenu = true;

        while (stayInMenu) {
            User currentUser = session.getCurrentUser();

            consoleHelper.printHeading("CUSTOMER DASHBOARD");
            consoleHelper.printLine("Logged in as: " + currentUser.getName());
            consoleHelper.printLine("1. View Available Events");
            consoleHelper.printLine("2. Search Events");
            consoleHelper.printLine("3. View Event Details");
            consoleHelper.printLine("4. Book Event");
            consoleHelper.printLine("5. View My Bookings");
            consoleHelper.printLine("6. Cancel Booking");
            consoleHelper.printLine("7. View Profile");
            consoleHelper.printLine("8. Update Profile");
            consoleHelper.printLine("9. Logout");

            int choice = consoleHelper.readInt("Enter your choice: ");

            switch (choice) {
                case 1:
                    viewAvailableEvents();
                    break;
                case 2:
                    searchEvents();
                    break;
                case 3:
                    viewEventDetails();
                    break;
                case 4:
                    bookEvent();
                    break;
                case 5:
                    viewMyBookings();
                    break;
                case 6:
                    cancelBooking();
                    break;
                case 7:
                    viewProfile();
                    break;
                case 8:
                    updateProfile();
                    break;
                case 9:
                    session.logout();
                    consoleHelper.printLine("You have been logged out.");
                    stayInMenu = false;
                    break;
                default:
                    consoleHelper.printLine("Invalid choice, please try again.");
            }
        }
    }

    private void viewAvailableEvents() {
        List<Event> events = eventService.getAllEvents();
        printEventTable(events);
    }
    private void printEventTable(List<Event> events) {
        if (events.isEmpty()) {
            consoleHelper.printLine("No events found.");
            return;
        }

        consoleHelper.printSeparator();
        System.out.printf("%-5s %-30s %-12s %-22s %-12s %-8s %-10s%n",
                "ID", "Event", "Date", "Venue", "City", "Avail", "Price");
        consoleHelper.printSeparator();
        for (Event event : events) {
            System.out.printf("%-5d %-30s %-12s %-22s %-12s %-8d %-10.2f%n",
                    event.getEventId(), event.getEventName(), event.getEventDate(),
                    event.getVenue(), event.getCity(), event.getAvailableSeats(), event.getTicketPrice());
        }
        consoleHelper.printSeparator();
    }

    private void searchEvents() {
        consoleHelper.printLine("Search by:");
        consoleHelper.printLine("1. Event Name");
        consoleHelper.printLine("2. Category ID");
        consoleHelper.printLine("3. Date");
        consoleHelper.printLine("4. Venue");

        int choice = consoleHelper.readInt("Enter your choice: ");
        List<Event> results;

        switch (choice) {
            case 1:
                String nameKeyword = consoleHelper.readLine("Enter event name keyword: ");
                results = eventService.searchByName(nameKeyword);
                break;
            case 2:
                int categoryId = consoleHelper.readInt("Enter category id: ");
                results = eventService.searchByCategory(categoryId);
                break;
            case 3:
                String dateText = consoleHelper.readLine("Enter date (yyyy-mm-dd): ");
                try {
                    LocalDate date = LocalDate.parse(dateText);
                    results = eventService.searchByDate(date);
                } catch (Exception e) {
                    consoleHelper.printLine("Invalid date format.");
                    return;
                }
                break;
            case 4:
                String venueKeyword = consoleHelper.readLine("Enter venue keyword: ");
                results = eventService.searchByVenue(venueKeyword);
                break;
            default:
                consoleHelper.printLine("Invalid choice.");
                return;
        }

        printEventTable(results);
    }

    private void viewEventDetails() {
        int eventId = consoleHelper.readInt("Enter event id: ");
        try {
            Event event = eventService.getEventById(eventId);
            consoleHelper.printSeparator();
            consoleHelper.printLine(event.toString());
            consoleHelper.printSeparator();
        } catch (EventNotFoundException e) {
            consoleHelper.printLine(e.getMessage());
        }
    }

    private void bookEvent() {
        int eventId = consoleHelper.readInt("Enter event id to book: ");
        int seats = consoleHelper.readInt("Enter number of seats: ");

        try {
            Event event = eventService.getEventById(eventId);

            // The seats are reserved right away so no one else can take them
            // while this customer is deciding whether to pay.
            Booking booking = bookingService.bookEvent(session.getCurrentUser().getUserId(), eventId, seats);

            consoleHelper.printHeading("BOOKING SUMMARY");
            consoleHelper.printLine("Booking ID    : " + booking.getBookingId());
            consoleHelper.printLine("Event         : " + event.getEventName());
            consoleHelper.printLine("Venue         : " + event.getVenue() + ", " + event.getCity());
            consoleHelper.printLine("Date and Time : " + event.getEventDate() + " " + event.getEventTime());
            consoleHelper.printLine("Seats Booked  : " + booking.getSeatsBooked());
            consoleHelper.printLine("Amount to Pay : " + booking.getTotalAmount());
            consoleHelper.printSeparator();

            String paymentChoice = consoleHelper.readLine("Proceed with payment? (yes/no): ");

            if (paymentChoice.equalsIgnoreCase("yes") || paymentChoice.equalsIgnoreCase("y")) {
                consoleHelper.printLine("Payment successful. Your booking is confirmed.");
            } else {
                bookingService.cancelBooking(booking.getBookingId(), session.getCurrentUser().getUserId());
                consoleHelper.printLine("Payment cancelled. Your booking has been cancelled and the seats released.");
            }

        } catch (EventNotFoundException | InsufficientSeatsException | InvalidBookingException e) {
            consoleHelper.printLine("Booking failed: " + e.getMessage());
        } catch (BookingNotFoundException e) {
            consoleHelper.printLine("Something went wrong while cancelling the unpaid booking.");
        }
    }

    private void viewMyBookings() {
        List<Booking> bookings = bookingService.getBookingsByUser(session.getCurrentUser().getUserId());

        if (bookings.isEmpty()) {
            consoleHelper.printLine("You have no bookings yet.");
            return;
        }

        consoleHelper.printSeparator();
        System.out.printf("%-12s %-20s %-8s %-10s %-10s%n",
                "Booking ID", "Event", "Seats", "Amount", "Status");
        consoleHelper.printSeparator();
        for (Booking booking : bookings) {
            String eventName;
            try {
                eventName = eventService.getEventById(booking.getEventId()).getEventName();
            } catch (EventNotFoundException e) {
                eventName = "(event no longer exists)";
            }
            System.out.printf("%-12d %-20s %-8d %-10.2f %-10s%n",
                    booking.getBookingId(), eventName, booking.getSeatsBooked(),
                    booking.getTotalAmount(), booking.getBookingStatus());
        }
        consoleHelper.printSeparator();
        consoleHelper.printLine("Use the Booking ID above with 'Cancel Booking' if you need to cancel one.");
    }

    private void cancelBooking() {
        int bookingId = consoleHelper.readInt("Enter booking id to cancel: ");

        try {
            bookingService.cancelBooking(bookingId, session.getCurrentUser().getUserId());
            consoleHelper.printLine("Booking cancelled and seats have been restored.");
        } catch (BookingNotFoundException | InvalidBookingException e) {
            consoleHelper.printLine("Cancellation failed: " + e.getMessage());
        }
    }

    private void viewProfile() {
        try {
            User user = userService.getUserById(session.getCurrentUser().getUserId());
            consoleHelper.printSeparator();
            consoleHelper.printLine(user.toString());
            consoleHelper.printSeparator();
        } catch (UserNotFoundException e) {
            consoleHelper.printLine(e.getMessage());
        }
    }

    private void updateProfile() {
        try {
            User currentUser = userService.getUserById(session.getCurrentUser().getUserId());

            consoleHelper.printLine("Leave a field blank and press enter to keep its current value.");

            String name = consoleHelper.readLine("Enter new name (" + currentUser.getName() + "): ");
            if (ValidationUtil.isEmpty(name)) {
                name = currentUser.getName();
            }

            String phone = consoleHelper.readLine("Enter new phone number (" + currentUser.getPhone() + "): ");
            if (ValidationUtil.isEmpty(phone)) {
                phone = currentUser.getPhone();
            }

            userService.updateProfile(session.getCurrentUser().getUserId(), name, phone);
            consoleHelper.printLine("Profile updated successfully.");
        } catch (UserNotFoundException | ValidationException e) {
            consoleHelper.printLine("Update failed: " + e.getMessage());
        }
    }
}