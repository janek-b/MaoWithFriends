package com.janek.maowithfriends.model;

import java.util.List;

public class Game {
    String gameId;
    String currentPlayer;
    List<User> players;

    public Game() {}

    public Game(String gameId, String currentPlayer, List<User> players) {
        this.gameId = gameId;
        this.currentPlayer = currentPlayer;
        this.players = players;
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
}
