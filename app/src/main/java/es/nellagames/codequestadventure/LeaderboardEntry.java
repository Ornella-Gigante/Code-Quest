package es.nellagames.codequestadventure;

import android.graphics.Bitmap;

public class LeaderboardEntry {
    public String playerName;
    public int score;
    public Bitmap avatar; // Ahora almacena la imagen real

    public LeaderboardEntry(String playerName, int score, Bitmap avatar) {
        this.playerName = playerName;
        this.score = score;
        this.avatar = avatar;
    }
}
