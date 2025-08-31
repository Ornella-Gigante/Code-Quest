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
    private boolean isNewGame = false;

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

        // Verificar si se quiere empezar un juego nuevo
        isNewGame = getIntent().getBooleanExtra("new_game", false);

        String diff = getIntent().getStringExtra("difficulty");
        if (diff != null) {
            gameSettings.setDifficulty(DifficultyLevel.fromString(diff));
        }

        gameLogic = new GameLogic(gameSettings);
        soundManager = new SoundManager(this);

        // Configurar número de piezas basado en la dificultad
        int totalPieces = gameSettings.getPuzzlePieces();
        pictureView.setTotalPieces(totalPieces);
        gameSettings.setTotalChallenges(totalPieces);

        // Si es un juego nuevo, resetear todo el progreso
        if (isNewGame) {
            completedPiecesCount = 0;
            currentChallengeIndex = 0;
            prefs.edit()
                    .putInt("completed_pieces", 0)
                    .putInt("current_challenge", 0)
                    .putBoolean("game_completed", false)
                    .apply();
            pictureView.resetPuzzle(); // Resetear la imagen también
        } else {
            // Cargar progreso existente solo si no es juego nuevo
            completedPiecesCount = prefs.getInt("completed_pieces", 0);
            currentChallengeIndex = prefs.getInt("current_challenge", 0);

            // Validar que el progreso no exceda los límites actuales
            if (completedPiecesCount > totalPieces) {
                completedPiecesCount = 0;
                currentChallengeIndex = 0;
            }
            if (currentChallengeIndex >= gameLogic.getTotalChallenges()) {
                currentChallengeIndex = 0;
            }
        }

        // Aplicar el progreso a la imagen (0 si es juego nuevo)
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

            // Solo incrementar si aún no hemos completado este desafío
            if (currentChallengeIndex >= completedPiecesCount) {
                completedPiecesCount++;
            }
            currentChallengeIndex++;

            // Guardar progreso
            prefs.edit()
                    .putInt("completed_pieces", completedPiecesCount)
                    .putInt("current_challenge", currentChallengeIndex)
                    .apply();

            // Revelar una pieza más de la imagen
            pictureView.revealPieces(completedPiecesCount);

            if (completedPiecesCount >= gameSettings.getTotalChallenges()) {
                revealFullImageAndCongrats();
            } else {
                Toast.makeText(this, "Correct! Piece " + completedPiecesCount + " revealed!", Toast.LENGTH_LONG).show();
                submitButton.setVisibility(Button.GONE);
                nextButton.setVisibility(Button.VISIBLE);
                nextButton.setText("Next Challenge");
            }
        } else {
            soundManager.playError();
            Toast.makeText(this, "Try again! The image remains hidden.", Toast.LENGTH_SHORT).show();
        }
    }

    private void revealFullImageAndCongrats() {
        // Asegurar que toda la imagen esté revelada
        pictureView.revealPieces(pictureView.getTotalPieces());

        new android.os.Handler().postDelayed(() -> {
            soundManager.playVictory();
            Toast.makeText(this, "Congratulations! You revealed the complete picture!", Toast.LENGTH_LONG).show();
            prefs.edit().putBoolean("game_completed", true).apply();

            // Volver al menú después de mostrar la imagen completa
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
        if (prefs != null && !isNewGame) {
            // Solo guardar progreso si no es un juego nuevo
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