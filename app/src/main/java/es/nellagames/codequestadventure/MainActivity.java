package es.nellagames.codequestadventure;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import es.nellagames.codequestadventure.GameSettings;
import es.nellagames.codequestadventure.SoundManager;

public class MainActivity extends AppCompatActivity {

    private Button startButton, continueButton, difficultyButton, tutorialButton, resetProgressButton;
    private TextView progressText, difficultyText;
    private ProgressBar progressBar;
    private SharedPreferences prefs;
    private SoundManager soundManager;
    private GameSettings gameSettings;

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
    }

    private void initializeGame() {
        gameSettings = new GameSettings(this);
        soundManager = new SoundManager(this);
        prefs = getSharedPreferences("CodeQuest", MODE_PRIVATE);

        soundManager.startBackgroundMusic();
    }

    private void setupListeners() {
        startButton.setOnClickListener(v -> {
            // sonido removido
            startGame();
        });

        continueButton.setOnClickListener(v -> {
            // sonido removido
            startGame();
        });

        difficultyButton.setOnClickListener(v -> {
            // sonido removido
            showDifficultySelection(false);
        });

        tutorialButton.setOnClickListener(v -> {
            // sonido removido
            Intent intent = new Intent(MainActivity.this, TutorialActivity.class);
            startActivity(intent);
        });

        resetProgressButton.setOnClickListener(v -> {
            // sonido removido
            prefs.edit()
                    .putInt("current_challenge", 0)
                    .putInt("completed_pieces", 0)
                    .putBoolean("game_completed", false)
                    .apply();
            Toast.makeText(MainActivity.this, "Progress reset successfully", Toast.LENGTH_SHORT).show();
            updateUI();
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
            // Limpiar progreso para nuevo juego en la nueva dificultad
            prefs.edit().clear().apply();
            startGame();
        }

        updateUI();
    }

    private void updateUI() {
        DifficultyLevel difficulty = gameSettings.getDifficulty();
        int completed = prefs.getInt("completed_pieces", 0); // Cambio hecho aquí
        int total = 10;

        progressBar.setMax(total);
        progressBar.setProgress(completed);

        progressText.setText("Progress: " + completed + " / " + total + " challenges completed");
        difficultyText.setText("Current Level: " + difficulty.getDisplayName());

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
        if (soundManager != null) {
            soundManager.resumeBackgroundMusic();
        }
        updateUI();
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
