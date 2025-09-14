package es.nellagames.codequestadventure;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    private TextView challengeTitle, challengeDescription, picturePiecesMessage, scoreStreakView;
    private CodeChallengeView challengeView;
    private HiddenPictureView pictureView;
    private Button submitButton, nextButton, backToMenuButton;

    private long currentUserId = -1;
    private String currentUsername = "";

    private GameLogic gameLogic;
    private SoundManager soundManager;
    private GameSettings gameSettings;
    private SharedPreferences prefs;

    private int currentChallengeIndex = 0;
    private int completedPiecesCount = 0;
    private int score = 0;
    private int streak = 0;

    private static final int TOTAL_CHALLENGES = 10;

    // 🔹 Achievements
    private AchievementsManager achievementsManager;
    private int correctStreak = 0;
    private int gamesPlayed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        prefs = getSharedPreferences("CodeQuest", MODE_PRIVATE);
        currentUserId = prefs.getLong("current_user_id", -1);
        currentUsername = prefs.getString("current_username", "Guest");

        // Correct instantiation:
        achievementsManager = new AchievementsManager(this, currentUserId);

        loadSavedProgress();
        initializeViews();
        setupGame();
        loadCurrentChallenge();
        setupBackListener();
        updateScoreStreakUI();

        gamesPlayed++;

        // Logro: Primer inicio de sesión
        if (achievementsManager.unlockAchievement(
                AchievementsManager.ACH_FIRST_LOGIN,
                "First Login"
        )) {
            showAchievementUnlockedDialog("Achievement Unlocked! 🎉", "You logged in for the first time.");
        }

        // Logro: 5 partidas jugadas
        if (gamesPlayed == 5) {
            if (achievementsManager.unlockAchievement(
                    AchievementsManager.ACH_FIVE_GAMES_PLAYED,
                    "Played 5 Games"
            )) {
                showAchievementUnlockedDialog("Achievement Unlocked! 🎉", "You played 5 games. Great job!");
            }
        }
    }

    private void loadSavedProgress() {
        score = prefs.getInt("score", 0);
        streak = prefs.getInt("streak", 0);
        completedPiecesCount = prefs.getInt("completed_pieces", 0);
        currentChallengeIndex = prefs.getInt("current_challenge", 0);
        gamesPlayed = prefs.getInt("games_played", 0);
    }

    private void initializeViews() {
        challengeTitle = findViewById(R.id.challengeTitle);
        challengeDescription = findViewById(R.id.challengeDescription);
        challengeView = findViewById(R.id.challengeView);
        pictureView = findViewById(R.id.pictureView);
        submitButton = findViewById(R.id.submitButton);
        nextButton = findViewById(R.id.nextButton);
        backToMenuButton = findViewById(R.id.backToMenuButton);
        scoreStreakView = findViewById(R.id.scoreStreakView);
        picturePiecesMessage = findViewById(R.id.picturePiecesMessage);

        nextButton.setVisibility(Button.GONE);
        submitButton.setOnClickListener(v -> checkAnswer());
        nextButton.setOnClickListener(v -> nextChallenge());
    }

    private void setupBackListener() {
        backToMenuButton.setOnClickListener(v -> {
            saveScoreToLeaderboard();
            prefs.edit().putInt("games_played", gamesPlayed).apply();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void setupGame() {
        gameSettings = new GameSettings(this);

        String diff = getIntent().getStringExtra("difficulty");
        if (diff != null) {
            gameSettings.setDifficulty(DifficultyLevel.fromString(diff));
        }

        gameLogic = new GameLogic(gameSettings);
        soundManager = new SoundManager(this);

        pictureView.setTotalPieces(TOTAL_CHALLENGES);
        gameSettings.setTotalChallenges(TOTAL_CHALLENGES);

        pictureView.resetPuzzle();
        pictureView.revealPieces(completedPiecesCount);
        updatePicturePiecesMessageVisibility(completedPiecesCount);
    }

    private void loadCurrentChallenge() {
        Challenge challenge = gameLogic.getChallenge(currentChallengeIndex);
        if (challenge != null) {
            challengeTitle.setText(challenge.getTitle());
            challengeDescription.setText(challenge.getDescription());
            challengeView.setChallenge(challenge);
            updateUI();
        }
    }

    private void updateUI() {
        submitButton.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.GONE);

        challengeTitle.setText("Challenge " + (currentChallengeIndex + 1) + " of " + TOTAL_CHALLENGES);
        updateScoreStreakUI();
    }

    private void checkAnswer() {
        String answer = challengeView.getUserAnswer();
        Challenge challenge = gameLogic.getChallenge(currentChallengeIndex);

        if (challenge != null && challenge.isCorrect(answer)) {
            if (soundManager != null) soundManager.playSuccess();

            score += 10;
            streak++;
            prefs.edit().putInt("score", score).putInt("streak", streak).apply();
            updateScoreStreakUI();

            // 🔹 Registrar respuesta correcta y verificar logro de racha
            onAnswerCorrect();

            showCorrectAnswerDialog(challenge.getCorrectExplanation(), () -> {
                completedPiecesCount++;
                currentChallengeIndex++;
                prefs.edit()
                        .putInt("completed_pieces", completedPiecesCount)
                        .putInt("current_challenge", currentChallengeIndex)
                        .apply();

                pictureView.revealPieces(completedPiecesCount);
                updatePicturePiecesMessageVisibility(completedPiecesCount);

                if (completedPiecesCount >= TOTAL_CHALLENGES) {
                    onChallengeCompleted(); // 🔹 Logro por completar reto
                    revealFullImageAndCongrats();
                } else {
                    Toast.makeText(this, "Correct! Piece " + completedPiecesCount + " revealed!", Toast.LENGTH_LONG).show();
                    submitButton.setVisibility(View.GONE);
                    nextButton.setVisibility(View.VISIBLE);
                    nextButton.setText("Next Challenge");
                }
            });
        } else {
            if (soundManager != null) soundManager.playError();
            streak = 0;
            prefs.edit().putInt("streak", streak).apply();
            updateScoreStreakUI();

            String hint = (challenge != null) ? challenge.getHint() : "Try again!";
            showHintDialog(hint);
        }
    }

    private void onAnswerCorrect() {
        correctStreak++;
        if (correctStreak == 3) {
            if (achievementsManager.unlockAchievement(
                    AchievementsManager.ACH_THREE_CORRECT_STREAK,
                    "3 Correct Answers in a Row"
            )) {
                showAchievementUnlockedDialog("Achievement Unlocked! 🎉", "You got 3 correct answers in a row!");
            }
            correctStreak = 0; // Reset the streak for the next
        }
    }

    private void onChallengeCompleted() {
        if (achievementsManager.unlockAchievement(
                AchievementsManager.ACH_FIRST_CHALLENGE,
                "First Challenge Completed"
        )) {
            showAchievementUnlockedDialog("Achievement Unlocked! 🎉", "You completed your first challenge!");
        }
    }

    // Popup con sonido de victory.mp3 para logros desbloqueados
    private void showAchievementUnlockedDialog(String title, String message) {
        final MediaPlayer victoryPlayer = MediaPlayer.create(this, R.raw.victory);
        if (victoryPlayer != null) {
            victoryPlayer.start();
        }
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    if (victoryPlayer != null) {
                        if (victoryPlayer.isPlaying()) victoryPlayer.stop();
                        victoryPlayer.release();
                    }
                    dialog.dismiss();
                })
                .setCancelable(false)
                .show();
    }

    private void revealFullImageAndCongrats() {
        pictureView.revealPieces(TOTAL_CHALLENGES);
        updatePicturePiecesMessageVisibility(TOTAL_CHALLENGES);

        saveScoreToLeaderboard();

        new android.os.Handler().postDelayed(() -> {
            if (soundManager != null) soundManager.playVictory();
            Toast.makeText(this, "Congratulations! You revealed the complete picture!", Toast.LENGTH_LONG).show();
            prefs.edit().putBoolean("game_completed", true).apply();

            new android.os.Handler().postDelayed(this::finish, 4000);
        }, 1200);
    }

    private void saveScoreToLeaderboard() {
        if (currentUserId != -1 && score > 0) {
            LeaderboardDbHelper dbHelper = new LeaderboardDbHelper(this);
            dbHelper.insertEntry(currentUserId, score, "");
        }
    }

    private void updatePicturePiecesMessageVisibility(int count) {
        picturePiecesMessage.setVisibility(count > 0 ? View.GONE : View.VISIBLE);
    }

    private void updateScoreStreakUI() {
        scoreStreakView.setText("Score: " + score + "   Streak: " + streak);
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

    private void nextChallenge() {
        if (currentChallengeIndex >= TOTAL_CHALLENGES) {
            showCompletion();
        } else {
            submitButton.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.GONE);
            loadCurrentChallenge();
            updateScoreStreakUI();
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
                    .putInt("score", score)
                    .putInt("streak", streak)
                    .putInt("games_played", gamesPlayed)
                    .apply();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundManager != null) soundManager.release();
    }
}
