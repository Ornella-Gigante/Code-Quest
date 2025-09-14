package es.nellagames.codequestadventure;

import android.app.AlertDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Handler;

public class GameActivity extends AppCompatActivity {

    private TextView challengeTitle, picturePiecesMessage, scoreStreakView;
    private TextView challengeDescription;
    private CodeChallengeView challengeView;
    private HiddenPictureView pictureView;
    private Button submitButton, nextButton, backToMenuButton;

    private long currentUserId = -1;
    private String currentUsername = "";
    private SharedPreferences prefs;

    private int currentChallengeIndex = 0;
    private int completedPiecesCount = 0;
    private int score = 0;
    private int streak = 0;
    private int gamesPlayed = 0;

    private int correctStreak = 0;

    private static final int TOTAL_CHALLENGES = 10;

    private GameLogic gameLogic;
    private SoundManager soundManager;
    private GameSettings gameSettings;

    private AchievementsManager achievementsManager;

    // User-specific preference keys
    private String keyScore() {
        return "score_user_" + currentUserId;
    }

    private String keyStreak() {
        return "streak_user_" + currentUserId;
    }

    private String keyCompletedPieces() {
        return "completed_pieces_user_" + currentUserId;
    }

    private String keyCurrentChallenge() {
        return "current_challenge_user_" + currentUserId;
    }

    private String keyGamesPlayed() {
        return "games_played_user_" + currentUserId;
    }

    private String keyCorrectStreak() {
        return "correct_streak_user_" + currentUserId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);

        prefs = getSharedPreferences("CodeQuest", MODE_PRIVATE);

        // Use consistent key for user ID
        currentUserId = prefs.getLong("current_user_id", -1);
        currentUsername = prefs.getString("current_username", "Guest");

