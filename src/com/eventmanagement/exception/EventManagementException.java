package com.eventmanagement.exception;

// This is the base exception for the whole application.
// Every other custom exception extends this class so we can
// catch all application level problems in one place if needed.
public class EventManagementException extends Exception {

    public EventManagementException(String message) {
        super(message);
    }
}
