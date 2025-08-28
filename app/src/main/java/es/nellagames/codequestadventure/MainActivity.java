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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
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

    private void setupListeners() {
        startButton.setOnClickListener(v -> {
            // Reset progress for new game
            prefs.edit().clear().apply();
            startGame();
        });

        continueButton.setOnClickListener(v -> startGame());
    }

    private void updateProgress() {
        int completedChallenges = prefs.getInt("completed_challenges", 0);
        int totalChallenges = 10;

        progressBar.setMax(totalChallenges);
        progressBar.setProgress(completedChallenges);
        progressText.setText("Progress: " + completedChallenges + "/" + totalChallenges + " challenges completed");

        continueButton.setVisibility(completedChallenges > 0 ? View.VISIBLE : View.GONE);
    }

    private void startGame() {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }
}
