package com.eventmanagement.service;

import com.eventmanagement.exception.UserNotFoundException;
import com.eventmanagement.exception.ValidationException;
import com.eventmanagement.model.Role;
import com.eventmanagement.model.User;

import java.util.List;

public interface UserService {

    void updateProfile(int userId, String name, String phone) throws UserNotFoundException, ValidationException;

    User getUserById(int userId) throws UserNotFoundException;

    List<User> getAllUsersByRole(Role role);

    List<User> getAllUsers();

    void setUserStatusActive(int userId) throws UserNotFoundException;

    void setUserStatusInactive(int userId) throws UserNotFoundException;

    void deleteUser(int userId) throws UserNotFoundException;

}
