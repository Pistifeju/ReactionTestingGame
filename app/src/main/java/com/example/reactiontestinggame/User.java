package com.example.reactiontestinggame;
import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    private String username;
    private ArrayList<Float> games;
    private float gameRecord;

    public User() {}

    public User(String username) {
        this.username = username;
        this.games = new ArrayList<>();
        this.gameRecord = 0;
    }

    public User(String username, float gameRecord, double lastThreeGamesAvg) {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ArrayList<Float> getGames() {
        return games;
    }

    public void setGames(ArrayList<Float> games) {
        this.games = games;
    }

    public float getGameRecord() {
        return gameRecord;
    }

    public void setGameRecord(float gameRecord) {
        this.gameRecord = gameRecord;
    }

    public void addGame(float gameScore) {
        games.add(gameScore);
        if (gameScore > gameRecord) {
            gameRecord = gameScore;
        }
    }
}
