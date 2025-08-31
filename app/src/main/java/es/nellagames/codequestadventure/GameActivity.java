package es.nellagames.codequestadventure;

import android.app.AlertDialog;
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
        prefs = getSharedPreferences("CodeQuest", MODE_PRIVATE);

        String diff = getIntent().getStringExtra("difficulty");
        if (diff != null) {
            gameSettings.setDifficulty(DifficultyLevel.fromString(diff));
        }

        gameLogic = new GameLogic(gameSettings);
        soundManager = new SoundManager(this);

        // SIEMPRE 10 piezas para 10 desafíos
        int totalPieces = 10;
        pictureView.setTotalPieces(totalPieces);
        gameSettings.setTotalChallenges(totalPieces);

        // Reiniciar progreso
        completedPiecesCount = 0;
        currentChallengeIndex = 0;
        prefs.edit()
                .putInt("completed_pieces", 0)
                .putInt("current_challenge", 0)
                .putBoolean("game_completed", false)
                .apply();

        // Resetear imagen
        pictureView.resetPuzzle();
        pictureView.revealPieces(0);
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

            showCorrectAnswerDialog(challenge.getCorrectExplanation(), () -> {
                completedPiecesCount++;
                currentChallengeIndex++;

                prefs.edit()
                        .putInt("completed_pieces", completedPiecesCount)
                        .putInt("current_challenge", currentChallengeIndex)
                        .apply();

                pictureView.revealPieces(completedPiecesCount);

                if (completedPiecesCount >= 10) {
                    revealFullImageAndCongrats();
                } else {
                    Toast.makeText(this, "Correct! Piece " + completedPiecesCount + " revealed!", Toast.LENGTH_LONG).show();
                    submitButton.setVisibility(Button.GONE);
                    nextButton.setVisibility(Button.VISIBLE);
                    nextButton.setText("Next Challenge");
                }
            });
        } else {
            soundManager.playError();

            String hint = (challenge != null) ? challenge.getHint() : "Try again!";
            showHintDialog(hint);
        }
    }

    private void showCorrectAnswerDialog(String explanation, Runnable onOk) {
        new AlertDialog.Builder(this)
                .setTitle("Correct! 🎉")
                .setMessage(explanation)
                .setPositiveButton("Continue", (dialog, which) -> {
                    dialog.dismiss();
                    if (onOk != null) onOk.run();
                })
                .setCancelable(false)
                .show();
    }

    private void showHintDialog(String hint) {
        new AlertDialog.Builder(this)
                .setTitle("Hint 💡")
                .setMessage(hint)
                .setPositiveButton("Try Again", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }

    private void revealFullImageAndCongrats() {
        pictureView.revealPieces(10);

        new android.os.Handler().postDelayed(() -> {
            soundManager.playVictory();
            Toast.makeText(this, "Congratulations! You revealed the complete picture!", Toast.LENGTH_LONG).show();
            prefs.edit().putBoolean("game_completed", true).apply();

            new android.os.Handler().postDelayed(this::finish, 4000);
        }, 1200);
    }

    private void nextChallenge() {
        if (currentChallengeIndex >= 10) {
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
        if (soundManager != null) soundManager.pauseBackgroundMusic();

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
