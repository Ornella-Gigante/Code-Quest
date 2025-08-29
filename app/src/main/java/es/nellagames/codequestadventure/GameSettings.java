package es.nellagames.codequestadventure;



import android.content.Context;
import android.content.SharedPreferences;
import es.nellagames.codequestadventure.DifficultyLevel;

public class GameSettings {

    private static final String PREFS_NAME = "CodeQuestSettings";
    private static final String KEY_DIFFICULTY = "selected_difficulty";
    private static final String KEY_HINTS_ENABLED = "hints_enabled";
    private static final String KEY_SOUND_ENABLED = "sound_enabled";

    private SharedPreferences prefs;
    private DifficultyLevel currentDifficulty;
    private boolean hintsEnabled;
    private boolean soundEnabled;

    public GameSettings(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadSettings();
    }

    private void loadSettings() {
        String difficultyStr = prefs.getString(KEY_DIFFICULTY, DifficultyLevel.BEGINNER.name());
        currentDifficulty = DifficultyLevel.fromString(difficultyStr);
        hintsEnabled = prefs.getBoolean(KEY_HINTS_ENABLED, true);
        soundEnabled = prefs.getBoolean(KEY_SOUND_ENABLED, true);
    }

    public void setDifficulty(DifficultyLevel difficulty) {
        this.currentDifficulty = difficulty;
        prefs.edit().putString(KEY_DIFFICULTY, difficulty.name()).apply();
    }

    public DifficultyLevel getDifficulty() {
        return currentDifficulty;
    }

    public int getTotalChallenges() {
        return currentDifficulty.getTotalChallenges();
    }

    public void setHintsEnabled(boolean enabled) {
        this.hintsEnabled = enabled;
        prefs.edit().putBoolean(KEY_HINTS_ENABLED, enabled).apply();
    }

    public boolean areHintsEnabled() {
        return hintsEnabled;
    }

    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
        prefs.edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply();
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public int getPuzzlePieces() {
        // More pieces for higher difficulty
        switch (currentDifficulty) {
            case BEGINNER: return 6;
            case INTERMEDIATE: return 10;
            case ADVANCED: return 16;
            default: return 10;
        }
    }

    public int getTimeLimit() {
        // Time limit per challenge in seconds (0 = no limit)
        switch (currentDifficulty) {
            case BEGINNER: return 0; // No time limit
            case INTERMEDIATE: return 120; // 2 minutes
            case ADVANCED: return 60; // 1 minute
            default: return 0;
        }
    }
}
