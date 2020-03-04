package com.dal.tourism;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public String name;
    public String mobile_number;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String mobile_number) {
        this.name = name;
        this.mobile_number = mobile_number;
    }

}