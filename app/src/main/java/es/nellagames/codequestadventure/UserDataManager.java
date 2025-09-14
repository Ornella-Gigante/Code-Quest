package es.nellagames.codequestadventure;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

public class UserDataManager {

    private Context context;
    private SharedPreferences prefs;

    public UserDataManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences("CodeQuest", Context.MODE_PRIVATE);
    }

    /**
     * Migrates old global data to user-specific data for a given user
     * This helps transition from the old system to the new user-specific system
     */
    public void migrateGlobalDataToUser(long userId) {
        if (userId <= 0) return;

        SharedPreferences.Editor editor = prefs.edit();

        // Check if we have old global data that needs migration
        int oldScore = prefs.getInt("score", -1);
        int oldStreak = prefs.getInt("streak", -1);
        int oldCompletedPieces = prefs.getInt("completed_pieces", -1);
        int oldCurrentChallenge = prefs.getInt("current_challenge", -1);
        boolean oldGameCompleted = prefs.getBoolean("game_completed", false);

        // Only migrate if old data exists and user data doesn't exist yet
        if (oldScore != -1 && !prefs.contains("score_user_" + userId)) {
            editor.putInt("score_user_" + userId, oldScore);
        }

        if (oldStreak != -1 && !prefs.contains("streak_user_" + userId)) {
            editor.putInt("streak_user_" + userId, oldStreak);
        }

        if (oldCompletedPieces != -1 && !prefs.contains("completed_pieces_user_" + userId)) {
            editor.putInt("completed_pieces_user_" + userId, oldCompletedPieces);
        }

        if (oldCurrentChallenge != -1 && !prefs.contains("current_challenge_user_" + userId)) {
            editor.putInt("current_challenge_user_" + userId, oldCurrentChallenge);
        }

        if (oldGameCompleted && !prefs.contains("game_completed_user_" + userId)) {
            editor.putBoolean("game_completed_user_" + userId, oldGameCompleted);
        }

        editor.apply();

        // After migration, remove old global keys to prevent confusion
        cleanupGlobalData();
    }

    /**
     * Resets all data for a specific user
     */
    public void resetUserData(long userId) {
        if (userId <= 0) return;

        SharedPreferences.Editor editor = prefs.edit();

        // Remove all user-specific game progress
        editor.remove("score_user_" + userId);
        editor.remove("streak_user_" + userId);
        editor.remove("completed_pieces_user_" + userId);
        editor.remove("current_challenge_user_" + userId);
        editor.remove("game_completed_user_" + userId);
        editor.remove("games_played_user_" + userId);
        editor.remove("correct_streak_user_" + userId);

        editor.apply();

        // Reset achievements for this user
        AchievementsManager achievementsManager = new AchievementsManager(context, userId);
        achievementsManager.resetAllAchievements();

        // Clear leaderboard scores for this user
        LeaderboardDbHelper dbHelper = new LeaderboardDbHelper(context);
        dbHelper.clearUserScores(userId);
        dbHelper.close();
    }

    /**
     * Completely removes a user and all their data
     */
    public void deleteUserCompletely(long userId) {
        if (userId <= 0) return;

        // Reset user data first
        resetUserData(userId);

        // Remove user from database
        LeaderboardDbHelper dbHelper = new LeaderboardDbHelper(context);
        dbHelper.deleteUser(userId);
        dbHelper.close();

        // Clear session if this is the current user
        long currentUserId = prefs.getLong("current_user_id", -1);
        if (currentUserId == userId) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove("current_user_id");
            editor.remove("current_username");
            editor.remove("current_avatar");
            editor.apply();
        }
    }

    /**
     * Cleans up old global data keys that are no longer used
     */
    private void cleanupGlobalData() {
        SharedPreferences.Editor editor = prefs.edit();

        // Remove old global keys
        editor.remove("score");
        editor.remove("streak");
        editor.remove("completed_pieces");
        editor.remove("current_challenge");
        editor.remove("game_completed");
        editor.remove("games_played");

        editor.apply();
    }

    /**
     * Gets user-specific data for display
     */
    public UserGameData getUserGameData(long userId) {
        if (userId <= 0) return new UserGameData();

        int score = prefs.getInt("score_user_" + userId, 0);
        int streak = prefs.getInt("streak_user_" + userId, 0);
        int completedPieces = prefs.getInt("completed_pieces_user_" + userId, 0);
        int currentChallenge = prefs.getInt("current_challenge_user_" + userId, 0);
        boolean gameCompleted = prefs.getBoolean("game_completed_user_" + userId, false);
        int gamesPlayed = prefs.getInt("games_played_user_" + userId, 0);

        return new UserGameData(score, streak, completedPieces, currentChallenge, gameCompleted, gamesPlayed);
    }

    /**
     * Validates user data integrity
     */
    public boolean validateUserData(long userId) {
        if (userId <= 0) return false;

        UserGameData data = getUserGameData(userId);

        // Check for impossible values
        if (data.completedPieces < 0 || data.completedPieces > 10) {
            return false;
        }

        if (data.currentChallenge < 0 || data.currentChallenge > 10) {
            return false;
        }

        if (data.score < 0) {
            return false;
        }

        if (data.streak < 0) {
            return false;
        }

        // Check logical consistency
        if (data.gameCompleted && data.completedPieces < 10) {
            return false;
        }

        return true;
    }

    /**
     * Repairs corrupted user data
     */
    public void repairUserData(long userId) {
        if (userId <= 0) return;

        UserGameData data = getUserGameData(userId);
        SharedPreferences.Editor editor = prefs.edit();

        // Fix impossible values
        if (data.completedPieces < 0 || data.completedPieces > 10) {
            editor.putInt("completed_pieces_user_" + userId, Math.max(0, Math.min(10, data.completedPieces)));
        }

        if (data.currentChallenge < 0 || data.currentChallenge > 10) {
            editor.putInt("current_challenge_user_" + userId, Math.max(0, Math.min(10, data.currentChallenge)));
        }

        if (data.score < 0) {
            editor.putInt("score_user_" + userId, 0);
        }

        if (data.streak < 0) {
            editor.putInt("streak_user_" + userId, 0);
        }

        // Fix logical inconsistencies
        if (data.gameCompleted && data.completedPieces < 10) {
            editor.putBoolean("game_completed_user_" + userId, false);
        }

        editor.apply();
    }

    /**
     * Data class to hold user game information
     */
    public static class UserGameData {
        public final int score;
        public final int streak;
        public final int completedPieces;
        public final int currentChallenge;
        public final boolean gameCompleted;
        public final int gamesPlayed;

        public UserGameData() {
            this(0, 0, 0, 0, false, 0);
        }

        public UserGameData(int score, int streak, int completedPieces,
                            int currentChallenge, boolean gameCompleted, int gamesPlayed) {
            this.score = score;
            this.streak = streak;
            this.completedPieces = completedPieces;
            this.currentChallenge = currentChallenge;
            this.gameCompleted = gameCompleted;
            this.gamesPlayed = gamesPlayed;
        }
    }
}