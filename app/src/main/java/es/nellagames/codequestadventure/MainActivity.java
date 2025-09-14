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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        prefs = getSharedPreferences("CodeQuest", MODE_PRIVATE);
        // Música global ya iniciada desde Login
    }

    private void setupListeners() {
        startButton.setOnClickListener(v -> startGame());
        continueButton.setOnClickListener(v -> startGame());
        difficultyButton.setOnClickListener(v -> showDifficultySelection(false));
        tutorialButton.setOnClickListener(v -> startActivity(new Intent(this, TutorialActivity.class)));
        leaderboardButton.setOnClickListener(v -> startActivity(new Intent(this, LeaderboardActivity.class)));

        resetProgressButton.setOnClickListener(v -> {
            prefs.edit()
                    .putInt("current_challenge", 0)
                    .putInt("completed_pieces", 0)
                    .putInt("score", 0)
                    .putInt("streak", 0)
                    .putBoolean("game_completed", false)
                    .apply();
            Toast.makeText(this, "Progress reset successfully", Toast.LENGTH_SHORT).show();
            updateUI();
        });

        achievementsButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AchievementsActivity.class));
        });

        logoutButton.setOnClickListener(v -> {
            // Borra solo datos relacionados a sesión del usuario sin afectar progreso
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
        });
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
            prefs.edit().clear().apply();
            startGame();
        }

        updateUI();
    }

    private void updateUI() {
        DifficultyLevel difficulty = gameSettings.getDifficulty();
        int completed = prefs.getInt("completed_pieces", 0);
        int total = 10;
        int score = prefs.getInt("score", 0);
        int streak = prefs.getInt("streak", 0);

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
