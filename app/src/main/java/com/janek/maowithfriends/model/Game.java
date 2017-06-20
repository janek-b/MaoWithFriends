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
    List<Card> deck = new ArrayList<>();
    List<Card> discard = new ArrayList<>();

    public Game() {}

    public Game(String gameId, String currentPlayer) {
        this.gameId = gameId;
        this.currentPlayer = currentPlayer;
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

    public List<Card> getDiscard() {
        return discard;
    }

    public void addPlayer(Player player) {
        this.players.put(player.getUserId(), player);
    }

    public void nextTurn() {
        this.currentPlayer = this.nextPlayerTurn.get(this.currentPlayer);
    }

    public void startGame() {
        this.deck = Game.createNewDeck(); // create new deck

        this.nextPlayerTurn = Game.calculateTurns(getPlayers()); // calculate player turn order

        dealCards(); // distribute starting cards

        // start discard pile
        if (discard.size() == 0) {
            discard.add(drawCard());
        }
    }

    private static List<Card> createNewDeck() {
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

    private static Map<String, String> calculateTurns(Map<String, Player> players) {
        Map<String, String> turns = new HashMap<>();
        String[] keys =  players.keySet().toArray(new String[]{});
        for (int i = 0; i < keys.length; i++) {
            if (i == keys.length - 1) {
                turns.put(keys[i], keys[0]);
            } else {
                turns.put(keys[i], keys[i+1]);
            }
        }
        return turns;
    }

    private void dealCards() {
        for (Player player: players.values()) {
            for (int i = 0; i < 6; i ++) {
                player.drawCard(drawCard());
            }
        }
    }

    private Card drawCard() {
        return deck.remove(0);
    }

}
