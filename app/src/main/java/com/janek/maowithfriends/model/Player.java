package com.janek.maowithfriends.model;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Player {
    String userId;
    String name;
    String imageUrl;
    List<Card> hand = new ArrayList<>();

    public Player() {}

    public Player(String userId, String name, String imageUrl) {
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

    public List<Card> getHand() {
        return hand;
    }

    public void drawCard(Card card) {
        this.hand.add(card);
    }
}
