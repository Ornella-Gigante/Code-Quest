package es.nellagames.codequestadventure;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    private TextView challengeTitle, challengeDescription;
    private CodeChallengeView challengeView;
    private HiddenPictureView pictureView;
    private Button submitButton, nextButton, backToMenuButton;

    private GameLogic gameLogic;
    private SoundManager soundManager;
    private GameSettings gameSettings;
    private SharedPreferences prefs;

    private Challenge currentChallenge;
    private int currentChallengeIndex = 0;
    private int completedPiecesCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        initializeViews();
        setupGame();
        loadCurrentChallenge();
        setupBackMenuListener();
    }

    private void initializeViews() {
        challengeTitle = findViewById(R.id.challengeTitle);
        challengeDescription = findViewById(R.id.challengeDescription);
        challengeView = findViewById(R.id.challengeView);
        pictureView = findViewById(R.id.pictureView);
        submitButton = findViewById(R.id.submitButton);
        nextButton = findViewById(R.id.nextButton);
        backToMenuButton = findViewById(R.id.backToMenuButton);

        nextButton.setVisibility(Button.GONE);
        submitButton.setOnClickListener(v -> checkAnswer());
        nextButton.setOnClickListener(v -> nextChallenge());
    }

    private void setupBackMenuListener() {
        if (backToMenuButton != null) {
            backToMenuButton.setOnClickListener(v -> {
                if (soundManager != null) soundManager.playSuccess();
                Intent intent = new Intent(GameActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }
    }

    private void setupGame() {
        gameSettings = new GameSettings(this);
        String difficultyStr = getIntent().getStringExtra("difficulty");
        if (difficultyStr != null) {
            DifficultyLevel difficulty = DifficultyLevel.fromString(difficultyStr);
            gameSettings.setDifficulty(difficulty);
        }

        gameLogic = new GameLogic(gameSettings);
        soundManager = new SoundManager(this);
        prefs = getSharedPreferences("CodeQuest", MODE_PRIVATE);

        currentChallengeIndex = prefs.getInt("current_challenge", 0);
        completedPiecesCount = prefs.getInt("completed_challenges", 0);
        pictureView.revealPieces(completedPiecesCount);
    }

    private void loadCurrentChallenge() {
        currentChallenge = gameLogic.getChallenge(currentChallengeIndex);
        if (currentChallenge != null) {
            challengeTitle.setText(currentChallenge.getTitle());
            challengeDescription.setText(currentChallenge.getDescription());
            challengeView.setChallenge(currentChallenge);
            updateChallengeUI();
        }
    }

    private void updateChallengeUI() {
        submitButton.setVisibility(Button.VISIBLE);
        nextButton.setVisibility(Button.GONE);

        int total = gameSettings.getTotalChallenges();
        challengeTitle.setText(challengeTitle.getText() + " (" + (currentChallengeIndex + 1) + "/" + total + ")");
    }

    private void checkAnswer() {
        String userAnswer = challengeView.getUserAnswer();

        if (currentChallenge != null && currentChallenge.isCorrect(userAnswer)) {
            soundManager.playSuccess();

            completedPiecesCount++;
            currentChallengeIndex++;

            prefs.edit()
                    .putInt("completed_challenges", completedPiecesCount)
                    .putInt("current_challenge", currentChallengeIndex)
                    .apply();

            pictureView.revealPieces(completedPiecesCount);

            String imageName = pictureView.getCurrentName();
            String imageDescription = pictureView.getCurrentDescription();
            String message = "Correct! 🎉\n" + currentChallenge.getExplanation();

            int total = gameSettings.getTotalChallenges();

            if (completedPiecesCount >= total) {
                revealFullImageAndShowCongrats();
            } else {
                if (pictureView.isComplete()) {
                    message += "\n\nImage Complete: " + imageName + "\n" + imageDescription;
                    pictureView.newRandom();
                } else {
                    message += "\n\nPiece revealed! Keep going to see: " + imageName;
                }

                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                submitButton.setVisibility(Button.GONE);
                nextButton.setVisibility(Button.VISIBLE);
                nextButton.setText("➡️ Next Challenge");
            }
        } else {
            soundManager.playError();
            String msg = "Try again! 🤔";
            if (currentChallenge != null && currentChallenge.getHint() != null) {
                msg += "\nHint: " + currentChallenge.getHint();
            }
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }
    }

    private void revealFullImageAndShowCongrats() {
        pictureView.revealPieces(pictureView.getTotalPieces());

        new android.os.Handler().postDelayed(() -> {
            soundManager.playVictory();

            String imageName = pictureView.getCurrentName();
            String imageDescription = pictureView.getCurrentDescription();
            DifficultyLevel difficulty = gameSettings.getDifficulty();

            String msg = "Congratulations!\nYou've completed " + difficulty.getDisplayName() +
                    "!\n\nFinal image: " + imageName + "\n" + imageDescription;

            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

            prefs.edit()
                    .putBoolean("completed_" + difficulty.name().toLowerCase(), true)
                    .putLong("completion_time_" + difficulty.name().toLowerCase(), System.currentTimeMillis())
                    .apply();

            new android.os.Handler().postDelayed(this::finish, 4000);
        }, 1200);
    }

    private void nextChallenge() {
        int total = gameSettings.getTotalChallenges();
        if (currentChallengeIndex >= total) {
            showCompletion();
        } else {
            submitButton.setVisibility(Button.VISIBLE);
            nextButton.setVisibility(Button.GONE);
            loadCurrentChallenge();
        }
    }

    private void showCompletion() {
        revealFullImageAndShowCongrats();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (soundManager != null) soundManager.resumeBackgroundMusic();
        updateChallengeUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (prefs != null){
            prefs.edit()
                    .putInt("current_challenge", currentChallengeIndex)
                    .putInt("completed_challenges", completedPiecesCount)
                    .apply();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundManager != null) soundManager.release();
    }

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
        return pictureView != null ? pictureView.getCurrentName() : "Unknown";
    }

    public boolean isPictureComplete() {
        return pictureView != null && pictureView.isComplete();
    }

    public int getRevealedPieces() {
        return pictureView != null ? pictureView.getRevealedPieces() : 0;
    }

    public void resetProgress() {
        if (prefs != null) {
            prefs.edit()
                    .putInt("current_challenge", 0)
                    .putInt("completed_challenges", 0)
                    .apply();
        }
        if (pictureView != null) {
            pictureView.resetPuzzle();
            pictureView.newRandom();
        }
        currentChallengeIndex = 0;
        completedPiecesCount = 0;
        loadCurrentChallenge();
    }
}
