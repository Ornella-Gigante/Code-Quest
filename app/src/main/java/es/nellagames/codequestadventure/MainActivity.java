package es.nellagames.codequestadventure;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Dialog;
import android.widget.LinearLayout;
import es.nellagames.codequestadventure.GameActivity;
import es.nellagames.codequestadventure.GameSettings;
import es.nellagames.codequestadventure.SoundManager;


public class MainActivity extends AppCompatActivity {

    private Button startButton, continueButton, difficultyButton;
    private TextView progressText, difficultyText;
    private ProgressBar progressBar;
    private Button backToMenuButton; // Added Back to Menu Button
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
        setupBackToMenuListener();  // Setup listener for Back to Menu Button
        updateUI();
    }

    private void initializeViews() {
        startButton = findViewById(R.id.startButton);
        continueButton = findViewById(R.id.continueButton);
        difficultyButton = findViewById(R.id.difficultyButton);
        progressText = findViewById(R.id.progressText);
        difficultyText = findViewById(R.id.difficultyText);
        progressBar = findViewById(R.id.progressBar);
        backToMenuButton = findViewById(R.id.backToMenuButton);  // Initialize Back to Menu Button
    }

    private void initializeGame() {
        gameSettings = new GameSettings(this);
        soundManager = new SoundManager(this);
        prefs = getSharedPreferences("CodeQuest", MODE_PRIVATE);

        // Start background music
        soundManager.startBackgroundMusic();
    }

    private void setupListeners() {
        startButton.setOnClickListener(v -> {
            soundManager.playSuccess();
            showDifficultySelection(true); // true = new game
        });

        continueButton.setOnClickListener(v -> {
            soundManager.playSuccess();
            startGame();
        });

        difficultyButton.setOnClickListener(v -> {
            soundManager.playSuccess();
            showDifficultySelection(false); // false = just changing difficulty
        });
    }

    private void setupBackToMenuListener() {
        if(backToMenuButton != null) {
            backToMenuButton.setOnClickListener(v -> {
                // Optional: play sound on click
                if(soundManager != null) {
                    soundManager.playSuccess();
                }
                // Start Main Activity - back to main menu
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }
    }

    private void showDifficultySelection(boolean isNewGame) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_difficulty_selection);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        LinearLayout beginnerLevel = dialog.findViewById(R.id.beginnerLevel);
        LinearLayout intermediateLevel = dialog.findViewById(R.id.intermediateLevel);
        LinearLayout advancedLevel = dialog.findViewById(R.id.advancedLevel);
        Button cancelButton = dialog.findViewById(R.id.cancelButton);

        beginnerLevel.setOnClickListener(v -> {
            selectDifficulty(DifficultyLevel.BEGINNER, isNewGame);
            dialog.dismiss();
        });

        intermediateLevel.setOnClickListener(v -> {
            selectDifficulty(DifficultyLevel.INTERMEDIATE, isNewGame);
            dialog.dismiss();
        });

        advancedLevel.setOnClickListener(v -> {
            selectDifficulty(DifficultyLevel.ADVANCED, isNewGame);
            dialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void selectDifficulty(DifficultyLevel difficulty, boolean isNewGame) {
        gameSettings.setDifficulty(difficulty);

        if (isNewGame) {
            // Reset progress for new game
            prefs.edit().clear().apply();
            startGame();
        }

        updateUI();
    }

    private void updateUI() {
        DifficultyLevel currentDifficulty = gameSettings.getDifficulty();
        int completedChallenges = prefs.getInt("completed_challenges", 0);
        int totalChallenges = gameSettings.getTotalChallenges();

        // Update progress bar
        progressBar.setMax(totalChallenges);
        progressBar.setProgress(completedChallenges);

        // Update progress text
        progressText.setText("Progress: " + completedChallenges + "/" + totalChallenges + " challenges completed");

        // Update difficulty display
        difficultyText.setText("Current Level: " + currentDifficulty.getDisplayName());

        // Show/hide continue button
        continueButton.setVisibility(completedChallenges > 0 ? View.VISIBLE : View.GONE);
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
    protected void onDestroy() {
        super.onDestroy();
        if (soundManager != null) {
            soundManager.release();
        }
    }
}
