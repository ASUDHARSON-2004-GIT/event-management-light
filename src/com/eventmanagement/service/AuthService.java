package com.eventmanagement.service;

import com.eventmanagement.exception.InvalidCredentialException;
import com.eventmanagement.exception.ValidationException;
import com.eventmanagement.model.Role;
import com.eventmanagement.model.User;

public interface AuthService {

    User register(String name, String email, String phone, String password, Role role)
            throws ValidationException;

    User login(String email, String password) throws InvalidCredentialException;
}
