package model;

import java.io.Serializable;
//Serializable: Marker Interface cho phép biến Object thành luồng byte để lưu xuống file nhị phân (.dat)
public class PlayerRecord implements Serializable {
    private String playerName;
    private int score;
    private String difficulty;

    public PlayerRecord(String playerName, int score, String difficulty) {
        this.playerName = playerName;
        this.score = score;
        this.difficulty = difficulty;
    }

    public String getPlayerName() { return playerName; }
    public int getScore() { return score; }
    public String getDifficulty() { return difficulty; }
}