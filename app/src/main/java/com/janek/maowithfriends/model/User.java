package com.janek.maowithfriends.model;

import org.parceler.Parcel;

import java.util.HashMap;
import java.util.Map;

@Parcel
public class User {
    String userId;
    String name;
    String imageUrl;
    Map<String, Boolean> games = new HashMap<>();

    public User() {}

    public User(String userId, String name, String imageUrl) {
        this.userId = userId;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Map<String, Boolean> getGames() {
        return games;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object otherUser) {
        if (!(otherUser instanceof User)) {
            return false;
        } else {
            User newUser = (User) otherUser;
            return this.getUserId().equals(newUser.getUserId()) &&
                    this.getName().equals(newUser.getName()) &&
                    this.getImageUrl().equals(newUser.getImageUrl());
        }
    }
}
