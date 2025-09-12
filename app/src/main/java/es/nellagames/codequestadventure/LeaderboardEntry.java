package es.nellagames.codequestadventure;

public class LeaderboardEntry {
    public String playerName;
    public int score;
    public String avatarUrl;

    public LeaderboardEntry(String playerName, int score, String avatarUrl) {
        this.playerName = playerName;
        this.score = score;
        this.avatarUrl = avatarUrl;
    }
}
