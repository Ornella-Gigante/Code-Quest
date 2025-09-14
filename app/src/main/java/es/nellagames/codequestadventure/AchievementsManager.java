package es.nellagames.codequestadventure;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

public class AchievementsManager {

    private static final String PREFS_NAME = "AchievementsPrefs";

    // Example achievement keys
    public static final String ACH_FIRST_LOGIN = "first_login";
    public static final String ACH_FIRST_CHALLENGE = "first_challenge_completed";
    public static final String ACH_THREE_CORRECT_STREAK = "three_correct_streak";
    public static final String ACH_FIVE_GAMES_PLAYED = "five_games_played";

    private SharedPreferences prefs;
    private Context context;

    public AchievementsManager(Context context) {
        this.context = context.getApplicationContext();
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public boolean isUnlocked(String achievementKey) {
        return prefs.getBoolean(achievementKey, false);
    }

    public void unlockAchievement(String achievementKey, String achievementName) {
        if (!isUnlocked(achievementKey)) {
            prefs.edit().putBoolean(achievementKey, true).apply();
            Toast.makeText(context, "Achievement unlocked! " + achievementName, Toast.LENGTH_SHORT).show();
        }
    }
}
