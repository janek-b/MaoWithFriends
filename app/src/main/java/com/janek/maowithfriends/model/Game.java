package com.janek.maowithfriends.model;

import org.parceler.Parcel;

import java.util.HashMap;
import java.util.Map;

@Parcel
public class Game {
    String gameId;
    String currentPlayer;
    Map<String, Player> players = new HashMap<>();
    Deck deck;

    public Game() {}

    public Game(String gameId, String currentPlayer) {
        this.gameId = gameId;
        this.currentPlayer = currentPlayer;
        this.deck = Deck.createNewDeck();
    }

    public String getGameId() {
        return gameId;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public Map<String, Player> getPlayers() {
        return players;
    }

    public Deck getDeck() {
        return deck;
    }

    public void addPlayer(Player player) {
        this.players.put(player.getUserId(), player);
    }

    public void dealCards() {
        for (Player player: players.values()) {
            for (int i = 0; i < 6; i ++) {
                player.drawCard(deck.getDeck().remove(0));
            }
        }
    }
}
