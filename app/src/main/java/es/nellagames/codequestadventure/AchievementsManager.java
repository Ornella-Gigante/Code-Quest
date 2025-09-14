package es.nellagames.codequestadventure;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

public class AchievementsManager {

    // Achievement Keys
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

    private SharedPreferences prefs;
    private Context context;
    private long userId;

    public AchievementsManager(Context context, long userId) {
        this.context = context.getApplicationContext();
        this.userId = userId;
        // User-specific preferences file
        String prefName = "Achievements_User_" + userId;
        prefs = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
    }

    public boolean isUnlocked(String achievementKey) {
        return prefs.getBoolean(achievementKey, false);
    }

    public boolean unlockAchievement(String achievementKey, String achievementName) {
        if (!isUnlocked(achievementKey)) {
            prefs.edit().putBoolean(achievementKey, true).apply();
            Toast.makeText(context, "Achievement unlocked! " + achievementName, Toast.LENGTH_SHORT).show();
            return true; // Return true when achievement is actually unlocked
        }
        return false; // Return false if already unlocked
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

    /**
     * Resets all achievements for this specific user
     */
    public void resetAllAchievements() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear(); // Clear all achievements for this user
        editor.apply();

        Toast.makeText(context, "All achievements reset for user " + userId, Toast.LENGTH_SHORT).show();
    }

    /**
     * Gets the count of unlocked achievements for this user
     */
    public int getUnlockedCount() {
        int count = 0;
        String[] allAchievements = {
                ACH_FIRST_LOGIN, ACH_FIRST_CHALLENGE, ACH_THREE_CORRECT_STREAK,
                ACH_FIVE_GAMES_PLAYED, ACH_SCORE_10, ACH_SCORE_30, ACH_SCORE_50,
                ACH_SCORE_100, ACH_SCORE_200, ACH_FIVE_PLAYED, ACH_THREE_STREAK
        };

        for (String achievement : allAchievements) {
            if (isUnlocked(achievement)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Gets the total number of possible achievements
     */
    public int getTotalAchievements() {
        return 11; // Update this number when adding new achievements
    }

    /**
     * Gets the achievement progress percentage for this user
     */
    public float getProgressPercentage() {
        return (float) getUnlockedCount() / getTotalAchievements() * 100f;
    }

    /**
     * Checks if a specific achievement key exists
     */
    public boolean isValidAchievementKey(String achievementKey) {
        String[] validKeys = {
                ACH_FIRST_LOGIN, ACH_FIRST_CHALLENGE, ACH_THREE_CORRECT_STREAK,
                ACH_FIVE_GAMES_PLAYED, ACH_SCORE_10, ACH_SCORE_30, ACH_SCORE_50,
                ACH_SCORE_100, ACH_SCORE_200, ACH_FIVE_PLAYED, ACH_THREE_STREAK
        };

        for (String validKey : validKeys) {
            if (validKey.equals(achievementKey)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets achievement display name from key
     */
    public String getAchievementDisplayName(String achievementKey) {
        switch (achievementKey) {
            case ACH_FIRST_LOGIN:
                return "First Login";
            case ACH_FIRST_CHALLENGE:
                return "First Challenge Completed";
            case ACH_THREE_CORRECT_STREAK:
                return "Three in a Row!";
            case ACH_FIVE_GAMES_PLAYED:
                return "Five Games Played";
            case ACH_SCORE_10:
                return "Scored 10 Points";
            case ACH_SCORE_30:
                return "Scored 30 Points";
            case ACH_SCORE_50:
                return "Scored 50 Points";
            case ACH_SCORE_100:
                return "Scored 100 Points";
            case ACH_SCORE_200:
                return "Scored 200 Points";
            case ACH_FIVE_PLAYED:
                return "Five Played";
            case ACH_THREE_STREAK:
                return "Three Streak";
            default:
                return "Unknown Achievement";
        }
    }

    /**
     * Gets achievement description from key
     */
    public String getAchievementDescription(String achievementKey) {
        switch (achievementKey) {
            case ACH_FIRST_LOGIN:
                return "You logged in for the first time.";
            case ACH_FIRST_CHALLENGE:
                return "You completed your first challenge.";
            case ACH_THREE_CORRECT_STREAK:
                return "You got 3 correct answers consecutively.";
            case ACH_FIVE_GAMES_PLAYED:
                return "You played 5 games. Keep going!";
            case ACH_SCORE_10:
                return "You reached 10 points.";
            case ACH_SCORE_30:
                return "You reached 30 points.";
            case ACH_SCORE_50:
                return "You reached 50 points.";
            case ACH_SCORE_100:
                return "You reached 100 points!";
            case ACH_SCORE_200:
                return "You reached 200 points! Amazing!";
            case ACH_FIVE_PLAYED:
                return "You played five games.";
            case ACH_THREE_STREAK:
                return "You achieved a three-game streak.";
            default:
                return "No description available.";
        }
    }

    /**
     * Gets all unlocked achievements for this user
     */
    public String[] getUnlockedAchievements() {
        String[] allAchievements = {
                ACH_FIRST_LOGIN, ACH_FIRST_CHALLENGE, ACH_THREE_CORRECT_STREAK,
                ACH_FIVE_GAMES_PLAYED, ACH_SCORE_10, ACH_SCORE_30, ACH_SCORE_50,
                ACH_SCORE_100, ACH_SCORE_200, ACH_FIVE_PLAYED, ACH_THREE_STREAK
        };

        java.util.List<String> unlocked = new java.util.ArrayList<>();
        for (String achievement : allAchievements) {
            if (isUnlocked(achievement)) {
                unlocked.add(achievement);
            }
        }

        return unlocked.toArray(new String[0]);
    }
}