        if (currentUserId == -1) {
            Toast.makeText(this, "Error: No user logged in", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        achievementsManager = new AchievementsManager(this, currentUserId);

        loadSavedProgress();
        initializeViews();
        setupGame();
        loadCurrentChallenge();
        setupBackListener();
        updateScoreStreakUI();

        gamesPlayed++;
        saveProgress(); // Save incremented games played

        // Check first login achievement
        if (achievementsManager.unlockAchievement(AchievementsManager.ACH_FIRST_LOGIN, "First Login")) {
            showAchievementUnlockedDialog("Achievement Unlocked!", "You logged in for the first time.");
        }

        // Check five games played achievement
        if (gamesPlayed >= 5) {
            if (achievementsManager.unlockAchievement(AchievementsManager.ACH_FIVE_GAMES_PLAYED, "Played 5 Games")) {
                showAchievementUnlockedDialog("Achievement Unlocked!", "You played 5 games!");
            }
        }
    }

    private void showAchievementUnlockedDialog(String title, String message) {
        MediaPlayer achievementSound = MediaPlayer.create(this, R.raw.victory);
        if (achievementSound != null) {
            achievementSound.start();
        }

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    if (achievementSound != null) {
                        if (achievementSound.isPlaying()) achievementSound.stop();
                        achievementSound.release();
                    }
                })
                .show();
    }

    private void loadSavedProgress() {
        score = prefs.getInt(keyScore(), 0);
        streak = prefs.getInt(keyStreak(), 0);
        completedPiecesCount = prefs.getInt(keyCompletedPieces(), 0);
        currentChallengeIndex = prefs.getInt(keyCurrentChallenge(), 0);
        gamesPlayed = prefs.getInt(keyGamesPlayed(), 0);
        correctStreak = prefs.getInt(keyCorrectStreak(), 0);
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
            saveProgress();
            saveScoreToLeaderboard();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void setupGame() {
        gameSettings = new GameSettings(this);
        String diff = getIntent().getStringExtra("difficulty");
        if (diff != null)
            gameSettings.setDifficulty(DifficultyLevel.fromString(diff));

        gameLogic = new GameLogic(gameSettings);
        soundManager = new SoundManager(this);

        pictureView.setTotalPieces(TOTAL_CHALLENGES);
        gameSettings.setTotalChallenges(TOTAL_CHALLENGES);
        pictureView.revealPieces(completedPiecesCount);
        pictureView.invalidate();
        updatePicturePiecesMessageVisibility(completedPiecesCount);
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
        challengeTitle.setText("Challenge " + (currentChallengeIndex + 1) + " of " + TOTAL_CHALLENGES);
        submitButton.setVisibility(Button.VISIBLE);
        nextButton.setVisibility(Button.GONE);
        updateScoreStreakUI();
    }

    private void checkAnswer() {
        String answer = challengeView.getUserAnswer();
        var challenge = gameLogic.getChallenge(currentChallengeIndex);

        if (challenge != null && challenge.isCorrect(answer)) {
            if (soundManager != null) soundManager.playSuccess();

            score += 10;
            streak++;
            correctStreak++;
            saveProgress();
            updateScoreStreakUI();

            achievementsManager.checkScoreAchievements(score);
            checkStreakAchievements();

            showCorrectAnswerDialog(challenge.getExplanation(), () -> {
                completedPiecesCount++;
                currentChallengeIndex++;
                saveProgress();

                pictureView.revealPieces(completedPiecesCount);
                updatePicturePiecesMessageVisibility(completedPiecesCount);

                if (completedPiecesCount >= TOTAL_CHALLENGES) {
                    onChallengeCompleted();
                    revealFullImageAndCongrats();
                } else {
                    Toast.makeText(this, "Correct! Piece " + completedPiecesCount + " revealed!", Toast.LENGTH_LONG).show();
                    submitButton.setVisibility(Button.GONE);
                    nextButton.setVisibility(Button.VISIBLE);
                    nextButton.setText("Next");
                }
            });

        } else {
            if (soundManager != null) soundManager.playError();

            streak = 0;
            correctStreak = 0; // Reset correct streak on wrong answer
            saveProgress();
            updateScoreStreakUI();

            String hint = (challenge != null) ? challenge.getHint() : "Try again!";
            showHintDialog(hint);
        }
    }

    private void checkStreakAchievements() {
        if (correctStreak >= 3) {
            if (achievementsManager.unlockAchievement(AchievementsManager.ACH_THREE_CORRECT_STREAK, "3 Correct Answers in a Row")) {
                showAchievementUnlockedDialog("Achievement Unlocked!", "3 correct answers in a row!");
            }
        }
    }

    private void onChallengeCompleted() {
        if (achievementsManager.unlockAchievement(AchievementsManager.ACH_FIRST_CHALLENGE, "First Challenge Completed")) {
            showAchievementUnlockedDialog("Achievement Unlocked!", "You've completed the first challenge!");
        }
    }

    private void revealFullImageAndCongrats() {
        pictureView.revealPieces(TOTAL_CHALLENGES);
        pictureView.invalidate();
        updatePicturePiecesMessageVisibility(TOTAL_CHALLENGES);
        saveScoreToLeaderboard();

        new Handler().postDelayed(() -> {
            if (soundManager != null) soundManager.playVictory();
            Toast.makeText(this, "Complete! Full picture revealed!", Toast.LENGTH_LONG).show();

            // Mark game as completed for this user
            prefs.edit().putBoolean("game_completed_user_" + currentUserId, true).apply();

            new Handler().postDelayed(this::finish, 4000);
        }, 1200);
    }

    private void saveScoreToLeaderboard() {
        if (currentUserId != -1 && score > 0) {
            LeaderboardDbHelper dbHelper = new LeaderboardDbHelper(this);
            dbHelper.insertEntry(currentUserId, score, "");
            dbHelper.close();
        }
    }

    private void updatePicturePiecesMessageVisibility(int count) {
        picturePiecesMessage.setVisibility(count > 0 ? Button.GONE : Button.VISIBLE);
    }

    private void updateScoreStreakUI() {
        scoreStreakView.setText("Score: " + score + "   Streak: " + streak);
    }

    private void showCorrectAnswerDialog(String message, Runnable onOk) {
        new AlertDialog.Builder(this)
                .setTitle("Correct!")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Continue", (d, w) -> {
                    d.dismiss();
                    if (onOk != null) onOk.run();
                })
                .show();
    }

    private void showHintDialog(String hint) {
        new AlertDialog.Builder(this)
                .setTitle("Hint")
                .setMessage(hint)
                .setCancelable(true)
                .setPositiveButton("Try again", (d, w) -> d.dismiss())
                .show();
    }

    private void saveProgress() {
        prefs.edit()
                .putInt(keyScore(), score)
                .putInt(keyStreak(), streak)
                .putInt(keyCompletedPieces(), completedPiecesCount)
                .putInt(keyCurrentChallenge(), currentChallengeIndex)
                .putInt(keyGamesPlayed(), gamesPlayed)
                .putInt(keyCorrectStreak(), correctStreak)
                .apply();
    }

    private void nextChallenge() {
        if (currentChallengeIndex >= TOTAL_CHALLENGES) {
            revealFullImageAndCongrats();
        } else {
            submitButton.setVisibility(Button.VISIBLE);
            nextButton.setVisibility(Button.GONE);
            loadCurrentChallenge();
            updateScoreStreakUI();
        }
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
        saveProgress();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundManager != null)
            soundManager.release();
    }
}