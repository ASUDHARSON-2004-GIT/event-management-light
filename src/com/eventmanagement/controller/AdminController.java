package com.eventmanagement.controller;

import com.eventmanagement.exception.CategoryNotFoundException;
import com.eventmanagement.exception.EventNotFoundException;
import com.eventmanagement.exception.UserNotFoundException;
import com.eventmanagement.exception.ValidationException;
import com.eventmanagement.model.Booking;
import com.eventmanagement.model.Category;
import com.eventmanagement.model.Event;
import com.eventmanagement.model.EventStatus;
import com.eventmanagement.model.Role;
import com.eventmanagement.model.User;
import com.eventmanagement.service.BookingService;
import com.eventmanagement.service.CategoryService;
import com.eventmanagement.service.EventService;
import com.eventmanagement.service.ReportService;
import com.eventmanagement.service.UserService;
import com.eventmanagement.util.ConsoleHelper;
import com.eventmanagement.util.Session;
import com.eventmanagement.util.ValidationUtil;

import java.util.List;

public class AdminController {

    private final UserService userService;
    private final EventService eventService;
    private final CategoryService categoryService;
    private final BookingService bookingService;
    private final ReportService reportService;
    private final ConsoleHelper consoleHelper;
    private final Session session;

    public AdminController(UserService userService, EventService eventService, CategoryService categoryService,
                           BookingService bookingService, ReportService reportService,
                           ConsoleHelper consoleHelper, Session session) {
        this.userService = userService;
        this.eventService = eventService;
        this.categoryService = categoryService;
        this.bookingService = bookingService;
        this.reportService = reportService;
        this.consoleHelper = consoleHelper;
        this.session = session;
    }

