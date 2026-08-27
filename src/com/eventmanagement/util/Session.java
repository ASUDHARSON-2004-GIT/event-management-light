package com.eventmanagement.util;

import com.eventmanagement.model.User;

// A very small class that just remembers which user is
// currently logged in. Only one user can be logged in
// at a time since this is a single terminal console app.
public class Session {

    private User currentUser;

    public void login(User user) {
        this.currentUser = user;
    }

    public void logout() {
        this.currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}
