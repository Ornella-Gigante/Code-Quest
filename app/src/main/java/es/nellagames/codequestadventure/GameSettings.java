package es.nellagames.codequestadventure;

import android.content.Context;
import android.content.SharedPreferences;

public class GameSettings {

    private static final String PREFS_NAME = "CodeQuestSettings";
    private static final String KEY_DIFFICULTY = "selected_difficulty";
    private static final String KEY_HINTS_ENABLED = "hints_enabled";
    private static final String KEY_SOUND_ENABLED = "sound_enabled";

    private SharedPreferences prefs;
    private DifficultyLevel currentDifficulty;
    private boolean hintsEnabled;
    private boolean soundEnabled;
    private int totalChallenges;  // <-- campo para controlar total

    public GameSettings(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadSettings();
        totalChallenges = getDefaultTotalChallenges();
    }

    private void loadSettings() {
        String diffStr = prefs.getString(KEY_DIFFICULTY, DifficultyLevel.BEGINNER.name());
        currentDifficulty = DifficultyLevel.fromString(diffStr);
        hintsEnabled = prefs.getBoolean(KEY_HINTS_ENABLED, true);
        soundEnabled = prefs.getBoolean(KEY_SOUND_ENABLED, true);
    }

    public void setDifficulty(DifficultyLevel difficulty) {
        this.currentDifficulty = difficulty;
        prefs.edit().putString(KEY_DIFFICULTY, difficulty.name()).apply();
        totalChallenges = getDefaultTotalChallenges(); // reset total on difficulty change
    }

    public DifficultyLevel getDifficulty() {
        return currentDifficulty;
    }

    // Default total challenges based on difficulty
    private int getDefaultTotalChallenges() {
        switch (currentDifficulty) {
            case BEGINNER: return 6;
            case INTERMEDIATE: return 10;
            case ADVANCED: return 16;
            default: return 10;
        }
    }

    // Allow setting total challenges externally (e.g., to sync with puzzle pieces)
    public void setTotalChallenges(int total) {
        totalChallenges = total;
    }

    // Provide current total challenges
    public int getTotalChallenges() {
        return totalChallenges;
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

    // Return number of puzzle pieces based on difficulty
    public int getPuzzlePieces() {
        switch (currentDifficulty) {
            case BEGINNER: return 6;
            case INTERMEDIATE: return 10;
            case ADVANCED: return 16;
            default: return 10;
        }
    }

    public int getTimeLimit() {
        switch (currentDifficulty) {
            case BEGINNER: return 0;
            case INTERMEDIATE: return 120;
            case ADVANCED: return 60;
            default: return 0;
        }
    }
}
