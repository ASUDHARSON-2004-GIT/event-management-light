package com.eventmanagement.controller;

import com.eventmanagement.exception.AccessDeniedException;
import com.eventmanagement.exception.CategoryNotFoundException;
import com.eventmanagement.exception.EventNotFoundException;
import com.eventmanagement.exception.ValidationException;
import com.eventmanagement.model.Booking;
import com.eventmanagement.model.Category;
import com.eventmanagement.model.Event;
import com.eventmanagement.model.User;
import com.eventmanagement.service.BookingService;
import com.eventmanagement.service.CategoryService;
import com.eventmanagement.service.EventService;
import com.eventmanagement.service.ReportService;
import com.eventmanagement.util.ConsoleHelper;
import com.eventmanagement.util.DateUtil;
import com.eventmanagement.util.Session;
import com.eventmanagement.util.ValidationUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class OrganizerController {

    private final EventService eventService;
    private final BookingService bookingService;
    private final CategoryService categoryService;
    private final ReportService reportService;
    private final ConsoleHelper consoleHelper;
    private final Session session;

    public OrganizerController(EventService eventService, BookingService bookingService,
                               CategoryService categoryService, ReportService reportService,
                               ConsoleHelper consoleHelper, Session session) {
        this.eventService = eventService;
        this.bookingService = bookingService;
        this.categoryService = categoryService;
        this.reportService = reportService;
        this.consoleHelper = consoleHelper;
        this.session = session;
    }

    public void showMenu() {

        boolean stayInMenu = true;

        while (stayInMenu) {
            User currentUser = session.getCurrentUser();

            consoleHelper.printHeading("ORGANIZER DASHBOARD");
            consoleHelper.printLine("Logged in as: " + currentUser.getName());
            consoleHelper.printLine("1. Create Event");
            consoleHelper.printLine("2. View My Events");
            consoleHelper.printLine("3. Update Event");
            consoleHelper.printLine("4. Delete Event");
            consoleHelper.printLine("5. View Event Bookings");
            consoleHelper.printLine("6. View Event Revenue Report");
            consoleHelper.printLine("7. Logout");

            int choice = consoleHelper.readInt("Enter your choice: ");

            switch (choice) {
                case 1:
                    createEvent();
                    break;

                case 2:
                    viewMyEvents();
                    break;

                case 3:
                    updateEvent();
                    break;

                case 4:
                    deleteEvent();
                    break;

                case 5:
                    viewEventBookings();
                    break;

                case 6:
                    viewEventRevenueReport();
                    break;

                case 7:
                    session.logout();
                    consoleHelper.printLine("You have been logged out.");
                    stayInMenu = false;
                    break;

                default:
                    consoleHelper.printLine("Invalid choice, please try again.");
            }
        }
    }

    private void createEvent() {

        String eventName = consoleHelper.readLine("Enter event name: ");
        String description = consoleHelper.readLine("Enter description: ");

        showAvailableCategories();//Showing available categories can help user to choose the category

        int categoryId = consoleHelper.readInt("Enter category id: ");
        String venue = consoleHelper.readLine("Enter venue name: ");
        String address = consoleHelper.readLine("Enter address : ");
        String dateText = consoleHelper.readLine("Enter event date (dd-MM-yyyy): ");
        String timeText = consoleHelper.readLine("Enter event time (HH:mm): ");
        int totalSeats = consoleHelper.readInt("Enter total seats: ");
        double ticketPrice = consoleHelper.readDouble("Enter ticket price: ");

        try {
            LocalDate eventDate = DateUtil.parseDate(dateText);
            LocalTime eventTime = LocalTime.parse(timeText);

            Event event = eventService.addEvent(session.getCurrentUser().getUserId(), categoryId, eventName,
                    description, venue, address, eventDate, eventTime, totalSeats, ticketPrice);

            consoleHelper.printLine("Event created successfully with id " + event.getEventId());

        } catch (ValidationException | CategoryNotFoundException e) {
            consoleHelper.printLine("Could not create event: " + e.getMessage());
        } catch (Exception e) {
            consoleHelper.printLine("Please enter the date and time in the correct format.");
        }
    }

    private void showAvailableCategories() {
        List<Category> categories = categoryService.getAllCategories();
        consoleHelper.printLine("Available categories:");

        for (Category category : categories) {
            consoleHelper.printLine(category.toString());
        }
    }

    private void viewMyEvents() {
        List<Event> events = eventService.getEventsByOrganizer(session.getCurrentUser().getUserId());

        if (events.isEmpty()) {
            consoleHelper.printLine("You have not created any events yet.");
            return;
        }

        consoleHelper.printSeparator();
        System.out.printf("%-4s %-30s %-12s %-8s %-22s %-12s %-6s %-6s %-8s %-10s%n",
                "ID", "Event", "Date", "Time", "Venue", "City", "Total", "Avail", "Price", "Status");
        consoleHelper.printSeparator();

        for (Event event : events) {
            System.out.printf("%-4d %-30s %-12s %-8s %-22s %-6d %-6d %-8.2f %-10s%n",
                    event.getEventId(), event.getEventName(), DateUtil.formatDate(event.getEventDate()), event.getEventTime(),
                    event.getVenue(), event.getTotalSeats(), event.getAvailableSeats(),
                    event.getTicketPrice(), event.getStatus());
        }
        consoleHelper.printSeparator();
    }

    private void updateEvent() {
        int eventId = consoleHelper.readInt("Enter event id to update: ");

        try {
            Event existingEvent = eventService.getEventById(eventId);

            consoleHelper.printLine("Leave a field blank and press enter to keep its current value.");

            String eventName = consoleHelper.readLine(
                    "Enter new event name (" + existingEvent.getEventName() + "): ");
            if (ValidationUtil.isEmpty(eventName)) {
                eventName = existingEvent.getEventName();
            }

            String description = consoleHelper.readLine(
                    "Enter new description (" + existingEvent.getDescription() + "): ");
            if (ValidationUtil.isEmpty(description)) {
                description = existingEvent.getDescription();
            }

            String venue = consoleHelper.readLine(
                    "Enter new venue name (" + existingEvent.getVenue() + "): ");
            if (ValidationUtil.isEmpty(venue)) {
                venue = existingEvent.getVenue();
            }


            String address = consoleHelper.readLine(
                    "Enter new address (" + existingEvent.getAddress() + "): ");
            if (ValidationUtil.isEmpty(address)) {
                address = existingEvent.getAddress();
            }

            String dateText = consoleHelper.readLine(
                    "Enter new event date, dd-MM-yyyy (" + DateUtil.formatDate(existingEvent.getEventDate()) + "): ");
            LocalDate eventDate = ValidationUtil.isEmpty(dateText)
                    ? existingEvent.getEventDate() : DateUtil.parseDate(dateText);

            String timeText = consoleHelper.readLine(
                    "Enter new event time, HH:mm (" + existingEvent.getEventTime() + "): ");
            LocalTime eventTime = ValidationUtil.isEmpty(timeText)
                    ? existingEvent.getEventTime() : LocalTime.parse(timeText);

            String priceText = consoleHelper.readLine(
                    "Enter new ticket price (" + existingEvent.getTicketPrice() + "): ");
            double ticketPrice = ValidationUtil.isEmpty(priceText)
                    ? existingEvent.getTicketPrice() : Double.parseDouble(priceText);

            eventService.updateEvent(eventId, session.getCurrentUser().getUserId(), eventName, description,
                    venue, address, eventDate, eventTime, ticketPrice);

            consoleHelper.printLine("Event updated successfully.");

        } catch (EventNotFoundException | ValidationException | AccessDeniedException e) {
            consoleHelper.printLine("Could not update event: " + e.getMessage());
        } catch (Exception e) {
            consoleHelper.printLine("Please enter the date, time and price in the correct format.");
        }
    }

    private void deleteEvent() {
        int eventId = consoleHelper.readInt("Enter event id to delete: ");

        try {
            eventService.deleteEvent(eventId, session.getCurrentUser().getUserId());
            consoleHelper.printLine("Event deleted successfully.");
        } catch (EventNotFoundException | AccessDeniedException e) {
            consoleHelper.printLine("Could not delete event: " + e.getMessage());
        }
    }

    private void viewEventBookings() {
        int eventId = consoleHelper.readInt("Enter event id: ");

        try {
            Event event = eventService.getEventById(eventId);

            if (event.getOrganizerId() != session.getCurrentUser().getUserId()) {
                consoleHelper.printLine("You are not allowed to view bookings for an event you do not own.");
                return;
            }

            List<Booking> bookings = bookingService.getBookingsByEvent(eventId);

            if (bookings.isEmpty()) {
                consoleHelper.printLine("No bookings yet for this event.");
                return;
            }

            consoleHelper.printSeparator();
            System.out.printf("%-12s %-10s %-8s %-10s %-10s%n",
                    "Booking ID", "User ID", "Seats", "Amount", "Status");
            consoleHelper.printSeparator();

            for (Booking booking : bookings) {
                System.out.printf("%-12d %-10d %-8d %-10.2f %-10s%n",
                        booking.getBookingId(), booking.getUserId(), booking.getSeatsBooked(),
                        booking.getTotalAmount(), booking.getBookingStatus());
            }
            consoleHelper.printSeparator();

        } catch (EventNotFoundException e) {
            consoleHelper.printLine(e.getMessage());
        }
    }

    private void viewEventRevenueReport() {
        int eventId = consoleHelper.readInt("Enter event id: ");

        try {
            Event event = eventService.getEventById(eventId);

            if (event.getOrganizerId() != session.getCurrentUser().getUserId()) {
                consoleHelper.printLine("You are not allowed to view a report for an event you do not own.");
                return;
            }

            String report = reportService.generateEventReport(eventId);
            consoleHelper.printLine(report);

        } catch (EventNotFoundException e) {
            consoleHelper.printLine(e.getMessage());
        } catch (com.eventmanagement.exception.UserNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}