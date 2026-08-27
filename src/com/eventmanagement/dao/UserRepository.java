package com.eventmanagement.dao;

import com.eventmanagement.model.User;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// This class stores users in memory using a map.
// The user id is the key so lookup by id is fast.
// Since there is no database yet, this class is the
// only place that knows how users are actually stored.
public class UserRepository {

    private final Map<Integer, User> userStore = new LinkedHashMap<>();

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
        return new ArrayList<>(userStore.values());
    }

    public List<User> findByRole(com.eventmanagement.model.Role role) {
        List<User> result = new ArrayList<>();
        for (User user : userStore.values()) {
            if (user.getRole() == role) {
                result.add(user);
            }
        }
        return result;
    }

    public int count() {
        return userStore.size();
    }
}
