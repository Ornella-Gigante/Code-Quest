package es.nellagames.codequestadventure;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;

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

    private int currentChallengeIndex = 0;
    private int completedPiecesCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        initializeViews();
        setupGame();
        loadCurrentChallenge();
        setupBackListener();
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

    private void setupBackListener() {
        if (backToMenuButton != null) {
            backToMenuButton.setOnClickListener(v -> {
                if (soundManager != null) soundManager.playSuccess();
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }
    }

    private void setupGame() {
        gameSettings = new GameSettings(this);
        String diff = getIntent().getStringExtra("difficulty");
        if (diff != null) {
            gameSettings.setDifficulty(DifficultyLevel.fromString(diff));
        }

        gameLogic = new GameLogic(gameSettings);
        soundManager = new SoundManager(this);
        prefs = getSharedPreferences("CodeQuest", MODE_PRIVATE);

        // Set the total challenges equal to number of pieces
        int totalPieces = pictureView.getTotalPieces();
        gameSettings.setTotalChallenges(totalPieces);

        currentChallengeIndex = prefs.getInt("current_challenge", 0);
        completedPiecesCount = prefs.getInt("completed_pieces", 0);
        pictureView.revealPieces(completedPiecesCount);
    }

    private void loadCurrentChallenge() {
        var challenge = gameLogic.getChallenge(currentChallengeIndex);
        if (challenge != null) {
            challengeTitle.setText(challenge.getTitle());
            challengeDescription.setText(challenge.getDescription());
            challengeView.setChallenge(challenge);
            updateUI();
        }
    }

    private void updateUI() {
        submitButton.setVisibility(Button.VISIBLE);
        nextButton.setVisibility(Button.GONE);

        int total = gameSettings.getTotalChallenges();
        challengeTitle.setText("Challenge " + (currentChallengeIndex + 1) + " of " + total);
    }

    private void checkAnswer() {
        String answer = challengeView.getUserAnswer();
        var challenge = gameLogic.getChallenge(currentChallengeIndex);

        if (challenge != null && challenge.isCorrect(answer)) {
            soundManager.playSuccess();
            completedPiecesCount++;
            currentChallengeIndex++;

            prefs.edit()
                    .putInt("completed_pieces", completedPiecesCount)
                    .putInt("current_challenge", currentChallengeIndex)
                    .apply();

            pictureView.revealPieces(completedPiecesCount);

            if (completedPiecesCount >= gameSettings.getTotalChallenges()) {
                revealFullImageAndCongrats();
            } else {
                Toast.makeText(this, "Correct! Keep going!", Toast.LENGTH_LONG).show();
                submitButton.setVisibility(Button.GONE);
                nextButton.setVisibility(Button.VISIBLE);
                nextButton.setText("Next");
            }
        } else {
            soundManager.playError();
            Toast.makeText(this, "Try again!", Toast.LENGTH_SHORT).show();
        }
    }

    private void revealFullImageAndCongrats() {
        pictureView.revealPieces(pictureView.getTotalPieces());
        new android.os.Handler().postDelayed(() -> {
            soundManager.playVictory();
            Toast.makeText(this, "Congratulations! You completed the game!", Toast.LENGTH_LONG).show();
            prefs.edit().putBoolean("game_completed", true).apply();
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
        revealFullImageAndCongrats();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (soundManager != null) soundManager.resumeBackgroundMusic();
        updateUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (prefs != null) {
            prefs.edit()
                    .putInt("current_challenge", currentChallengeIndex)
                    .putInt("completed_pieces", completedPiecesCount)
                    .apply();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundManager != null) soundManager.release();
    }
}
