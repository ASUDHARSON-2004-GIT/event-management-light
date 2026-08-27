package com.eventmanagement.exception;

public class UserNotFoundException extends EventManagementException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
