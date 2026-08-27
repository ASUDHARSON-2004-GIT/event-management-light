package com.eventmanagement.bootstrap;

import com.eventmanagement.exception.ValidationException;
import com.eventmanagement.model.Event;
import com.eventmanagement.model.Role;
import com.eventmanagement.model.User;
import com.eventmanagement.service.AuthService;
import com.eventmanagement.service.BookingService;
import com.eventmanagement.service.CategoryService;
import com.eventmanagement.service.EventService;

import java.time.LocalDate;
import java.time.LocalTime;

public class SeedDataLoader {

    public static void seedAll(CategoryService categoryService, AuthService authService,
                               EventService eventService, BookingService bookingService) {

        seedDefaultCategories(categoryService);
        seedDefaultAdmin(authService);
        seedSampleData(authService, eventService, bookingService);
    }

    private static void seedDefaultCategories(CategoryService categoryService) {
        try {
            categoryService.addCategory("Education", "Workshops, seminars and training sessions");
            categoryService.addCategory("Technology", "Tech talks, hackathons and conferences");
        } catch (ValidationException e) {
            // This should never happen for the fixed set of names above.
            System.out.println("Could not load default categories: " + e.getMessage());
        }
    }

    private static void seedDefaultAdmin(AuthService authService) {
        try {
            authService.register("SUDHARSON", "sudharson@gmail.com", "9999999999", "2004", Role.ADMIN);
        } catch (ValidationException e) {
            // If this ever fails, the application can still be used through registration.
            System.out.println("Could not create default admin account: " + e.getMessage());
        }
    }


    private static void seedSampleData(AuthService authService, EventService eventService,
                                       BookingService bookingService) {
        try {
            User arun = authService.register(
                    "Dharson", "dharson@gmail.com", "9876543210", "1234", Role.ORGANIZER);
            User meena = authService.register(
                    "Meena Raj", "meena@gmail.com", "9876500000", "1234", Role.ORGANIZER);
            User priya = authService.register(
                    "Sasi", "sasi@gmail.com", "9123456789", "1234", Role.CUSTOMER);
            User karthik = authService.register(
                    "Karthik Iyer", "karthik@gmail.com", "9123456780", "1234", Role.CUSTOMER);

            Event javaWorkshop = eventService.addEvent(arun.getUserId(), 2, "Java Full Stack Workshop",
                    "Hands on workshop covering core java, spring boot and react",
                    "FXEC Seminar Hall", "Chennai", "No 12, College Road",
                    LocalDate.now().plusDays(10), LocalTime.of(10, 0), 60, 500);

            Event aiSeminar = eventService.addEvent(arun.getUserId(), 2, "AI and Machine Learning Seminar",
                    "Seminar covering practical artificial intelligence and machine learning use cases",
                    "TOWZO", "Bengaluru", "Outer Ring Road, Sector 5",
                    LocalDate.now().plusDays(20), LocalTime.of(14, 0), 100, 750);

            Event placementTraining = eventService.addEvent(meena.getUserId(), 1, "Campus Placement Training",
                    "Aptitude, group discussion and interview preparation for final year students",
                    "Main Auditorium", "Chennai", "Anna University Campus",
                    LocalDate.now().plusDays(5), LocalTime.of(9, 30), 150, 0);

            // A few sample bookings so reports and booking lists are not empty either.
            bookingService.bookEvent(priya.getUserId(), javaWorkshop.getEventId(), 2);
            bookingService.bookEvent(karthik.getUserId(), javaWorkshop.getEventId(), 1);
            bookingService.bookEvent(priya.getUserId(), placementTraining.getEventId(), 1);

        } catch (Exception e) {
            System.out.println("Could not load sample data: " + e.getMessage());
        }
    }
}