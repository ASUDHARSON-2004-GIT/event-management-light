package com.eventmanagement.controller;

import com.eventmanagement.exception.InvalidCredentialException;
import com.eventmanagement.exception.ValidationException;
import com.eventmanagement.model.Role;
import com.eventmanagement.model.User;
import com.eventmanagement.service.AuthService;
import com.eventmanagement.util.ConsoleHelper;
import com.eventmanagement.util.Session;

public class AuthController {

    private final AuthService authService;
    private final ConsoleHelper consoleHelper;
    private final Session session;

    public AuthController(AuthService authService, ConsoleHelper consoleHelper, Session session) {
        this.authService = authService;
        this.consoleHelper = consoleHelper;
        this.session = session;
    }

    public boolean showMainMenu() {

        consoleHelper.printHeading("EVENT MANAGEMENT SYSTEM");
        consoleHelper.printLine("1. Register (don't have an account)");
        consoleHelper.printLine("2. Login (already have an account)");
        consoleHelper.printLine("3. Exit");

        int choice = consoleHelper.readInt("Enter your choice: ");

        switch (choice) {
            case 1:
                handleRegister();
                return true;

            case 2:
                handleLogin();
                return true;

            case 3:
                consoleHelper.printLine("Thank you for using the Event Management System.");
                return false;

            default:
                consoleHelper.printLine("Invalid choice, please try again.");
                return true;
        }
    }

    private void handleRegister() {
        consoleHelper.printLine("Register as:");
        consoleHelper.printLine("1. Customer");
        consoleHelper.printLine("2. Organizer");

        int roleChoice = consoleHelper.readInt("Enter your choice: ");

        Role role;

        if (roleChoice == 1) {
            role = Role.CUSTOMER;
        } else if (roleChoice == 2) {
            role = Role.ORGANIZER;
        } else {
            consoleHelper.printLine("Invalid role choice.");
            return;
        }

        String name = consoleHelper.readLine("Enter your name: ");
        String email = consoleHelper.readLine("Enter your email: ");
        String phone = consoleHelper.readLine("Enter your phone number: ");
        String password = consoleHelper.readLine("Enter your password: ");

        try {
            User newUser = authService.register(name, email, phone, password, role);
            consoleHelper.printLine("Registration successful. Your user id is " + newUser.getUserId());
        } catch (ValidationException e) {
            consoleHelper.printLine("Registration failed: " + e.getMessage());
        }
    }

    private void handleLogin() {
        String email = consoleHelper.readLine("Enter your email: ");
        String password = consoleHelper.readLine("Enter your password: ");

        try {
            User user = authService.login(email, password);

            session.login(user);
            consoleHelper.printLine("Login successful. Welcome " + user.getName());
        } catch (InvalidCredentialException e) {
            consoleHelper.printLine("Login failed: " + e.getMessage());
        }

    }
}
