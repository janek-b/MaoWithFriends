package com.janek.maowithfriends.model;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Parcel
public class Game {
    String gameId;
    String currentPlayer;
    Map<String, Player> players = new HashMap<>();
    Map<String, String> nextPlayerTurn = new HashMap<>();
    List<Card> deck;

    public Game() {}

    public Game(String gameId, String currentPlayer) {
        this.gameId = gameId;
        this.currentPlayer = currentPlayer;
        this.deck = Game.createNewDeck();
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

    public List<Card> getDeck() {
        return deck;
    }

    public static List<Card> createNewDeck() {
        List<Card> newDeck = new ArrayList<>();
        for (CardValue value : CardValue.values()) {
            for (Suit suit : Suit.values()) {
                Card newCard = new Card(value, suit);
                newDeck.add(newCard);
            }
        }
        Collections.shuffle(newDeck);
        return newDeck;
    }

    public void addPlayer(Player player) {
        this.players.put(player.getUserId(), player);
    }

    public void dealCards() {
        setTurns();
        for (Player player: players.values()) {
            for (int i = 0; i < 6; i ++) {
                player.drawCard(deck.remove(0));
            }
        }
    }

    public void nextTurn() {
        currentPlayer = nextPlayerTurn.get(currentPlayer);
    }

    private void setTurns() {
        Map<String, String> nextTurn = new HashMap<>();
        List<String> keys = new ArrayList<>(getPlayers().keySet());
        for (int i = 0; i < keys.size(); i++) {
            if (i == keys.size() - 1) {
                nextTurn.put(keys.get(i), keys.get(0));
            } else {
                nextTurn.put(keys.get(i), keys.get(i+1));
            }
        }
        nextPlayerTurn = nextTurn;
    }
}
