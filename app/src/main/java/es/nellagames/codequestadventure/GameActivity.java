package es.nellagames.codequestadventure;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import es.nellagames.codequestadventure.Challenge;
import es.nellagames.codequestadventure.GameLogic;
import es.nellagames.codequestadventure.SoundManager;
import es.nellagames.codequestadventure.CodeChallengeView;
import es.nellagames.codequestadventure.HiddenPictureView;

public class GameActivity extends AppCompatActivity {

    private TextView challengeTitle, challengeDescription;
    private CodeChallengeView challengeView;
    private HiddenPictureView pictureView;
    private Button submitButton, nextButton;

    private GameLogic gameLogic;
    private SoundManager soundManager;
    private SharedPreferences prefs;
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

        nextButton.setVisibility(View.GONE);
    }

    private void setupGame() {
        gameLogic = new GameLogic();
        soundManager = new SoundManager(this);
        prefs = getSharedPreferences("CodeQuest", MODE_PRIVATE);

        currentChallengeIndex = prefs.getInt("current_challenge", 0);

        // ✅ DO NOT start music here - already running from MainActivity
        // ✅ Only setup button listeners

        submitButton.setOnClickListener(v -> checkAnswer());
        nextButton.setOnClickListener(v -> nextChallenge());
    }

    private void loadCurrentChallenge() {
        currentChallenge = gameLogic.getChallenge(currentChallengeIndex);

        challengeTitle.setText(currentChallenge.getTitle());
        challengeDescription.setText(currentChallenge.getDescription());
        challengeView.setChallenge(currentChallenge);

        // Update picture view with current progress
        int completedChallenges = prefs.getInt("completed_challenges", 0);
        pictureView.revealPieces(completedChallenges);
    }

    private void checkAnswer() {
        String userAnswer = challengeView.getUserAnswer();

        if (currentChallenge.isCorrect(userAnswer)) {
            // Correct answer!
            soundManager.playSuccess();

            // Update progress
            int completedChallenges = prefs.getInt("completed_challenges", 0);
            completedChallenges++;
            prefs.edit()
                    .putInt("completed_challenges", completedChallenges)
                    .putInt("current_challenge", currentChallengeIndex + 1)
                    .apply();

            // Reveal picture piece
            pictureView.revealNextPiece();

            Toast.makeText(this, "Correct! 🎉 " + currentChallenge.getExplanation(), Toast.LENGTH_LONG).show();

            submitButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.VISIBLE);

            if (completedChallenges >= 10) {
                nextButton.setText("Complete Adventure! 🏆");
            }
        } else {
            // Incorrect answer
            soundManager.playError();
            Toast.makeText(this, "Try again! Think step by step 🤔", Toast.LENGTH_SHORT).show();
        }
    }

    private void nextChallenge() {
        currentChallengeIndex++;

        if (currentChallengeIndex >= 10) {
            // Game completed!
            showCompletionScreen();
        } else {
            submitButton.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.GONE);
            loadCurrentChallenge();
        }
    }

    private void showCompletionScreen() {
        // Play victory sound
        soundManager.playVictory();
        Toast.makeText(this, "Congratulations! You have completed Code Quest! 🎉🏆", Toast.LENGTH_LONG).show();

        // Optional: play game over sound after victory
        new android.os.Handler().postDelayed(() -> {
            soundManager.playGameOver();
        }, 2000); // 2 seconds later

        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // ✅ Music continues automatically because it's a service
        // ✅ DO NOT call resumeBackgroundMusic() here
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundManager != null) {
            soundManager.release();
        }
        // ✅ DO NOT stop music here so it continues in MainActivity
    }
}
