package com.eventmanagement.service;

import com.eventmanagement.dao.UserRepository;
import com.eventmanagement.exception.InvalidCredentialException;
import com.eventmanagement.exception.ValidationException;
import com.eventmanagement.model.Role;
import com.eventmanagement.model.User;
import com.eventmanagement.model.UserStatus;
import com.eventmanagement.util.IdGenerator;
import com.eventmanagement.util.PasswordUtil;
import com.eventmanagement.util.ValidationUtil;

public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final IdGenerator userIdGenerator;

    public AuthServiceImpl(UserRepository userRepository, IdGenerator userIdGenerator) {
        this.userRepository = userRepository;
        this.userIdGenerator = userIdGenerator;
    }

    @Override
    public User register(String name, String email, String phone, String password, Role role)
            throws ValidationException {

        if (ValidationUtil.isEmpty(name)) {
            throw new ValidationException("Name cannot be empty.");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            throw new ValidationException("Please enter a valid email address.");
        }
        if (!ValidationUtil.isValidPhone(phone)) {
            throw new ValidationException("Phone number must be exactly 10 digits.");
        }
        if (ValidationUtil.isEmpty(password)) {
            throw new ValidationException("Password cannot be empty.");
        }
        if (userRepository.existsByEmail(email)) {
            throw new ValidationException("An account with this email already exists.");
        }

        int newUserId = userIdGenerator.nextId();
        String hashedPassword = PasswordUtil.hashPassword(password);

        User newUser = new User(newUserId, name, email, phone, hashedPassword, role);
        userRepository.save(newUser);
        return newUser;
    }

    @Override
    public User login(String email, String password) throws InvalidCredentialException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialException("Email or password is incorrect."));

        if (!PasswordUtil.matches(password, user.getPassword())) {
            throw new InvalidCredentialException("Email or password is incorrect.");
        }

        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new InvalidCredentialException("This account has been deactivated. Please contact admin.");
        }

        return user;
    }
}
