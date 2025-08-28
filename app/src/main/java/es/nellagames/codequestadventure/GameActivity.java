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

            Toast.makeText(this, "¡Correcto! 🎉 " + currentChallenge.getExplanation(), Toast.LENGTH_LONG).show();

            submitButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.VISIBLE);

            if (completedChallenges >= 10) {
                nextButton.setText("¡Completar Aventura! 🏆");
            }
        } else {
            soundManager.playError();
            Toast.makeText(this, "¡Inténtalo de nuevo! Piensa paso a paso 🤔", Toast.LENGTH_SHORT).show();
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
        // Show completion celebration
        soundManager.playVictory();
        Toast.makeText(this, "¡Felicitaciones! ¡Has completado Code Quest! 🎉🏆", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundManager != null) {
            soundManager.release();
        }
    }
}
