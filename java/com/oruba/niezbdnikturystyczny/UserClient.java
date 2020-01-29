package com.oruba.niezbdnikturystyczny;

import android.app.Application;
import com.oruba.niezbdnikturystyczny.models.User;

/**
 * When the application process is started, this class is instantiated before any of the application's components.
 */
public class UserClient extends Application {

    private User user = null;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
