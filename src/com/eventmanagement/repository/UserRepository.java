package com.eventmanagement.repository;

import com.eventmanagement.model.User;

import java.util.*;

public class UserRepository {

    private final Map<Integer, User> userStore = new HashMap<>();

    public User save(User user) {
        userStore.put(user.getUserId(), user);
        return user;
    }

    public Optional<User> findById(int userId) {
        return Optional.ofNullable(userStore.get(userId));
    }

    public Optional<User> findByEmail(String email) {
        return userStore.values().stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    public List<User> findAll() {
        return userStore.values().stream().sorted(Comparator.comparingInt(User::getUserId)).toList();
    }

    public List<User> findByRole(com.eventmanagement.model.Role role) {
        List<User> result = new ArrayList<>();

        for (User user : userStore.values().stream().sorted(Comparator.comparingInt(User::getUserId)).toList()) {
            if (user.getRole() == role) {
                result.add(user);
            }
        }

        return result;
    }

    public void deleteUser(int userId){
        userStore.remove(userId);
    }

}
