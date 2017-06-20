package com.janek.maowithfriends.model;

import org.parceler.Parcel;

@Parcel
public class Card {
    CardValue value;
    Suit suit;

    public Card() {}

    public Card(CardValue value, Suit suit) {
        this.value = value;
        this.suit = suit;
    }

    public CardValue getValue() {
        return value;
    }

    public void setValue(CardValue value) {
        this.value = value;
    }

    public Suit getSuit() {
        return suit;
    }

    public void setSuit(Suit suit) {
        this.suit = suit;
    }
}
