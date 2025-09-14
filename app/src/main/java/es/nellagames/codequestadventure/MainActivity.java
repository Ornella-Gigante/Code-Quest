package es.nellagames.codequestadventure;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button startButton, continueButton, difficultyButton, tutorialButton, resetProgressButton, leaderboardButton, logoutButton;
    private TextView progressText, difficultyText, scoreStreakText;
    private ProgressBar progressBar;
    private SharedPreferences prefs;
    private SoundManager soundManager;
    private GameSettings gameSettings;
    private Button achievementsButton;

    private long currentUserId = -1;
    private AchievementsManager achievementsManager;

    // User-specific preference keys
    private String keyScore() {
        return "score_user_" + currentUserId;
    }

    private String keyStreak() {
        return "streak_user_" + currentUserId;
    }

    private String keyCompletedPieces() {
        return "completed_pieces_user_" + currentUserId;
    }

    private String keyCurrentChallenge() {
        return "current_challenge_user_" + currentUserId;
    }

    private String keyGameCompleted() {
        return "game_completed_user_" + currentUserId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("CodeQuest", MODE_PRIVATE);
        currentUserId = prefs.getLong("current_user_id", -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "Error: No user logged in", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        achievementsManager = new AchievementsManager(this, currentUserId);

        initializeViews();
        initializeGame();
        setupListeners();
        updateUI();
    }

    private void initializeViews() {
        startButton = findViewById(R.id.startButton);
        continueButton = findViewById(R.id.continueButton);
        difficultyButton = findViewById(R.id.difficultyButton);
        tutorialButton = findViewById(R.id.tutorialButton);
        resetProgressButton = findViewById(R.id.resetProgressButton);
        progressText = findViewById(R.id.progressText);
        difficultyText = findViewById(R.id.difficultyText);
        progressBar = findViewById(R.id.progressBar);
        scoreStreakText = findViewById(R.id.scoreStreakText);
        leaderboardButton = findViewById(R.id.leaderboardButton);
        logoutButton = findViewById(R.id.logoutButton);
        achievementsButton = findViewById(R.id.achievementsButton);
    }

    private void initializeGame() {
        gameSettings = new GameSettings(this);
        soundManager = new SoundManager(this);
    }

    private void setupListeners() {
        startButton.setOnClickListener(v -> startGame());
        continueButton.setOnClickListener(v -> startGame());
        difficultyButton.setOnClickListener(v -> showDifficultySelection(false));
        tutorialButton.setOnClickListener(v -> startActivity(new Intent(this, TutorialActivity.class)));
        leaderboardButton.setOnClickListener(v -> startActivity(new Intent(this, LeaderboardActivity.class)));

        resetProgressButton.setOnClickListener(v -> resetUserProgress());

        achievementsButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AchievementsActivity.class));
        });

        logoutButton.setOnClickListener(v -> logoutUser());
    }

    private void resetUserProgress() {
        // Reset user-specific game progress
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(keyCurrentChallenge());
        editor.remove(keyCompletedPieces());
        editor.remove(keyScore());
        editor.remove(keyStreak());
        editor.remove(keyGameCompleted());
        editor.apply();

        // Reset achievements for this user
        if (achievementsManager != null) {
            achievementsManager.resetAllAchievements();
        }

        Toast.makeText(this, "Progress and achievements reset successfully", Toast.LENGTH_SHORT).show();
        updateUI();
    }

    private void logoutUser() {
        // Clear session data only (don't clear user progress)
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("current_user_id");
        editor.remove("current_username");
        editor.remove("current_avatar");
        editor.apply();

        stopService(new Intent(this, MusicService.class));
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void showDifficultySelection(boolean isNewGame) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_difficulty_selection);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        LinearLayout beginner = dialog.findViewById(R.id.beginnerLevel);
        LinearLayout intermediate = dialog.findViewById(R.id.intermediateLevel);
        LinearLayout advanced = dialog.findViewById(R.id.advancedLevel);
        Button cancel = dialog.findViewById(R.id.cancelButton);

        beginner.setOnClickListener(v -> {
            selectDifficulty(DifficultyLevel.BEGINNER, isNewGame);
            dialog.dismiss();
        });

        intermediate.setOnClickListener(v -> {
            selectDifficulty(DifficultyLevel.INTERMEDIATE, isNewGame);
            dialog.dismiss();
        });

        advanced.setOnClickListener(v -> {
            selectDifficulty(DifficultyLevel.ADVANCED, isNewGame);
            dialog.dismiss();
        });

        cancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void selectDifficulty(DifficultyLevel difficulty, boolean isNewGame) {
        gameSettings.setDifficulty(difficulty);

        if (isNewGame) {
            // Only reset user-specific progress when starting new game
            resetUserProgress();
            startGame();
        }

        updateUI();
    }

    private void updateUI() {
        DifficultyLevel difficulty = gameSettings.getDifficulty();
        int completed = prefs.getInt(keyCompletedPieces(), 0);
        int total = 10;
        int score = prefs.getInt(keyScore(), 0);
        int streak = prefs.getInt(keyStreak(), 0);

        progressBar.setMax(total);
        progressBar.setProgress(completed);

        progressText.setText("Progress: " + completed + " / " + total + " challenges completed");
        difficultyText.setText("Current Level: " + difficulty.getDisplayName());

        scoreStreakText.setText("Score: " + score + "   Streak: " + streak);

        continueButton.setVisibility(completed > 0 ? View.VISIBLE : View.GONE);
    }

    private void startGame() {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("difficulty", gameSettings.getDifficulty().name());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
        if (soundManager != null) {
            soundManager.resumeBackgroundMusic();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (soundManager != null) {
            soundManager.pauseBackgroundMusic();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundManager != null) {
            soundManager.release();
        }
    }
}