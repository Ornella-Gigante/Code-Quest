package es.nellagames.codequestadventure;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

public class AchievementsManager {

    // Achievement Keys (as before)
    public static final String ACH_FIRST_LOGIN = "first_login";
    public static final String ACH_FIRST_CHALLENGE = "first_challenge_completed";
    public static final String ACH_THREE_CORRECT_STREAK = "three_correct_streak";
    public static final String ACH_FIVE_GAMES_PLAYED = "five_games_played";
    public static final String ACH_SCORE_10 = "score_10_points";
    public static final String ACH_SCORE_30 = "score_30_points";
    public static final String ACH_SCORE_50 = "score_50_points";
    public static final String ACH_SCORE_100 = "score_100_points";
    public static final String ACH_SCORE_200 = "score_200_points";
    public static final String ACH_FIVE_PLAYED = "five_played";
    public static final String ACH_THREE_STREAK = "three_streak";
    // Add more as desired...

    private SharedPreferences prefs;
    private Context context;
    private long userId;

    // Pass the userId when constructing!
    public AchievementsManager(Context context, long userId) {
        this.context = context.getApplicationContext();
        this.userId = userId;
        String prefName = "AchievementsPrefs_" + userId;
        prefs = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
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

    public void checkScoreAchievements(int score) {
        if (score >= 10 && !isUnlocked(ACH_SCORE_10)) {
            unlockAchievement(ACH_SCORE_10, "Scored 10 Points");
        }
        if (score >= 30 && !isUnlocked(ACH_SCORE_30)) {
            unlockAchievement(ACH_SCORE_30, "Scored 30 Points");
        }
        if (score >= 50 && !isUnlocked(ACH_SCORE_50)) {
            unlockAchievement(ACH_SCORE_50, "Scored 50 Points");
        }
        if (score >= 100 && !isUnlocked(ACH_SCORE_100)) {
            unlockAchievement(ACH_SCORE_100, "Scored 100 Points");
        }
        if (score >= 200 && !isUnlocked(ACH_SCORE_200)) {
            unlockAchievement(ACH_SCORE_200, "Scored 200 Points");
        }
    }
}
