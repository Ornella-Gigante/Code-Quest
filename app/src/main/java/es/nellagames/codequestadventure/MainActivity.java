package es.nellagames.codequestadventure;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    private Button startButton, continueButton;
    private TextView progressText;
    private ProgressBar progressBar;
    private SharedPreferences prefs;
    private SoundManager soundManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        initializeAudio();
        setupListeners();
        updateProgress();
    }

    private void initializeViews() {
        startButton = findViewById(R.id.startButton);
        continueButton = findViewById(R.id.continueButton);
        progressText = findViewById(R.id.progressText);
        progressBar = findViewById(R.id.progressBar);
        prefs = getSharedPreferences("CodeQuest", MODE_PRIVATE);
    }

    private void initializeAudio() {
        soundManager = new SoundManager(this);
        // Iniciar música de fondo al abrir la app
        soundManager.startBackgroundMusic();
    }

    private void setupListeners() {
        startButton.setOnClickListener(v -> {
            // Sonido de éxito al iniciar
            soundManager.playSuccess();

            // Reset progress for new game
            prefs.edit().clear().apply();
            startGame();
        });

        continueButton.setOnClickListener(v -> {
            soundManager.playSuccess();
            startGame();
        });
    }

    private void updateProgress() {
        int completedChallenges = prefs.getInt("completed_challenges", 0);
        int totalChallenges = 10;

        progressBar.setMax(totalChallenges);
        progressBar.setProgress(completedChallenges);
        progressText.setText("Progreso: " + completedChallenges + "/" + totalChallenges + " desafíos completados");

        continueButton.setVisibility(completedChallenges > 0 ? View.VISIBLE : View.GONE);
    }

    private void startGame() {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reanudar música si regresamos a esta activity
        if (soundManager != null) {
            soundManager.resumeBackgroundMusic();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundManager != null) {
            soundManager.release();
        }
    }
}