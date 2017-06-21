package com.janek.maowithfriends.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.janek.maowithfriends.Constants;

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
    int penalty = 0;
    boolean gameOver = false;

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

    public int getPenalty() {
        return penalty;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public void addPlayer(Player player) {
        this.players.put(player.getUserId(), player);
    }

    public Player currentTurnPlayer() {
        return getPlayers().get(currentPlayer);
    }

    public Card topDiscardCard() {
        return this.discard.get(this.discard.size() - 1);
    }

    public void playCard(String playerId, int cardIndex) {
        if (this.currentPlayer.equals(playerId)) {
            processCard(getPlayers().get(playerId).getHand().remove(cardIndex));
        }
    }

    private void processCard(Card card) {
        this.discard.add(card);
        if (card.getValue().getCardValue() == 14) {
            nextTurn();
            nextTurn();
        } else if (card.getValue().getCardValue() == 7) {
            this.penalty++;
            nextTurn();
        } else {
            nextTurn();
        }
    }

    public void playerDrawCard(String playerId) {
        if (this.currentPlayer.equals(playerId)) {
            if (this.penalty > 0) {
                for (int i = 0; i < this.penalty; i++) {
                    getPlayers().get(playerId).drawCard(drawCard());
                }
                this.penalty = 0;
            } else {
                getPlayers().get(playerId).drawCard(drawCard());
            }
            nextTurn();
        }
    }

    public void nextTurn() {
        this.currentPlayer = this.nextPlayerTurn.get(this.currentPlayer);
        updateGameState();
    }

    public void startGame() {
        this.deck = Game.createNewDeck();
        this.nextPlayerTurn = Game.calculateTurns(getPlayers());
        this.gameOver = false;
        dealCards();
        if (this.discard.size() == 0) {
            this.discard.add(drawCard());
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

    public boolean validCard(Card card) {
        Card topCard = topDiscardCard();
        if ((this.penalty > 0) && (topCard.getValue().getCardValue() == 7)) {
            return topCard.getValue().equals(card.getValue());
        } else {
            boolean matchingSuit = topDiscardCard().getSuit().equals(card.getSuit());
            boolean matchingValue = topDiscardCard().getValue().equals(card.getValue());
            return matchingSuit || matchingValue;
        }
    }

    public boolean validCardInHand(String playerId) {
        List<Card> playerHand = getPlayers().get(playerId).getHand();
        for (Card card : playerHand) {
            boolean valid = validCard(card);
            if (valid) {
                return true;
            }
        }
        return false;
    }

    public boolean roundOver() {
        for (Player player : getPlayers().values()) {
            if (player.getHand().size() == 0) {
                return true;
            }
        }
        return false;
    }

    public void endGame() {
        this.setGameOver(true);
        Map updates = new HashMap();
        updates.put(String.format(Constants.FIREBASE_GAME_REF, getGameId()), this);
        for (Player player : getPlayers().values()) {
            updates.put(String.format(Constants.FIREBASE_USER_GAME_REF, player.getUserId(), getGameId()), false);
        }
        FirebaseDatabase.getInstance().getReference().updateChildren(updates);
    }

    private void updateGameState() {
        DatabaseReference gameRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_GAMES_REF).child(getGameId());
        gameRef.setValue(this);
    }

}
