package com.janek.maowithfriends.model;

import java.util.List;

public class Game {
    String gameId;
    String currentPlayer;
    List<User> players;
    Deck deck;

    public Game() {}

    public Game(String gameId, String currentPlayer, List<User> players) {
        this.gameId = gameId;
        this.currentPlayer = currentPlayer;
        this.players = players;
        this.deck = Deck.createNewDeck();
    }

    public String getGameId() {
        return gameId;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public List<User> getPlayers() {
        return players;
    }

    public Deck getDeck() {
        return deck;
    }
}
