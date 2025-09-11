package es.nellagames.codequestadventure;

public class LeaderboardEntry {
    public String playerName;
    public int score;
    public String avatarUrl;

    public LeaderboardEntry(String name, int score, String avatar) {
        this.playerName = name;
        this.score = score;
        this.avatarUrl = avatar;
    }
}
