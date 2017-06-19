package com.janek.maowithfriends.model;


public class User {
    String userId;
    String name;
    String imageUrl;

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
}