    public void showMenu() {

        boolean stayInMenu = true;

        while (stayInMenu) {
            User currentUser = session.getCurrentUser();

            consoleHelper.printHeading("ADMIN DASHBOARD");
            consoleHelper.printLine("Logged in as: " + currentUser.getName());
            consoleHelper.printLine("1. Manage Users");
            consoleHelper.printLine("2. Manage Organizers");
            consoleHelper.printLine("3. Manage Events");
            consoleHelper.printLine("4. Manage Categories");
            consoleHelper.printLine("5. View All Bookings");
            consoleHelper.printLine("6. View System Report");
            consoleHelper.printLine("7. Logout");

            int choice = consoleHelper.readInt("Enter your choice: ");

            switch (choice) {
                case 1:
                    manageUsersByRole(Role.CUSTOMER);
                    break;
                case 2:
                    manageUsersByRole(Role.ORGANIZER);
                    break;
                case 3:
                    manageEvents();
                    break;
                case 4:
                    manageCategories();
                    break;
                case 5:
                    viewAllBookings();
                    break;
                case 6:
                    viewSystemReport();
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

    private void manageUsersByRole(Role role) {

        boolean stayInSubMenu = true;

        while (stayInSubMenu) {
            List<User> users = userService.getAllUsersByRole(role);

            consoleHelper.printHeading("MANAGE " + role + "S");
            if (users.isEmpty()) {
                consoleHelper.printLine("No users found in this role.");
            } else {
                consoleHelper.printSeparator();
                System.out.printf("%-6s %-18s %-25s %-12s %-10s%n",
                        "ID", "Name", "Email", "Phone", "Status");
                consoleHelper.printSeparator();
                for (User user : users) {
                    System.out.printf("%-6d %-18s %-25s %-12s %-10s%n",
                            user.getUserId(), user.getName(), user.getEmail(), user.getPhone(), user.getStatus());
                }
                consoleHelper.printSeparator();
            }

            consoleHelper.printLine("1. Activate a user");
            consoleHelper.printLine("2. Deactivate a user");
            consoleHelper.printLine("3. Back to admin menu");

            int choice = consoleHelper.readInt("Enter your choice: ");

            try {
                switch (choice) {
                    case 1:
                        int activateId = consoleHelper.readInt("Enter user id to activate: ");
                        userService.setUserStatusActive(activateId);
                        consoleHelper.printLine("User activated.");
                        break;
                    case 2:
                        int deactivateId = consoleHelper.readInt("Enter user id to deactivate: ");
                        userService.setUserStatusInactive(deactivateId);
                        consoleHelper.printLine("User deactivated.");
                        break;
                    case 3:
                        stayInSubMenu = false;
                        break;
                    default:
                        consoleHelper.printLine("Invalid choice, please try again.");
                }
            } catch (UserNotFoundException e) {
                consoleHelper.printLine(e.getMessage());
            }
        }
    }

    private void manageEvents() {

        boolean stayInSubMenu = true;

        while (stayInSubMenu) {
            List<Event> events = eventService.getAllEvents();

            consoleHelper.printHeading("MANAGE EVENTS");
            if (events.isEmpty()) {
                consoleHelper.printLine("No events found.");
            } else {
                consoleHelper.printSeparator();
                System.out.printf("%-4s %-30s %-12s %-12s %-22s %-12s %-6s %-6s %-8s %-10s%n",
                        "ID", "Event", "Organizer", "Date", "Venue", "City", "Total", "Avail", "Price", "Status");
                consoleHelper.printSeparator();
                for (Event event : events) {
                    System.out.printf("%-4d %-30s %-12d %-12s %-22s %-12s %-6d %-6d %-8.2f %-10s%n",
                            event.getEventId(), event.getEventName(), event.getOrganizerId(), event.getEventDate(),
                            event.getVenue(), event.getCity(), event.getTotalSeats(), event.getAvailableSeats(),
                            event.getTicketPrice(), event.getStatus());
                }
                consoleHelper.printSeparator();
            }

            consoleHelper.printLine("1. Cancel an event");
            consoleHelper.printLine("2. Delete an event");
            consoleHelper.printLine("3. Back to admin menu");

            int choice = consoleHelper.readInt("Enter your choice: ");

            try {
                switch (choice) {
                    case 1:
                        int cancelId = consoleHelper.readInt("Enter event id to cancel: ");
                        eventService.changeEventStatus(cancelId, EventStatus.CANCELLED);
                        consoleHelper.printLine("Event cancelled.");
                        break;
                    case 2:
                        int deleteId = consoleHelper.readInt("Enter event id to delete: ");
                        Event event = eventService.getEventById(deleteId);
                        eventService.deleteEvent(deleteId, event.getOrganizerId());
                        consoleHelper.printLine("Event deleted.");
                        break;
                    case 3:
                        stayInSubMenu = false;
                        break;
                    default:
                        consoleHelper.printLine("Invalid choice, please try again.");
                }
            } catch (EventNotFoundException e) {
                consoleHelper.printLine(e.getMessage());
            } catch (Exception e) {
                consoleHelper.printLine("Could not complete the action: " + e.getMessage());
            }
        }
    }

    private void manageCategories() {

        boolean stayInSubMenu = true;

        while (stayInSubMenu) {
            List<Category> categories = categoryService.getAllCategories();

            consoleHelper.printHeading("MANAGE CATEGORIES");
            if (categories.isEmpty()) {
                consoleHelper.printLine("No categories found.");
            } else {
                consoleHelper.printSeparator();
                System.out.printf("%-6s %-16s %-30s%n", "ID", "Name", "Description");
                consoleHelper.printSeparator();
                for (Category category : categories) {
                    System.out.printf("%-6d %-16s %-30s%n",
                            category.getCategoryId(), category.getCategoryName(), category.getDescription());
                }
                consoleHelper.printSeparator();
            }

            consoleHelper.printLine("1. Add category");
            consoleHelper.printLine("2. Update category");
            consoleHelper.printLine("3. Delete category");
            consoleHelper.printLine("4. Back to admin menu");

            int choice = consoleHelper.readInt("Enter your choice: ");

            try {
                switch (choice) {
                    case 1:
                        String name = consoleHelper.readLine("Enter category name: ");
                        String description = consoleHelper.readLine("Enter category description: ");
                        categoryService.addCategory(name, description);
                        consoleHelper.printLine("Category added.");
                        break;
                    case 2:
                        int updateId = consoleHelper.readInt("Enter category id to update: ");
                        Category existingCategory = categoryService.getCategoryById(updateId);

                        consoleHelper.printLine("Leave a field blank and press enter to keep its current value.");

                        String newName = consoleHelper.readLine(
                                "Enter new category name (" + existingCategory.getCategoryName() + "): ");
                        if (ValidationUtil.isEmpty(newName)) {
                            newName = existingCategory.getCategoryName();
                        }

                        String newDescription = consoleHelper.readLine(
                                "Enter new category description (" + existingCategory.getDescription() + "): ");
                        if (ValidationUtil.isEmpty(newDescription)) {
                            newDescription = existingCategory.getDescription();
                        }

                        categoryService.updateCategory(updateId, newName, newDescription);
                        consoleHelper.printLine("Category updated.");
                        break;
                    case 3:
                        int deleteId = consoleHelper.readInt("Enter category id to delete: ");
                        categoryService.deleteCategory(deleteId);
                        consoleHelper.printLine("Category deleted.");
                        break;
                    case 4:
                        stayInSubMenu = false;
                        break;
                    default:
                        consoleHelper.printLine("Invalid choice, please try again.");
                }
            } catch (CategoryNotFoundException | ValidationException e) {
                consoleHelper.printLine(e.getMessage());
            }
        }
    }

    private void viewAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();

        if (bookings.isEmpty()) {
            consoleHelper.printLine("No bookings have been made yet.");
            return;
        }

        consoleHelper.printSeparator();
        System.out.printf("%-12s %-8s %-8s %-8s %-10s %-10s%n",
                "Booking ID", "User ID", "Event ID", "Seats", "Amount", "Status");
        consoleHelper.printSeparator();
        for (Booking booking : bookings) {
            System.out.printf("%-12d %-8d %-8d %-8d %-10.2f %-10s%n",
                    booking.getBookingId(), booking.getUserId(), booking.getEventId(),
                    booking.getSeatsBooked(), booking.getTotalAmount(), booking.getBookingStatus());
        }
        consoleHelper.printSeparator();
    }

    private void viewSystemReport() {
        String report = reportService.generateSystemReport();
        consoleHelper.printLine(report);
    }
}