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
    private int totalChallenges;

    public GameSettings(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadSettings();
        totalChallenges = 10; // SIEMPRE 10 desafíos
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
        totalChallenges = 10; // SIEMPRE 10 desafíos independientemente de la dificultad
    }

    public DifficultyLevel getDifficulty() {
        return currentDifficulty;
    }

    public void setTotalChallenges(int total) {
        // Ignorar el parámetro, siempre usar 10
        totalChallenges = 10;
    }

    public int getTotalChallenges() {
        return 10; // SIEMPRE 10 desafíos
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
        return 10; // SIEMPRE 10 piezas
    }

    public int getTimeLimit() {
        switch (currentDifficulty) {
            case BEGINNER: return 0;
            case INTERMEDIATE: return 120;
            case ADVANCED: return 60;
            default: return 0;
        }
    }

    // Método para reiniciar el progreso del juego
    public void resetGameProgress(Context context) {
        SharedPreferences gamePrefs = context.getSharedPreferences("CodeQuest", Context.MODE_PRIVATE);
        gamePrefs.edit()
                .putInt("completed_pieces", 0)
                .putInt("current_challenge", 0)
                .putBoolean("game_completed", false)
                .apply();
    }
}