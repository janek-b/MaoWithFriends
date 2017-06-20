package com.janek.maowithfriends.model;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.firebase.database.DataSnapshot;

import me.mattlogan.auto.value.firebase.annotation.FirebaseValue;

@AutoValue @FirebaseValue
public abstract class User implements Parcelable {
    public abstract String userId();
    public abstract String name();
    public abstract String imageUrl();

    public static User create(String userId, String name, String imageUrl) {
        return new AutoValue_User(userId, name, imageUrl);
    }

    public static User create(DataSnapshot dataSnapshot) {
        return dataSnapshot.getValue(AutoValue_User.FirebaseValue.class).toAutoValue();
    }

    public Object toFirebaseValue() {
        return new AutoValue_User.FirebaseValue(this);
    }

}

//public class User {
//    String userId;
//    String name;
//    String imageUrl;
//
//    public User() {}
//
//    public User(String userId, String name, String imageUrl) {
//        this.userId = userId;
//        this.name = name;
//        this.imageUrl = imageUrl;
//    }
//
//    public String getUserId() {
//        return userId;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public String getImageUrl() {
//        return imageUrl;
//    }
//
//    @Override
//    public String toString() {
//        return this.name;
//    }
//
//    @Override
//    public boolean equals(Object otherUser) {
//        if (!(otherUser instanceof User)) {
//            return false;
//        } else {
//            User newUser = (User) otherUser;
//            return this.getUserId().equals(newUser.getUserId()) &&
//                    this.getName().equals(newUser.getName()) &&
//                    this.getImageUrl().equals(newUser.getImageUrl());
//        }
//    }
//}
