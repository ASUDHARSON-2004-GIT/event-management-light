package com.eventmanagement;

import com.eventmanagement.bootstrap.SeedDataLoader;
import com.eventmanagement.controller.AdminController;
import com.eventmanagement.controller.AuthController;
import com.eventmanagement.controller.CustomerController;
import com.eventmanagement.controller.OrganizerController;
import com.eventmanagement.collectionsDB.BookingRepository;
import com.eventmanagement.collectionsDB.CategoryRepository;
import com.eventmanagement.collectionsDB.EventRepository;
import com.eventmanagement.collectionsDB.UserRepository;
import com.eventmanagement.model.Role;
import com.eventmanagement.model.User;
import com.eventmanagement.service.AuthService;
import com.eventmanagement.service.AuthServiceImpl;
import com.eventmanagement.service.BookingService;
import com.eventmanagement.service.BookingServiceImpl;
import com.eventmanagement.service.CategoryService;
import com.eventmanagement.service.CategoryServiceImpl;
import com.eventmanagement.service.EventService;
import com.eventmanagement.service.EventServiceImpl;
import com.eventmanagement.service.ReportService;
import com.eventmanagement.service.ReportServiceImpl;
import com.eventmanagement.service.UserService;
import com.eventmanagement.service.UserServiceImpl;
import com.eventmanagement.util.ConsoleHelper;
import com.eventmanagement.util.IdGenerator;
import com.eventmanagement.util.Session;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        ConsoleHelper consoleHelper = new ConsoleHelper(scanner);
        Session session = new Session();

        UserRepository userRepository = new UserRepository();
        CategoryRepository categoryRepository = new CategoryRepository();
        EventRepository eventRepository = new EventRepository();
        BookingRepository bookingRepository = new BookingRepository();

        IdGenerator userIdGenerator = new IdGenerator(1);
        IdGenerator categoryIdGenerator = new IdGenerator(1);
        IdGenerator eventIdGenerator = new IdGenerator(1);
        IdGenerator bookingIdGenerator = new IdGenerator(1);

        AuthService authService = new AuthServiceImpl(userRepository, userIdGenerator);
        UserService userService = new UserServiceImpl(userRepository);
        CategoryService categoryService = new CategoryServiceImpl(categoryRepository, categoryIdGenerator);
        EventService eventService = new EventServiceImpl(eventRepository, eventIdGenerator, categoryService);
        BookingService bookingService = new BookingServiceImpl(bookingRepository, eventRepository, bookingIdGenerator);
        ReportService reportService = new ReportServiceImpl(userService, eventService, bookingService);

        AuthController authController = new AuthController(authService, consoleHelper, session);
        CustomerController customerController =
                new CustomerController(eventService, bookingService, userService, consoleHelper, session);
        OrganizerController organizerController =
                new OrganizerController(eventService, bookingService, categoryService, reportService, consoleHelper, session);
        AdminController adminController =
                new AdminController(userService, eventService, categoryService, bookingService, reportService, consoleHelper, session);

        SeedDataLoader.seedAll(categoryService, authService, eventService, bookingService);

        boolean applicationRunning = true;

        while (applicationRunning) {
            if (!session.isLoggedIn()) {
                applicationRunning = authController.showMainMenu();
                continue;
            }

            User currentUser = session.getCurrentUser();

            if (currentUser.getRole() == Role.CUSTOMER) {
                customerController.showMenu();
            } else if (currentUser.getRole() == Role.ORGANIZER) {
                organizerController.showMenu();
            } else if (currentUser.getRole() == Role.ADMIN) {
                adminController.showMenu();
            }
        }

        scanner.close();
    }
}