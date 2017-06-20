package com.janek.maowithfriends.model;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Parcel
public class Deck {
    List<Card> deck = new ArrayList<>();

    public Deck() {}

    public List<Card> getDeck() {
        return deck;
    }

    public static Deck createNewDeck() {
        Deck newDeck = new Deck();
        for (CardValue value : CardValue.values()) {
            for (Suit suit : Suit.values()) {
                Card newCard = new Card(value, suit);
                newDeck.deck.add(newCard);
            }
        }
        Collections.shuffle(newDeck.deck);
        return newDeck;
    }
}
