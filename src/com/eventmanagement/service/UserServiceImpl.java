package com.eventmanagement.service;

import com.eventmanagement.collectionsDB.UserRepository;
import com.eventmanagement.exception.UserNotFoundException;
import com.eventmanagement.exception.ValidationException;
import com.eventmanagement.model.Role;
import com.eventmanagement.model.User;
import com.eventmanagement.model.UserStatus;
import com.eventmanagement.util.ValidationUtil;

import java.util.List;

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void updateProfile(int userId, String name, String phone)
            throws UserNotFoundException, ValidationException {

        User user = getUserById(userId);

        if (ValidationUtil.isEmpty(name)) {
            throw new ValidationException("Name cannot be empty.");
        }

        if (!ValidationUtil.isValidPhone(phone)) {
            throw new ValidationException("Phone number must be exactly 10 digits.");
        }

        user.setName(name);
        user.setPhone(phone);
        userRepository.save(user);
    }

    @Override
    public User getUserById(int userId) throws UserNotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("No user found with id " + userId));
    }

    @Override
    public List<User> getAllUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void setUserStatusActive(int userId) throws UserNotFoundException {
        User user = getUserById(userId);
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
    }

    @Override
    public void setUserStatusInactive(int userId) throws UserNotFoundException {
        User user = getUserById(userId);
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
    }
}
