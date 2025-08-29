package es.nellagames.codequestadventure;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import es.nellagames.codequestadventure.Challenge;
import es.nellagames.codequestadventure.DifficultyLevel;
import es.nellagames.codequestadventure.GameLogic;
import es.nellagames.codequestadventure.GameSettings;
import es.nellagames.codequestadventure.SoundManager;
import es.nellagames.codequestadventure.CodeChallengeView;
import es.nellagames.codequestadventure.HiddenPictureView;

public class GameActivity extends AppCompatActivity {

    // UI Components
    private TextView challengeTitle, challengeDescription;
    private CodeChallengeView challengeView;
    private HiddenPictureView pictureView;
    private Button submitButton, nextButton;

    // Game Logic Components
    private GameLogic gameLogic;
    private SoundManager soundManager;
    private GameSettings gameSettings;
    private SharedPreferences prefs;

    // Game State
    private Challenge currentChallenge;
    private int currentChallengeIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        initializeViews();
        setupGame();
        loadCurrentChallenge();
    }

    private void initializeViews() {
        challengeTitle = findViewById(R.id.challengeTitle);
        challengeDescription = findViewById(R.id.challengeDescription);
        challengeView = findViewById(R.id.challengeView);
        pictureView = findViewById(R.id.pictureView);
        submitButton = findViewById(R.id.submitButton);
        nextButton = findViewById(R.id.nextButton);

        // Initially hide next button
        nextButton.setVisibility(View.GONE);

        // Setup button listeners
        submitButton.setOnClickListener(v -> checkAnswer());
        nextButton.setOnClickListener(v -> nextChallenge());
    }

    private void setupGame() {
        // Initialize game settings
        gameSettings = new GameSettings(this);

        // Get difficulty from intent if passed from MainActivity
        String difficultyStr = getIntent().getStringExtra("difficulty");
        if (difficultyStr != null) {
            DifficultyLevel difficulty = DifficultyLevel.fromString(difficultyStr);
            gameSettings.setDifficulty(difficulty);
        }

        // Initialize game components
        gameLogic = new GameLogic(gameSettings);
        soundManager = new SoundManager(this);
        prefs = getSharedPreferences("CodeQuest", MODE_PRIVATE);

        // Load current progress
        currentChallengeIndex = prefs.getInt("current_challenge", 0);

        // Initialize picture view with current progress
        int completedChallenges = prefs.getInt("completed_challenges", 0);
        pictureView.revealPieces(completedChallenges);
    }

    private void loadCurrentChallenge() {
        currentChallenge = gameLogic.getChallenge(currentChallengeIndex);

        if (currentChallenge != null) {
            challengeTitle.setText(currentChallenge.getTitle());
            challengeDescription.setText(currentChallenge.getDescription());
            challengeView.setChallenge(currentChallenge);

            // Update UI for current challenge state
            updateChallengeUI();
        }
    }

    private void updateChallengeUI() {
        // Reset button states
        submitButton.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.GONE);

        // Update progress text if needed
        int totalChallenges = gameSettings.getTotalChallenges();
        challengeTitle.setText(challengeTitle.getText() + " (" + (currentChallengeIndex + 1) + "/" + totalChallenges + ")");
    }

    private void checkAnswer() {
        String userAnswer = challengeView.getUserAnswer();

        if (currentChallenge != null && currentChallenge.isCorrect(userAnswer)) {
            // ✅ Correct answer!
            soundManager.playSuccess();

            // Update progress
            int completedChallenges = prefs.getInt("completed_challenges", 0);
            completedChallenges++;

            prefs.edit()
                    .putInt("completed_challenges", completedChallenges)
                    .putInt("current_challenge", currentChallengeIndex + 1)
                    .apply();

            // Reveal next picture piece
            pictureView.revealNextPiece();

            // Get current image info for enhanced feedback
            String imageName = pictureView.getCurrentImageName();
            String imageDescription = pictureView.getCurrentImageDescription();

            // Show success message with image information
            String successMessage = "Correct! 🎉\n" + currentChallenge.getExplanation();

            if (pictureView.isComplete()) {
                successMessage += "\n\n🖼️ Image Complete: " + imageName + "\n" + imageDescription;
                // Load new random image for next set of challenges
                pictureView.newRandomImage();
            } else {
                successMessage += "\n\n🧩 Piece revealed! Keep going to see: " + imageName;
            }

            Toast.makeText(this, successMessage, Toast.LENGTH_LONG).show();

            // Update UI
            submitButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.VISIBLE);

            // Check if all challenges completed for current difficulty
            int totalChallenges = gameSettings.getTotalChallenges();
            if (completedChallenges >= totalChallenges) {
                nextButton.setText("🏆 Complete Adventure!");
            } else {
                nextButton.setText("➡️ Next Challenge");
            }

        } else {
            // ❌ Incorrect answer
            soundManager.playError();

            // Show hint based on current challenge
            String hintMessage = "Try again! 🤔";
            if (currentChallenge != null && currentChallenge.getHint() != null) {
                hintMessage += "\n💡 Hint: " + currentChallenge.getHint();
            }

            Toast.makeText(this, hintMessage, Toast.LENGTH_LONG).show();
        }
    }

    private void nextChallenge() {
        currentChallengeIndex++;
        int totalChallenges = gameSettings.getTotalChallenges();

        if (currentChallengeIndex >= totalChallenges) {
            // 🏆 Game completed for current difficulty!
            showCompletionScreen();
        } else {
            // Continue to next challenge
            submitButton.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.GONE);
            loadCurrentChallenge();
        }
    }

    private void showCompletionScreen() {
        // Play victory sound
        soundManager.playVictory();

        // Get current difficulty and image info for completion message
        DifficultyLevel currentDifficulty = gameSettings.getDifficulty();
        String imageName = pictureView.getCurrentImageName();

        String completionMessage = "🎉 Congratulations! 🎉\n\n" +
                "You've completed " + currentDifficulty.getDisplayName() + " level!\n\n" +
                "🖼️ Final Image: " + imageName + "\n" +
                pictureView.getCurrentImageDescription();

        Toast.makeText(this, completionMessage, Toast.LENGTH_LONG).show();

        // Optional: play game over sound after victory
        new android.os.Handler().postDelayed(() -> {
            soundManager.playGameOver();
        }, 3000); // 3 seconds delay for longer message

        // Save completion achievement
        prefs.edit()
                .putBoolean("completed_" + currentDifficulty.name().toLowerCase(), true)
                .putLong("completion_time_" + currentDifficulty.name().toLowerCase(), System.currentTimeMillis())
                .apply();

        // Return to main menu after delay
        new android.os.Handler().postDelayed(this::finish, 5000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Music continues automatically because it's a service
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save current state when pausing
        if (prefs != null) {
            prefs.edit()
                    .putInt("current_challenge", currentChallengeIndex)
                    .apply();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Release sound resources
        if (soundManager != null) {
            soundManager.release();
        }

        // DO NOT stop background music here so it continues in MainActivity
    }

    // Public methods for external access
    public int getCurrentChallengeIndex() {
        return currentChallengeIndex;
    }

    public DifficultyLevel getCurrentDifficulty() {
        return gameSettings != null ? gameSettings.getDifficulty() : DifficultyLevel.BEGINNER;
    }

    public int getTotalChallenges() {
        return gameSettings != null ? gameSettings.getTotalChallenges() : 10;
    }

    public String getCurrentImageName() {
        return pictureView != null ? pictureView.getCurrentImageName() : "Unknown";
    }

    public boolean isPictureComplete() {
        return pictureView != null && pictureView.isComplete();
    }

    public int getRevealedPieces() {
        return pictureView != null ? pictureView.getRevealedPieces() : 0;
    }

    // Method to reset game progress (for testing or new game)
    public void resetProgress() {
        if (prefs != null) {
            prefs.edit()
                    .putInt("current_challenge", 0)
                    .putInt("completed_challenges", 0)
                    .apply();
        }

        if (pictureView != null) {
            pictureView.resetPuzzle();
            pictureView.newRandomImage();
        }

        currentChallengeIndex = 0;
        loadCurrentChallenge();
    }
}
